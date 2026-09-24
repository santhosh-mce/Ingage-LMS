package com.lms.Backend.user.dto;

public class UserEducationDto {
    private Long id;
    private String qualification;
    private String degree;
    private String institution;
    private String department;
    private String graduationYear;
    private String cgpa;

    public UserEducationDto() {}

    public UserEducationDto(Long id, String qualification, String degree, String institution, String department, String graduationYear, String cgpa) {
        this.id = id;
        this.qualification = qualification;
        this.degree = degree;
        this.institution = institution;
        this.department = department;
        this.graduationYear = graduationYear;
        this.cgpa = cgpa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getGraduationYear() { return graduationYear; }
    public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }
    public String getCgpa() { return cgpa; }
    public void setCgpa(String cgpa) { this.cgpa = cgpa; }
}
