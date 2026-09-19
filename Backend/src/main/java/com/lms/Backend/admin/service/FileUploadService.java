package com.lms.Backend.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);
    private final Path rootLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileUploadService() {
        try {
            Files.createDirectories(rootLocation.resolve("videos"));
            Files.createDirectories(rootLocation.resolve("thumbnails"));
            Files.createDirectories(rootLocation.resolve("documents"));
            Files.createDirectories(rootLocation.resolve("avatars"));
            Files.createDirectories(rootLocation.resolve("profile-images"));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directories", e);
        }
    }

    public String storeFile(MultipartFile file, String category) throws IOException {
        return storeFile(file, category, null);
    }

    public String storeFile(MultipartFile file, String category, String prefix) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file.");
        }

        String subDir = "documents";
        if ("video".equalsIgnoreCase(category) || "videos".equalsIgnoreCase(category)) {
            subDir = "videos";
        } else if ("thumbnail".equalsIgnoreCase(category) || "thumbnails".equalsIgnoreCase(category) || "images".equalsIgnoreCase(category)) {
            subDir = "thumbnails";
        } else if ("profile-image".equalsIgnoreCase(category) || "profile-images".equalsIgnoreCase(category)) {
            subDir = "profile-images";
        } else if ("avatar".equalsIgnoreCase(category) || "avatars".equalsIgnoreCase(category) || "profile".equalsIgnoreCase(category)) {
            subDir = "profile-images";
        }

        Path destinationFolder = rootLocation.resolve(subDir);
        Files.createDirectories(destinationFolder);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\"))) {
            throw new SecurityException("Filename contains invalid characters.");
        }
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String safePrefix = (prefix != null && !prefix.isBlank())
            ? prefix.replaceAll("[^a-zA-Z0-9_-]", "") + "-"
            : "";
        String uniqueFilename = safePrefix + UUID.randomUUID().toString().replace("-", "") + extension;
        Path destinationPath = destinationFolder.resolve(uniqueFilename).normalize();

        // Prevent path traversal
        if (!destinationPath.startsWith(destinationFolder)) {
            throw new SecurityException("Cannot store file outside current directory.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("[FileUploadService] Stored file in {}/{}", subDir, uniqueFilename);

        // Returns relative URL accessible from browser via Tomcat context-path /api
        return "/uploads/" + subDir + "/" + uniqueFilename;
    }

    /**
     * Determines whether the given URL or path is a safe, locally uploaded user profile image.
     * Rejects:
     * - Null / empty paths
     * - External URLs (http://, https://, protocol-relative //)
     * - Google profile photos (lh3.googleusercontent.com, etc.)
     * - Default avatars or shared static assets
     * - Paths attempting path traversal (..)
     * - Files outside uploads/profile-images/ or uploads/avatars/
     * - Files not matching the user's prefix (if specified)
     */
    public boolean isManagedProfileImage(String fileUrl, String expectedUserPrefix) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        String cleanUrl = fileUrl.trim();

        // 1. Reject external URLs (Google avatars, OAuth pictures, external CDNs)
        String lower = cleanUrl.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("//") || lower.startsWith("data:") || lower.startsWith("blob:")) {
            log.debug("[FileUploadService] Skipping deletion of external URL: {}", cleanUrl);
            return false;
        }
        if (lower.contains("googleusercontent.com") || lower.contains("gravatar.com")) {
            return false;
        }

        // 2. Reject default profile images or static assets
        if (lower.contains("default") || lower.contains("profile-avatar") || lower.contains("avatar.png") ||
            lower.contains("/assets/") || lower.contains("/static/") || lower.contains("/resources/")) {
            log.debug("[FileUploadService] Skipping deletion of static/default asset: {}", cleanUrl);
            return false;
        }

        // 3. Reject path traversal characters
        if (cleanUrl.contains("..") || cleanUrl.contains("\0")) {
            log.warn("[FileUploadService] Path traversal attempt in file deletion: {}", fileUrl);
            return false;
        }

        // 4. Strip leading /api or /
        if (cleanUrl.startsWith("/api/")) {
            cleanUrl = cleanUrl.substring(4);
        }
        if (cleanUrl.startsWith("/")) {
            cleanUrl = cleanUrl.substring(1);
        }

        // 5. Must strictly reside in uploads/profile-images/ or uploads/avatars/
        if (!cleanUrl.startsWith("uploads/profile-images/") && !cleanUrl.startsWith("uploads/avatars/")) {
            log.warn("[FileUploadService] Attempted deletion outside allowed upload directories: {}", fileUrl);
            return false;
        }

        try {
            String fileName = Paths.get(cleanUrl).getFileName().toString();
            Path destinationFolder = cleanUrl.contains("avatars")
                ? rootLocation.resolve("avatars").normalize()
                : rootLocation.resolve("profile-images").normalize();
            Path filePath = destinationFolder.resolve(fileName).normalize();

            // Must reside within allowed destination directory
            if (!filePath.startsWith(destinationFolder)) {
                log.warn("[FileUploadService] Resolved path outside allowed upload directory: {}", filePath);
                return false;
            }

            // 6. User ownership check: if expectedUserPrefix is provided, verify filename match
            if (expectedUserPrefix != null && !expectedUserPrefix.isBlank()) {
                if (!fileName.startsWith(expectedUserPrefix) && !fileName.startsWith("user-")) {
                    log.warn("[FileUploadService] File '{}' does not match user prefix '{}'. Skipping.", fileName, expectedUserPrefix);
                    return false;
                }
            }

            return Files.exists(filePath) && Files.isRegularFile(filePath);
        } catch (Exception e) {
            log.warn("[FileUploadService] Error verifying profile image '{}': {}", fileUrl, e.getMessage());
            return false;
        }
    }

    public boolean deleteOldProfileImage(String fileUrl, String expectedUserPrefix) {
        if (!isManagedProfileImage(fileUrl, expectedUserPrefix)) {
            log.info("[FileUploadService] Skipped deletion (not a managed user uploaded image): {}", fileUrl);
            return false;
        }

        try {
            String cleanUrl = fileUrl.trim();
            if (cleanUrl.startsWith("/api/")) {
                cleanUrl = cleanUrl.substring(4);
            }
            if (cleanUrl.startsWith("/")) {
                cleanUrl = cleanUrl.substring(1);
            }

            String fileName = Paths.get(cleanUrl).getFileName().toString();
            Path destinationFolder = cleanUrl.contains("avatars")
                ? rootLocation.resolve("avatars").normalize()
                : rootLocation.resolve("profile-images").normalize();
            Path filePath = destinationFolder.resolve(fileName).normalize();

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                boolean deleted = Files.deleteIfExists(filePath);
                log.info("[FileUploadService] Successfully deleted old profile image: {} (success: {})", filePath, deleted);
                return deleted;
            }
        } catch (Exception e) {
            log.warn("[FileUploadService] Could not delete old file '{}': {}", fileUrl, e.getMessage());
        }
        return false;
    }

    public boolean deleteFileByUrl(String fileUrl) {
        return deleteOldProfileImage(fileUrl, null);
    }

    public Path loadFileAsPath(String subDir, String filename) {
        return rootLocation.resolve(subDir).resolve(filename).normalize();
    }
}
