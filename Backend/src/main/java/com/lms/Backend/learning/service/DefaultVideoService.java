package com.lms.Backend.learning.service;

import com.lms.Backend.course.entity.CourseLesson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Default implementation of VideoService.
 * During development, all lessons across all courses seamlessly fall back to the common
 * development video (uploads/videos/video.mp4) while maintaining separate learner progress.
 * Future cloud storage (AWS S3, Cloudinary, GCS, CDN) can be plugged in by setting videoKey/contentUrl.
 */
@Service
public class DefaultVideoService implements VideoService {

    private static final Logger log = LoggerFactory.getLogger(DefaultVideoService.class);

    @Value("${app.video.default-path:uploads/videos/video.mp4}")
    private String defaultVideoPath;

    @Override
    public Resource getVideoResource(CourseLesson lesson) throws IOException {
        if (lesson == null) {
            return getDefaultVideoResource();
        }

        // 1. Check if lesson has a specific video reference (videoKey or contentUrl)
        String specificRef = null;
        if (lesson.getVideoKey() != null && !lesson.getVideoKey().trim().isEmpty()) {
            specificRef = lesson.getVideoKey().trim();
        } else if (lesson.getContentUrl() != null && !lesson.getContentUrl().trim().isEmpty()) {
            specificRef = lesson.getContentUrl().trim();
        }

        if (specificRef != null) {
            Resource specificResource = resolveSpecificResource(specificRef);
            if (specificResource != null && specificResource.exists() && specificResource.isReadable()) {
                log.info("[VideoService] Serving specific video for lessonId={}", lesson.getId());
                return specificResource;
            }
            log.debug("[VideoService] Specific video reference '{}' for lessonId={} not resolved to a local/cloud file. Falling back to default video.",
                specificRef, lesson.getId());
        }

        // 2. Default development video fallback (uploads/videos/video.mp4)
        log.info("[VideoService] Serving default development video ({}) for lessonId={}", defaultVideoPath, lesson.getId());
        return getDefaultVideoResource();
    }

    private Resource resolveSpecificResource(String ref) {
        try {
            // Check if reference is a remote / cloud URL (e.g. S3 presigned URL, Cloudinary, CDN)
            if (ref.startsWith("http://") || ref.startsWith("https://")) {
                return new UrlResource(ref);
            }

            // Check if reference is an absolute filesystem path
            Path directPath = Paths.get(ref).toAbsolutePath().normalize();
            if (Files.exists(directPath) && Files.isReadable(directPath)) {
                return new FileSystemResource(directPath.toFile());
            }

            // Check if reference is relative to current working directory
            Path relativePath = Paths.get(System.getProperty("user.dir"), ref).normalize();
            if (Files.exists(relativePath) && Files.isReadable(relativePath)) {
                return new FileSystemResource(relativePath.toFile());
            }
        } catch (MalformedURLException e) {
            log.warn("[VideoService] Invalid URL format for specific video reference '{}': {}", ref, e.getMessage());
        }
        return null;
    }

    @Override
    public Resource getDefaultVideoResource() throws IOException {
        Path path = Paths.get(defaultVideoPath).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isReadable(path)) {
            Path alt = Paths.get(System.getProperty("user.dir"), defaultVideoPath).normalize();
            if (Files.exists(alt) && Files.isReadable(alt)) {
                path = alt;
            }
        }

        if (!Files.exists(path) || !Files.isReadable(path)) {
            log.error("[VideoService] Default development video file not found at: {}", defaultVideoPath);
            throw new FileNotFoundException("Default development video file not found on server at: " + defaultVideoPath);
        }

        return new FileSystemResource(path.toFile());
    }
}
