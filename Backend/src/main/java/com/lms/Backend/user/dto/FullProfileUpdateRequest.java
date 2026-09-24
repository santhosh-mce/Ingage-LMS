package com.lms.Backend.user.dto;

import java.util.List;

public class FullProfileUpdateRequest {

    private PersonalInfoDto personalInfo;
    private CareerGoalDto careerGoal;
    private ProfessionalLinksDto links;
    private List<UserEducationDto> education;
    private List<UserSkillDto> skills;

    public FullProfileUpdateRequest() {}

    public PersonalInfoDto getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfoDto personalInfo) {
        this.personalInfo = personalInfo;
    }

    public CareerGoalDto getCareerGoal() {
        return careerGoal;
    }

    public void setCareerGoal(CareerGoalDto careerGoal) {
        this.careerGoal = careerGoal;
    }

    public ProfessionalLinksDto getLinks() {
        return links;
    }

    public void setLinks(ProfessionalLinksDto links) {
        this.links = links;
    }

    public List<UserEducationDto> getEducation() {
        return education;
    }

    public void setEducation(List<UserEducationDto> education) {
        this.education = education;
    }

    public List<UserSkillDto> getSkills() {
        return skills;
    }

    public void setSkills(List<UserSkillDto> skills) {
        this.skills = skills;
    }
}
