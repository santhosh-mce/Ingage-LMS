package com.lms.Backend.user.dto;

import java.time.Instant;

public class ResumeDto {
    private String filename;
    private String url;
    private Long fileSize;
    private String formattedFileSize;
    private Instant uploadedAt;
    private String status; // "Uploaded", "Not Uploaded"

    public ResumeDto() {}

    public ResumeDto(String filename, String url, Long fileSize, Instant uploadedAt) {
        this.filename = filename;
        this.url = url;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.status = (url != null && !url.isBlank()) ? "Uploaded" : "Not Uploaded";
        this.formattedFileSize = formatSize(fileSize);
    }

    private String formatSize(Long bytes) {
        if (bytes == null || bytes <= 0) return "0 KB";
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { 
        this.fileSize = fileSize; 
        this.formattedFileSize = formatSize(fileSize);
    }
    public String getFormattedFileSize() { return formattedFileSize; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
