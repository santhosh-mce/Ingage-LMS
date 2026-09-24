package com.lms.Backend.opportunity.dto;

public class OpportunityStatsDto {
    private long availableCount;
    private long appliedCount;
    private long savedCount;
    private long partnersCount;

    public OpportunityStatsDto() {}

    public OpportunityStatsDto(long availableCount, long appliedCount, long savedCount, long partnersCount) {
        this.availableCount = availableCount;
        this.appliedCount = appliedCount;
        this.savedCount = savedCount;
        this.partnersCount = partnersCount;
    }

    public long getAvailableCount() { return availableCount; }
    public void setAvailableCount(long availableCount) { this.availableCount = availableCount; }

    public long getAppliedCount() { return appliedCount; }
    public void setAppliedCount(long appliedCount) { this.appliedCount = appliedCount; }

    public long getSavedCount() { return savedCount; }
    public void setSavedCount(long savedCount) { this.savedCount = savedCount; }

    public long getPartnersCount() { return partnersCount; }
    public void setPartnersCount(long partnersCount) { this.partnersCount = partnersCount; }
}
