package com.lms.Backend.learning.service;

import com.lms.Backend.course.entity.CourseLesson;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Service abstraction for retrieving course lesson video resources.
 * Supports:
 * 1. Specific local or future cloud video references (via videoKey or contentUrl).
 * 2. Automatic fallback to the default development video (uploads/videos/video.mp4).
 */
public interface VideoService {

    /**
     * Resolves the video resource for a course lesson.
     * If the lesson has a specific video reference that exists, it will be used.
     * Otherwise, gracefully falls back to the common development video.
     *
     * @param lesson The course lesson
     * @return A readable Spring Resource representing the video
     * @throws IOException if the video resource cannot be resolved or is missing
     */
    Resource getVideoResource(CourseLesson lesson) throws IOException;

    /**
     * Resolves the default development video resource.
     *
     * @return A readable Spring Resource for the default video
     * @throws IOException if the default video file is missing on the server
     */
    Resource getDefaultVideoResource() throws IOException;
}
