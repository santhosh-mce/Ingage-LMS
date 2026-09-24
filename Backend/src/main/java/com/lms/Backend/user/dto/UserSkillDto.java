package com.lms.Backend.user.dto;

public class UserSkillDto {
    private Long id;
    private String name;
    private String category;
    private String level;

    public UserSkillDto() {}

    public UserSkillDto(Long id, String name, String category, String level) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.level = level;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
