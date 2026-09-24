package com.lms.Backend.opportunity.dto;

import java.time.Instant;

public class OpportunityApplicationDto {
    private String id;
    private String opportunityId;
    private String opportunityTitle;
    private String companyName;
    private String companyLogo;
    private String type;
    private String location;
    private String salary;
    private String status;
    private String notes;
    private Instant appliedAt;
    private String appliedAtFormatted;

    public OpportunityApplicationDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getOpportunityTitle() { return opportunityTitle; }
    public void setOpportunityTitle(String opportunityTitle) { this.opportunityTitle = opportunityTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Instant appliedAt) { this.appliedAt = appliedAt; }

    public String getAppliedAtFormatted() { return appliedAtFormatted; }
    public void setAppliedAtFormatted(String appliedAtFormatted) { this.appliedAtFormatted = appliedAtFormatted; }
}
