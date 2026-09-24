package com.lms.Backend.user.dto;

import java.util.ArrayList;
import java.util.List;

public class ProfileDto {
    private PersonalInfoDto personalInfo;
    private CareerGoalDto careerGoal;
    private ProfessionalLinksDto links;
    private ResumeDto resume;
    private List<UserEducationDto> education = new ArrayList<>();
    private List<UserSkillDto> skills = new ArrayList<>();
    private List<UserProjectDto> projects = new ArrayList<>();
    private DashboardSummaryDto summary;
    private CareerCompassReadinessDto careerCompass;
    private int profileCompletionPercentage;
    private List<String> completedSections = new ArrayList<>();
    private List<String> missingSections = new ArrayList<>();

    public ProfileDto() {}

    public PersonalInfoDto getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(PersonalInfoDto personalInfo) { this.personalInfo = personalInfo; }

    public CareerGoalDto getCareerGoal() { return careerGoal; }
    public void setCareerGoal(CareerGoalDto careerGoal) { this.careerGoal = careerGoal; }

    public ProfessionalLinksDto getLinks() { return links; }
    public void setLinks(ProfessionalLinksDto links) { this.links = links; }

    public ResumeDto getResume() { return resume; }
    public void setResume(ResumeDto resume) { this.resume = resume; }

    public List<UserEducationDto> getEducation() { return education; }
    public void setEducation(List<UserEducationDto> education) { this.education = education; }

    public List<UserSkillDto> getSkills() { return skills; }
    public void setSkills(List<UserSkillDto> skills) { this.skills = skills; }

    public List<UserProjectDto> getProjects() { return projects; }
    public void setProjects(List<UserProjectDto> projects) { this.projects = projects; }

    public DashboardSummaryDto getSummary() { return summary; }
    public void setSummary(DashboardSummaryDto summary) { this.summary = summary; }

    public CareerCompassReadinessDto getCareerCompass() { return careerCompass; }
    public void setCareerCompass(CareerCompassReadinessDto careerCompass) { this.careerCompass = careerCompass; }

    public int getProfileCompletionPercentage() { return profileCompletionPercentage; }
    public void setProfileCompletionPercentage(int profileCompletionPercentage) { this.profileCompletionPercentage = profileCompletionPercentage; }

    public List<String> getCompletedSections() { return completedSections; }
    public void setCompletedSections(List<String> completedSections) { this.completedSections = completedSections; }

    public List<String> getMissingSections() { return missingSections; }
    public void setMissingSections(List<String> missingSections) { this.missingSections = missingSections; }
}
