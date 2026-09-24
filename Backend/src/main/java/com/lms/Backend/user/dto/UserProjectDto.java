package com.lms.Backend.user.dto;

public class UserProjectDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String technologies;
    private String githubUrl;
    private String liveUrl;
    private String imageUrl;
    private String status;

    public UserProjectDto() {}

    public UserProjectDto(Long id, String title, String description, String category, String technologies, String githubUrl, String liveUrl, String imageUrl, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.technologies = technologies;
        this.githubUrl = githubUrl;
        this.liveUrl = liveUrl;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getLiveUrl() { return liveUrl; }
    public void setLiveUrl(String liveUrl) { this.liveUrl = liveUrl; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
