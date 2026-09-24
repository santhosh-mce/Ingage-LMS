package com.lms.Backend.user.controller;

import com.lms.Backend.admin.service.FileUploadService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L; // 5 MB
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/webp"
    );

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    public UserController(UserRepository userRepository, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Get the currently authenticated user's profile.
     * Route resolves to: GET /api/users/profile or GET /api/users/me
     */
    @GetMapping({"/profile", "/me"})
    public ResponseEntity<?> currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Unauthorized: Valid authentication session required."
            ));
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "User not found."
            ));
        }

        String rawImage = user.getProfileImage();
        String accessibleImage = rawImage;
        if (accessibleImage != null && accessibleImage.startsWith("/uploads/")) {
            accessibleImage = "/api" + accessibleImage;
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("message", "Authenticated user profile");
        res.put("userId", user.getId().toString());
        res.put("name", user.getName());
        res.put("email", user.getEmail());
        res.put("role", user.getRole().name());
        res.put("profileImage", accessibleImage);
        res.put("avatar", accessibleImage);
        res.put("avatarUrl", accessibleImage);
        return ResponseEntity.ok(res);
    }

    /**
     * Upload and update the profile image for the currently authenticated user.
     * Accessible via:
     * - POST /api/users/profile/image (using 'image' or 'file' multipart parameter)
     * - POST /api/users/me/avatar
     */
    @PostMapping(
        value = {"/profile/image", "/me/avatar", "/avatar"},
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadProfileImage(
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "file", required = false) MultipartFile file,
        Authentication authentication,
        HttpServletRequest request
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Unauthorized: Valid authentication required to upload profile image."
            ));
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "User not found."
            ));
        }

        // 1. Resolve uploaded file from 'image' or 'file' field
        MultipartFile uploadFile = (image != null && !image.isEmpty()) ? image : file;
        if (uploadFile == null || uploadFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Please select an image file to upload."
            ));
        }

        // 2. Validate file size (max 5MB)
        if (uploadFile.getSize() > MAX_FILE_SIZE_BYTES) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "File size exceeds the 5 MB limit."
            ));
        }

        // 3. Validate MIME type
        String contentType = uploadFile.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid image format. Supported formats: JPG, JPEG, PNG, WEBP."
            ));
        }

        // 4. Validate file extension and prevent path traversal
        String originalFilename = uploadFile.getOriginalFilename();
        if (originalFilename != null) {
            if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid filename: path traversal characters are not allowed."
                ));
            }
            String lower = originalFilename.toLowerCase();
            if (!lower.endsWith(".jpg") && !lower.endsWith(".jpeg") && !lower.endsWith(".png") && !lower.endsWith(".webp")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid file extension. Only .jpg, .jpeg, .png, and .webp are allowed."
                ));
            }
        }

        // 1. Read old image path from PostgreSQL
        String previousImage = user.getProfileImage();
        String userPrefix = "user-" + (user.getId() != null ? user.getId().toString().substring(0, Math.min(8, user.getId().toString().length())) : "default");

        String newlySavedFileUrl = null;
        try {
            // 3. Save NEW image with unique/safe filename in backend storage
            newlySavedFileUrl = fileUploadService.storeFile(uploadFile, "profile-images", userPrefix);

            // 4. Update user entity in PostgreSQL with NEW image path
            user.setProfileImage(newlySavedFileUrl);

            // 5. Confirm database update succeeded
            user = userRepository.save(user);

            // 6. Delete OLD uploaded image from backend storage (ONLY after database save succeeded!)
            if (previousImage != null && !previousImage.isBlank() && !previousImage.equals(newlySavedFileUrl)) {
                fileUploadService.deleteOldProfileImage(previousImage, userPrefix);
            }

            log.info("[ProfileImageUpload] Successfully updated profile image for user: {} (url: {})",
                user.getEmail(), newlySavedFileUrl);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Profile image uploaded successfully");
            response.put("profileImage", newlySavedFileUrl);
            response.put("avatarUrl", newlySavedFileUrl);
            response.put("user", Map.of(
                "userId", user.getId().toString(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "profileImage", newlySavedFileUrl,
                "avatarUrl", newlySavedFileUrl
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[ProfileImageUpload] Failed to replace profile image for user: {}", user.getEmail(), e);
            // Rollback: Keep old image, clean up newly created file if partially saved
            if (newlySavedFileUrl != null) {
                try {
                    fileUploadService.deleteOldProfileImage(newlySavedFileUrl, userPrefix);
                } catch (Exception cleanupEx) {
                    log.warn("[ProfileImageUpload] Failed to clean up newly saved file after failure: {}", newlySavedFileUrl, cleanupEx);
                }
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to update profile image: " + e.getMessage()
            ));
        }
    }

    /**
     * Remove the current user's profile image.
     * Route resolves to: DELETE /api/users/profile/image or DELETE /api/users/me/avatar
     */
    @DeleteMapping({"/profile/image", "/me/avatar", "/avatar"})
    public ResponseEntity<?> removeProfileImage(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Unauthorized: Valid authentication required."
            ));
        }

        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "User not found."
            ));
        }

        String previousImage = user.getProfileImage();
        if (previousImage != null && !previousImage.isBlank()) {
            String userPrefix = "user-" + (user.getId() != null ? user.getId().toString().substring(0, Math.min(8, user.getId().toString().length())) : "default");
            fileUploadService.deleteOldProfileImage(previousImage, userPrefix);
        }

        user.setProfileImage(null);
        userRepository.save(user);

        log.info("[ProfileImageUpload] Removed profile image for user: {}", user.getEmail());

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Profile image removed successfully",
            "profileImage", "",
            "avatarUrl", ""
        ));
    }
}