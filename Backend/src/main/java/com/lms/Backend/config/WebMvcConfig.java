package com.lms.Backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Global Web MVC configuration.
 * - Prefixes @RestController routes with "/api" when context path is not set to /api.
 * - Registers resource handlers to serve public user uploads (profile images, thumbnails, avatars, documents).
 * - Videos are strictly excluded from static handlers to enforce protected streaming authorization.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (contextPath == null || !contextPath.trim().startsWith("/api")) {
            configurer.addPathPrefix("/api", c -> c.isAnnotationPresent(RestController.class));
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

        // 1. Profile images handler
        registry.addResourceHandler("/uploads/profile-images/**", "/api/uploads/profile-images/**")
                .addResourceLocations(uploadDir.resolve("profile-images").toUri().toString() + "/");

        // 2. Avatars handler
        registry.addResourceHandler("/uploads/avatars/**", "/api/uploads/avatars/**")
                .addResourceLocations(uploadDir.resolve("avatars").toUri().toString() + "/");

        // 3. Course thumbnails handler
        registry.addResourceHandler("/uploads/thumbnails/**", "/api/uploads/thumbnails/**")
                .addResourceLocations(uploadDir.resolve("thumbnails").toUri().toString() + "/");

        // 4. Documents handler
        registry.addResourceHandler("/uploads/documents/**", "/api/uploads/documents/**")
                .addResourceLocations(uploadDir.resolve("documents").toUri().toString() + "/");
    }
}
