package com.lms.Backend.user.dto;

public class ProfessionalLinksDto {
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String otherWebsiteUrl;

    public ProfessionalLinksDto() {}

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getOtherWebsiteUrl() { return otherWebsiteUrl; }
    public void setOtherWebsiteUrl(String otherWebsiteUrl) { this.otherWebsiteUrl = otherWebsiteUrl; }
}
