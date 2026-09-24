package com.lms.Backend.explore.dto;

import java.util.List;

public class ExploreResponse {

    private List<CareerExploreItem> careers;
    private List<ProjectExploreItem> projects;

    public ExploreResponse() {}

    public ExploreResponse(List<CareerExploreItem> careers, List<ProjectExploreItem> projects) {
        this.careers = careers;
        this.projects = projects;
    }

    public List<CareerExploreItem> getCareers() { return careers; }
    public void setCareers(List<CareerExploreItem> careers) { this.careers = careers; }

    public List<ProjectExploreItem> getProjects() { return projects; }
    public void setProjects(List<ProjectExploreItem> projects) { this.projects = projects; }

    public static class CareerExploreItem {
        private Long id;
        private String title;
        private String slug;
        private String category;
        private boolean trending;
        private String icon;

        public CareerExploreItem() {}

        public CareerExploreItem(Long id, String title, String slug, String category, boolean trending, String icon) {
            this.id = id;
            this.title = title;
            this.slug = slug;
            this.category = category;
            this.trending = trending;
            this.icon = icon;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public boolean isTrending() { return trending; }
        public void setTrending(boolean trending) { this.trending = trending; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    public static class ProjectExploreItem {
        private Long id;
        private String title;
        private String slug;
        private String category;
        private String industry;

        public ProjectExploreItem() {}

        public ProjectExploreItem(Long id, String title, String slug, String category, String industry) {
            this.id = id;
            this.title = title;
            this.slug = slug;
            this.category = category;
            this.industry = industry;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getIndustry() { return industry; }
        public void setIndustry(String industry) { this.industry = industry; }
    }
}
