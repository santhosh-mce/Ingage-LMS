package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.FileUploadService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/admin/media/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "category", defaultValue = "video") String category
    ) {
        try {
            String fileUrl = fileUploadService.storeFile(file, category);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "url", fileUrl,
                "filename", file.getOriginalFilename(),
                "fileSize", file.getSize()
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    @GetMapping({"/uploads/{category}/{filename:.+}", "/api/uploads/{category}/{filename:.+}"})
    public ResponseEntity<Resource> serveFile(
        @PathVariable String category,
        @PathVariable String filename
    ) {
        if ("videos".equalsIgnoreCase(category) || "video".equalsIgnoreCase(category)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).build();
        }
        try {
            Path filePath = fileUploadService.loadFileAsPath(category, filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
