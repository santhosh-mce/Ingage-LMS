package com.lms.Backend.credential.dto;

public class CredentialModuleDto {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private Integer orderIndex;

    public CredentialModuleDto() {}

    public CredentialModuleDto(Long id, String title, String description, String duration, Integer orderIndex) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.orderIndex = orderIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
