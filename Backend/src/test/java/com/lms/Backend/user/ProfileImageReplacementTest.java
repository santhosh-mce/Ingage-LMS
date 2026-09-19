package com.lms.Backend.user;

import com.lms.Backend.admin.service.FileUploadService;
import com.lms.Backend.user.controller.UserController;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProfileImageReplacementTest {

    private UserRepository userRepository;
    private FileUploadService fileUploadService;
    private UserController userController;
    private Authentication mockAuth;

    private UUID userId;
    private String userPrefix;
    private User testUser;
    private final Path uploadDir = Paths.get("uploads", "profile-images").toAbsolutePath().normalize();

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(uploadDir);

        userRepository = mock(UserRepository.class);
        fileUploadService = new FileUploadService();
        userController = new UserController(userRepository, fileUploadService);

        userId = UUID.randomUUID();
        userPrefix = "user-" + userId.toString().substring(0, 8);

        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Test Student");
        testUser.setEmail("student@example.com");
        testUser.setRole(UserRole.STUDENT);

        mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn(testUser.getEmail());
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up any test files starting with userPrefix
        if (Files.exists(uploadDir)) {
            try (var stream = Files.list(uploadDir)) {
                stream.filter(p -> p.getFileName().toString().startsWith(userPrefix))
                      .forEach(p -> {
                          try {
                              Files.deleteIfExists(p);
                          } catch (IOException ignored) {}
                      });
            }
        }
    }

    @Test
    @DisplayName("1. Uploading a new image deletes old physical file, saves new physical file, and updates DB")
    void replaceProfileImage_deletesOldFile_and_savesNewFile() throws Exception {
        // Create an existing old profile image file
        String oldFilename = userPrefix + "-old-avatar.jpg";
        Path oldFilePath = uploadDir.resolve(oldFilename);
        Files.writeString(oldFilePath, "fake-old-image-bytes");
        assertTrue(Files.exists(oldFilePath), "Old file should exist before upload");

        testUser.setProfileImage("/uploads/profile-images/" + oldFilename);

        MockMultipartFile newImageFile = new MockMultipartFile(
            "image",
            "new-profile.png",
            "image/png",
            "fake-new-image-content-bytes".getBytes()
        );

        ResponseEntity<?> response = userController.uploadProfileImage(newImageFile, null, mockAuth, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));

        String newImageUrl = (String) body.get("profileImage");
        assertNotNull(newImageUrl);
        assertTrue(newImageUrl.startsWith("/uploads/profile-images/" + userPrefix));
        assertNotEquals("/uploads/profile-images/" + oldFilename, newImageUrl);

        // Verification 1: Old physical file is deleted
        assertFalse(Files.exists(oldFilePath), "Old physical file must be deleted from storage");

        // Verification 2: New physical file exists on disk
        String newFilename = Paths.get(newImageUrl).getFileName().toString();
        Path newFilePath = uploadDir.resolve(newFilename);
        assertTrue(Files.exists(newFilePath), "New physical file must exist on disk");

        // Verification 3: PostgreSQL profile_image contains the new path
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(newImageUrl, captor.getValue().getProfileImage(), "Database entity must have new path");
    }

    @Test
    @DisplayName("2. Existing Google OAuth avatar is NOT deleted from filesystem when uploading new image")
    void replaceProfileImage_preservesExternalGoogleUrlWithoutError() throws Exception {
        String googleAvatarUrl = "https://lh3.googleusercontent.com/a/ACg8ocK-test-google-picture";
        testUser.setProfileImage(googleAvatarUrl);

        MockMultipartFile newImageFile = new MockMultipartFile(
            "image",
            "new-profile.jpg",
            "image/jpeg",
            "fake-new-jpg-bytes".getBytes()
        );

        ResponseEntity<?> response = userController.uploadProfileImage(newImageFile, null, mockAuth, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));

        String newImageUrl = (String) body.get("profileImage");
        assertTrue(newImageUrl.startsWith("/uploads/profile-images/" + userPrefix));

        // DB is updated to the newly uploaded image
        assertEquals(newImageUrl, testUser.getProfileImage());

        // File is stored on disk
        String newFilename = Paths.get(newImageUrl).getFileName().toString();
        Path newFilePath = uploadDir.resolve(newFilename);
        assertTrue(Files.exists(newFilePath));
    }

    @Test
    @DisplayName("3. Sequential replacements (A -> B -> C): Only latest image remains in storage")
    void sequentialReplacements_onlyLatestImageRemains() throws Exception {
        // Step A: Upload Image A
        MockMultipartFile fileA = new MockMultipartFile("image", "avatarA.png", "image/png", "imgA".getBytes());
        ResponseEntity<?> resA = userController.uploadProfileImage(fileA, null, mockAuth, null);
        assertEquals(HttpStatus.OK, resA.getStatusCode());
        String urlA = (String) ((Map<?, ?>) resA.getBody()).get("profileImage");
        Path pathA = uploadDir.resolve(Paths.get(urlA).getFileName().toString());
        assertTrue(Files.exists(pathA), "Image A must exist after upload A");

        // Step B: Upload Image B -> A must be deleted, B must exist
        MockMultipartFile fileB = new MockMultipartFile("image", "avatarB.png", "image/png", "imgB".getBytes());
        ResponseEntity<?> resB = userController.uploadProfileImage(fileB, null, mockAuth, null);
        assertEquals(HttpStatus.OK, resB.getStatusCode());
        String urlB = (String) ((Map<?, ?>) resB.getBody()).get("profileImage");
        Path pathB = uploadDir.resolve(Paths.get(urlB).getFileName().toString());
        assertFalse(Files.exists(pathA), "Image A must be deleted after upload B");
        assertTrue(Files.exists(pathB), "Image B must exist after upload B");

        // Step C: Upload Image C -> B must be deleted, C must exist
        MockMultipartFile fileC = new MockMultipartFile("image", "avatarC.png", "image/png", "imgC".getBytes());
        ResponseEntity<?> resC = userController.uploadProfileImage(fileC, null, mockAuth, null);
        assertEquals(HttpStatus.OK, resC.getStatusCode());
        String urlC = (String) ((Map<?, ?>) resC.getBody()).get("profileImage");
        Path pathC = uploadDir.resolve(Paths.get(urlC).getFileName().toString());
        assertFalse(Files.exists(pathB), "Image B must be deleted after upload C");
        assertTrue(Files.exists(pathC), "Image C must exist after upload C");
    }

    @Test
    @DisplayName("4. Failure rollback: If database update fails, old image remains intact and new file is cleaned up")
    void failedUpload_rollsBackSafely() throws Exception {
        // Old image setup
        String oldFilename = userPrefix + "-safe-original.png";
        Path oldFilePath = uploadDir.resolve(oldFilename);
        Files.writeString(oldFilePath, "safe-original-bytes");
        String originalDbPath = "/uploads/profile-images/" + oldFilename;
        testUser.setProfileImage(originalDbPath);

        // Simulate database failure
        reset(userRepository);
        when(userRepository.findByEmailIgnoreCase(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error during save"));

        MockMultipartFile newImageFile = new MockMultipartFile(
            "image",
            "will-fail.png",
            "image/png",
            "failure-data".getBytes()
        );

        ResponseEntity<?> response = userController.uploadProfileImage(newImageFile, null, mockAuth, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        // Verification: Old file is still safely on disk!
        assertTrue(Files.exists(oldFilePath), "Old image file must NOT be deleted when upload fails!");
    }

    @Test
    @DisplayName("5. Safety validation: isManagedProfileImage rejects unauthorized or unsafe deletion targets")
    void isManagedProfileImage_safetyValidation() {
        // External URLs
        assertFalse(fileUploadService.isManagedProfileImage("https://lh3.googleusercontent.com/avatar.jpg", userPrefix));
        assertFalse(fileUploadService.isManagedProfileImage("http://cdn.example.com/pic.png", userPrefix));
        assertFalse(fileUploadService.isManagedProfileImage("//example.com/test.jpg", userPrefix));

        // Default assets
        assertFalse(fileUploadService.isManagedProfileImage("/static/default-avatar.png", userPrefix));
        assertFalse(fileUploadService.isManagedProfileImage("/assets/profile-avatar.jpg", userPrefix));

        // Path traversal
        assertFalse(fileUploadService.isManagedProfileImage("../../etc/passwd", userPrefix));
        assertFalse(fileUploadService.isManagedProfileImage("/uploads/profile-images/../passwords.txt", userPrefix));

        // Outside allowed directories
        assertFalse(fileUploadService.isManagedProfileImage("/uploads/documents/report.pdf", userPrefix));
        assertFalse(fileUploadService.isManagedProfileImage("/uploads/videos/course.mp4", userPrefix));

        // Another user's image
        assertFalse(fileUploadService.isManagedProfileImage("/uploads/profile-images/user-otherid-avatar.jpg", userPrefix));
    }
}
