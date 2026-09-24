package com.lms.Backend.opportunity.service;

import com.lms.Backend.common.exception.ResourceNotFoundException;
import com.lms.Backend.opportunity.dto.OpportunityApplicationDto;
import com.lms.Backend.opportunity.dto.OpportunityDto;
import com.lms.Backend.opportunity.dto.OpportunityStatsDto;
import com.lms.Backend.opportunity.entity.Opportunity;
import com.lms.Backend.opportunity.entity.OpportunityApplication;
import com.lms.Backend.opportunity.entity.SavedOpportunity;
import com.lms.Backend.opportunity.repository.OpportunityApplicationRepository;
import com.lms.Backend.opportunity.repository.OpportunityRepository;
import com.lms.Backend.opportunity.repository.SavedOpportunityRepository;
import com.lms.Backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityApplicationRepository applicationRepository;
    private final SavedOpportunityRepository savedRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy")
        .withZone(ZoneId.systemDefault());

    public OpportunityService(
        OpportunityRepository opportunityRepository,
        OpportunityApplicationRepository applicationRepository,
        SavedOpportunityRepository savedRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.savedRepository = savedRepository;
    }

    @Transactional(readOnly = true)
    public List<OpportunityDto> getPublicOpportunities(String type, String workMode, String query) {
        String cleanType = (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("all")) ? type.trim() : null;
        String cleanMode = (workMode != null && !workMode.trim().isEmpty() && !workMode.equalsIgnoreCase("all")) ? workMode.trim() : null;
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;

        List<Opportunity> list;
        if (cleanType == null && cleanMode == null && cleanQuery == null) {
            list = opportunityRepository.findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscCreatedAtDesc();
        } else {
            list = opportunityRepository.searchPublicOpportunities(cleanType, cleanMode, cleanQuery);
        }

        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OpportunityDto getOpportunityById(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        return toDto(opp);
    }

    @Transactional(readOnly = true)
    public OpportunityStatsDto getStats(User user) {
        long available = opportunityRepository.countByActiveTrueAndPublishedTrue();
        long partners = opportunityRepository.countDistinctActivePublishedCompanies();
        long applied = user != null ? applicationRepository.countByUserId(user.getId()) : 0;
        long saved = user != null ? savedRepository.countByUserId(user.getId()) : 0;

        return new OpportunityStatsDto(available, applied, saved, partners);
    }

    @Transactional(readOnly = true)
    public List<OpportunityApplicationDto> getUserApplications(User user) {
        if (user == null) return List.of();
        return applicationRepository.findByUserIdOrderByAppliedAtDesc(user.getId())
            .stream()
            .map(this::toApplicationDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getUserSavedIds(User user) {
        if (user == null) return List.of();
        return savedRepository.findByUserIdOrderBySavedAtDesc(user.getId())
            .stream()
            .map(s -> String.valueOf(s.getOpportunity().getId()))
            .collect(Collectors.toList());
    }

    public OpportunityApplicationDto apply(User user, Long opportunityId, String notes) {
        if (user == null) {
            throw new IllegalArgumentException("User must be authenticated to apply");
        }
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        if (applicationRepository.existsByUserIdAndOpportunityId(user.getId(), opportunityId)) {
            // Already applied - return existing application without creating duplicate
            OpportunityApplication existing = applicationRepository
                .findByUserIdAndOpportunityId(user.getId(), opportunityId)
                .orElseThrow();
            return toApplicationDto(existing);
        }

        OpportunityApplication app = new OpportunityApplication(user, opportunity, notes);
        OpportunityApplication saved = applicationRepository.save(app);
        return toApplicationDto(saved);
    }

    public boolean toggleSave(User user, Long opportunityId) {
        if (user == null) {
            throw new IllegalArgumentException("User must be authenticated to save opportunities");
        }
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        Optional<SavedOpportunity> existing = savedRepository.findByUserIdAndOpportunityId(user.getId(), opportunityId);
        if (existing.isPresent()) {
            savedRepository.delete(existing.get());
            return false; // Now unsaved
        } else {
            savedRepository.save(new SavedOpportunity(user, opportunity));
            return true; // Now saved
        }
    }

    public void removeSaved(User user, Long opportunityId) {
        if (user == null) return;
        savedRepository.deleteByUserIdAndOpportunityId(user.getId(), opportunityId);
    }

    // Admin Operations
    @Transactional(readOnly = true)
    public List<OpportunityDto> getAllAdminOpportunities() {
        return opportunityRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public OpportunityDto createOpportunity(Map<String, Object> payload) {
        Opportunity opp = new Opportunity();
        applyPayload(opp, payload);
        Opportunity saved = opportunityRepository.save(opp);
        return toDto(saved);
    }

    public OpportunityDto updateOpportunity(Long id, Map<String, Object> payload) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        applyPayload(opp, payload);
        Opportunity updated = opportunityRepository.save(opp);
        return toDto(updated);
    }

    public OpportunityDto togglePublish(Long id, Boolean published) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opp.setPublished(published != null ? published : !opp.isPublished());
        return toDto(opportunityRepository.save(opp));
    }

    public OpportunityDto toggleStatus(Long id, Boolean active) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opp.setActive(active != null ? active : !opp.isActive());
        return toDto(opportunityRepository.save(opp));
    }

    public void deleteOpportunity(Long id) {
        Opportunity opp = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));
        opportunityRepository.delete(opp);
    }

    @SuppressWarnings("unchecked")
    private void applyPayload(Opportunity opp, Map<String, Object> payload) {
        if (payload.containsKey("title")) opp.setTitle((String) payload.get("title"));
        if (payload.containsKey("company")) opp.setCompany((String) payload.get("company"));
        if (payload.containsKey("companyName")) opp.setCompany((String) payload.get("companyName"));
        if (payload.containsKey("companyLogo")) opp.setCompanyLogo((String) payload.get("companyLogo"));
        if (payload.containsKey("location")) opp.setLocation((String) payload.get("location"));
        if (payload.containsKey("type")) opp.setType((String) payload.get("type"));
        if (payload.containsKey("workMode")) opp.setWorkMode((String) payload.get("workMode"));
        if (payload.containsKey("salary")) opp.setSalary((String) payload.get("salary"));
        if (payload.containsKey("salaryMin") && payload.get("salaryMin") != null) {
            opp.setSalaryMin(Double.parseDouble(String.valueOf(payload.get("salaryMin"))));
        }
        if (payload.containsKey("salaryMax") && payload.get("salaryMax") != null) {
            opp.setSalaryMax(Double.parseDouble(String.valueOf(payload.get("salaryMax"))));
        }
        if (payload.containsKey("salaryPeriod")) opp.setSalaryPeriod((String) payload.get("salaryPeriod"));
        if (payload.containsKey("currency")) opp.setCurrency((String) payload.get("currency"));
        if (payload.containsKey("experienceLevel")) opp.setExperienceLevel((String) payload.get("experienceLevel"));
        if (payload.containsKey("category")) opp.setCategory((String) payload.get("category"));
        if (payload.containsKey("matchScore") && payload.get("matchScore") != null) {
            opp.setMatchScore(Integer.parseInt(String.valueOf(payload.get("matchScore"))));
        }
        if (payload.containsKey("description")) opp.setDescription((String) payload.get("description"));
        if (payload.containsKey("aboutCompany")) opp.setAboutCompany((String) payload.get("aboutCompany"));
        if (payload.containsKey("deadline")) opp.setDeadline((String) payload.get("deadline"));
        if (payload.containsKey("roleTrackId")) opp.setRoleTrackId((String) payload.get("roleTrackId"));
        if (payload.containsKey("active")) opp.setActive(Boolean.parseBoolean(String.valueOf(payload.get("active"))));
        if (payload.containsKey("published")) opp.setPublished(Boolean.parseBoolean(String.valueOf(payload.get("published"))));
        if (payload.containsKey("featured")) opp.setFeatured(Boolean.parseBoolean(String.valueOf(payload.get("featured"))));
        if (payload.containsKey("displayOrder") && payload.get("displayOrder") != null) {
            opp.setDisplayOrder(Integer.parseInt(String.valueOf(payload.get("displayOrder"))));
        }

        if (payload.get("requiredSkills") instanceof List) {
            List<?> rawList = (List<?>) payload.get("requiredSkills");
            List<String> skills = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof Map) {
                    Object name = ((Map<?, ?>) item).get("name");
                    if (name != null) skills.add(name.toString());
                } else if (item != null) {
                    skills.add(item.toString());
                }
            }
            opp.setRequiredSkills(skills);
        }
        if (payload.get("responsibilities") instanceof List) {
            opp.setResponsibilities((List<String>) payload.get("responsibilities"));
        }
        if (payload.get("qualifications") instanceof List) {
            opp.setQualifications((List<String>) payload.get("qualifications"));
        }
        if (payload.get("benefits") instanceof List) {
            opp.setBenefits((List<String>) payload.get("benefits"));
        }
    }

    public OpportunityDto toDto(Opportunity opp) {
        OpportunityDto dto = new OpportunityDto();
        dto.setId(String.valueOf(opp.getId()));
        dto.setTitle(opp.getTitle());
        dto.setCompany(opp.getCompany());
        dto.setCompanyLogo(opp.getCompanyLogo());
        dto.setLocation(opp.getLocation());
        dto.setType(opp.getType());
        dto.setWorkMode(opp.getWorkMode());
        dto.setSalary(opp.getSalary());
        dto.setSalaryMin(opp.getSalaryMin());
        dto.setSalaryMax(opp.getSalaryMax());
        dto.setSalaryPeriod(opp.getSalaryPeriod());
        dto.setCurrency(opp.getCurrency());
        dto.setExperienceLevel(opp.getExperienceLevel());
        dto.setCategory(opp.getCategory());
        dto.setMatchScore(opp.getMatchScore());
        dto.setDescription(opp.getDescription());
        dto.setAboutCompany(opp.getAboutCompany());

        if (opp.getRequiredSkills() != null) {
            dto.setRequiredSkills(
                opp.getRequiredSkills().stream()
                    .map(s -> new OpportunityDto.SkillItem(s, true))
                    .collect(Collectors.toList())
            );
        }
        dto.setResponsibilities(opp.getResponsibilities() != null ? new ArrayList<>(opp.getResponsibilities()) : new ArrayList<>());
        dto.setQualifications(opp.getQualifications() != null ? new ArrayList<>(opp.getQualifications()) : new ArrayList<>());
        dto.setBenefits(opp.getBenefits() != null ? new ArrayList<>(opp.getBenefits()) : new ArrayList<>());
        dto.setDeadline(opp.getDeadline());
        dto.setRoleTrackId(opp.getRoleTrackId());
        dto.setActive(opp.isActive());
        dto.setPublished(opp.isPublished());
        dto.setFeatured(opp.isFeatured());
        dto.setDisplayOrder(opp.getDisplayOrder());
        dto.setCreatedAt(opp.getCreatedAt());
        dto.setUpdatedAt(opp.getUpdatedAt());
        dto.setPostedDate(formatRelativeTime(opp.getCreatedAt()));

        return dto;
    }

    public OpportunityApplicationDto toApplicationDto(OpportunityApplication app) {
        OpportunityApplicationDto dto = new OpportunityApplicationDto();
        dto.setId(String.valueOf(app.getId()));
        dto.setOpportunityId(String.valueOf(app.getOpportunity().getId()));
        dto.setOpportunityTitle(app.getOpportunity().getTitle());
        dto.setCompanyName(app.getOpportunity().getCompany());
        dto.setCompanyLogo(app.getOpportunity().getCompanyLogo());
        dto.setType(app.getOpportunity().getType());
        dto.setLocation(app.getOpportunity().getLocation());
        dto.setSalary(app.getOpportunity().getSalary());
        dto.setStatus(app.getStatus());
        dto.setNotes(app.getNotes());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setAppliedAtFormatted(app.getAppliedAt() != null ? DATE_FORMATTER.format(app.getAppliedAt()) : "");
        return dto;
    }

    private String formatRelativeTime(Instant instant) {
        if (instant == null) return "Recent";
        Duration diff = Duration.between(instant, Instant.now());
        long seconds = diff.getSeconds();
        if (seconds < 60) return "Just now";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        if (days == 1) return "1 day ago";
        if (days < 7) return days + " days ago";
        long weeks = days / 7;
        if (weeks == 1) return "1 week ago";
        if (weeks < 4) return weeks + " weeks ago";
        return DATE_FORMATTER.format(instant);
    }
}
