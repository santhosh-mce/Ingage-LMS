package com.lms.Backend.career.service;

import com.lms.Backend.career.dto.*;
import com.lms.Backend.career.entity.*;
import com.lms.Backend.career.repository.*;
import com.lms.Backend.common.exception.ResourceNotFoundException;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.payment.entity.Order;
import com.lms.Backend.payment.entity.OrderStatus;
import com.lms.Backend.payment.entity.Payment;
import com.lms.Backend.payment.entity.PaymentStatus;
import com.lms.Backend.payment.repository.OrderRepository;
import com.lms.Backend.payment.repository.PaymentRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CareerService {

    private final CareerRepository careerRepository;
    private final CareerSkillRepository skillRepository;
    private final CareerResponsibilityRepository responsibilityRepository;
    private final CareerRoadmapRepository roadmapRepository;
    private final CareerProjectRepository projectRepository;
    private final CareerCourseRepository careerCourseRepository;
    private final CareerOpportunityRepository opportunityRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CareerEnrollmentRepository careerEnrollmentRepository;

    public CareerService(
        CareerRepository careerRepository,
        CareerSkillRepository skillRepository,
        CareerResponsibilityRepository responsibilityRepository,
        CareerRoadmapRepository roadmapRepository,
        CareerProjectRepository projectRepository,
        CareerCourseRepository careerCourseRepository,
        CareerOpportunityRepository opportunityRepository,
        CourseRepository courseRepository,
        EnrollmentRepository enrollmentRepository,
        OrderRepository orderRepository,
        PaymentRepository paymentRepository,
        UserRepository userRepository,
        CareerEnrollmentRepository careerEnrollmentRepository
    ) {
        this.careerRepository = careerRepository;
        this.skillRepository = skillRepository;
        this.responsibilityRepository = responsibilityRepository;
        this.roadmapRepository = roadmapRepository;
        this.projectRepository = projectRepository;
        this.careerCourseRepository = careerCourseRepository;
        this.opportunityRepository = opportunityRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.careerEnrollmentRepository = careerEnrollmentRepository;
    }

    public PageResponse<CareerResponse> getFilteredCareers(
        String keyword,
        String category,
        String level,
        Boolean featured,
        Boolean popular,
        Pageable pageable
    ) {
        Page<Career> page = careerRepository.findFilteredCareers(keyword, category, level, featured, popular, pageable);
        Page<CareerResponse> responsePage = page.map(this::mapToCareerResponse);
        return PageResponse.from(responsePage);
    }

    public PageResponse<CareerResponse> searchCareers(String keyword, Pageable pageable) {
        Page<Career> page = careerRepository.searchCareers(keyword, pageable);
        Page<CareerResponse> responsePage = page.map(this::mapToCareerResponse);
        return PageResponse.from(responsePage);
    }

    public CareerDetailResponse getCareerBySlug(String slug) {
        return getCareerBySlug(slug, null);
    }

    public CareerDetailResponse getCareerBySlug(String slug, User user) {
        Career career = careerRepository.findBySlugAndActiveTrue(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Career path not found with slug: " + slug));
        return mapToCareerDetailResponse(career, user);
    }

    public CareerDetailResponse getCareerById(Long id) {
        return getCareerById(id, null);
    }

    public CareerDetailResponse getCareerById(Long id, User user) {
        Career career = careerRepository.findById(id)
            .filter(Career::isActive)
            .orElseThrow(() -> new ResourceNotFoundException("Career path not found with id: " + id));
        return mapToCareerDetailResponse(career, user);
    }

    public Career findCareerEntity(String slugOrId) {
        return careerRepository.findBySlugAndActiveTrue(slugOrId)
            .orElseGet(() -> {
                try {
                    Long id = Long.parseLong(slugOrId);
                    return careerRepository.findById(id).filter(Career::isActive).orElse(null);
                } catch (NumberFormatException e) {
                    return null;
                }
            });
    }

    public boolean hasUserPurchasedCareer(Career career, User user) {
        if (user == null || career == null) {
            return false;
        }
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // 1. Direct active CareerEnrollment check
        if (careerEnrollmentRepository.existsByUserIdAndCareerIdAndStatus(user.getId(), career.getId(), EnrollmentStatus.ACTIVE)
            || careerEnrollmentRepository.existsByUserIdAndCareerIdAndStatus(user.getId(), career.getId(), EnrollmentStatus.COMPLETED)) {
            return true;
        }

        // 2. Direct PAID payment check
        if (paymentRepository.existsByUserIdAndCareerIdAndPaymentStatus(user.getId(), career.getId(), PaymentStatus.PAID)) {
            return true;
        }

        return false;
    }

    public String generateCurriculumContent(Career career) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ingage LMS - ").append(career.getTitle()).append(" Career Curriculum\n");
        sb.append("Duration: ").append(career.getDuration() != null ? career.getDuration() : "N/A").append("\n");
        sb.append("Level: ").append(career.getLevel() != null ? career.getLevel() : "N/A").append("\n");
        sb.append("Average Salary: ").append(formatSalary(career.getSalaryMin(), career.getSalaryMax())).append("\n");
        sb.append("Job Openings: ").append(career.getJobOpenings() != null ? career.getJobOpenings() : "N/A").append("\n\n");

        sb.append("Modules & Roadmaps:\n");
        if (career.getRoadmaps() != null && !career.getRoadmaps().isEmpty()) {
            int idx = 1;
            for (CareerRoadmap rm : career.getRoadmaps()) {
                sb.append(idx++).append(". ").append(rm.getTitle());
                if (rm.getDuration() != null && !rm.getDuration().isBlank()) {
                    sb.append(" (").append(rm.getDuration()).append(")");
                }
                sb.append("\n");
                if (rm.getDescription() != null && !rm.getDescription().isBlank()) {
                    sb.append("   ").append(rm.getDescription()).append("\n");
                }
            }
        } else {
            sb.append("1. Foundations of ").append(career.getTitle()).append(" (3 weeks)\n");
            sb.append("2. Applied Skills & Workflows (4 weeks)\n");
            sb.append("3. Enterprise Architecture & Projects (4 weeks)\n");
            sb.append("4. Capstone & Certification Defense (3 weeks)\n");
        }

        sb.append("\nKey Skills: ");
        if (career.getSkills() != null && !career.getSkills().isEmpty()) {
            sb.append(career.getSkills().stream().map(CareerSkill::getSkillName).collect(Collectors.joining(", ")));
        } else {
            sb.append("Problem Solving, Data Structures, System Architecture, Version Control");
        }
        sb.append("\n");

        if (career.getCertificationName() != null && !career.getCertificationName().isBlank()) {
            sb.append("Certification: ").append(career.getCertificationName()).append("\n");
        } else {
            sb.append("Certification: Certified ").append(career.getTitle()).append(" Professional\n");
        }

        return sb.toString();
    }

    public List<String> getCategories() {
        return careerRepository.findDistinctCategories();
    }

    public CareerStatsResponse getCareerStats(Long id) {
        if (!careerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Career path not found with id: " + id);
        }
        long courseCount = careerCourseRepository.countByCareerId(id);
        long projectCount = projectRepository.countByCareerId(id);
        long jobCount = opportunityRepository.countByCareerId(id);
        long skillCount = skillRepository.countByCareerId(id);

        return new CareerStatsResponse(courseCount, projectCount, jobCount, skillCount);
    }

    // --- Admin Operations ---

    public PageResponse<CareerResponse> getAllCareersAdmin(Pageable pageable) {
        Page<Career> page = careerRepository.findAll(pageable);
        return PageResponse.from(page.map(this::mapToCareerResponse));
    }

    @Transactional
    public CareerDetailResponse createCareer(CareerRequest request) {
        if (careerRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Career slug already exists: " + request.slug());
        }

        Career career = new Career();
        applyRequestToEntity(career, request);
        careerRepository.save(career);

        return mapToCareerDetailResponse(career);
    }

    @Transactional
    public CareerDetailResponse updateCareer(Long id, CareerRequest request) {
        Career career = careerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Career path not found with id: " + id));

        if (!career.getSlug().equalsIgnoreCase(request.slug()) && careerRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Career slug already in use by another career: " + request.slug());
        }

        applyRequestToEntity(career, request);
        careerRepository.save(career);

        return mapToCareerDetailResponse(career);
    }

    @Transactional
    public void deleteCareer(Long id) {
        Career career = careerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Career path not found with id: " + id));
        careerRepository.delete(career);
    }

    // --- Mappings ---

    public CareerResponse mapToCareerResponse(Career career) {
        List<String> skillNames = career.getSkills() != null
            ? career.getSkills().stream().map(CareerSkill::getSkillName).collect(Collectors.toList())
            : Collections.emptyList();

        String formattedSalary = formatSalary(career.getSalaryMin(), career.getSalaryMax());

        return new CareerResponse(
            career.getId(),
            career.getTitle(),
            career.getSlug(),
            career.getCategory(),
            career.getDescription(),
            career.getShortDescription(),
            career.getLevel(),
            career.getDuration(),
            new CareerResponse.SalaryInfo(
                career.getSalaryMin(),
                career.getSalaryMax(),
                career.getSalaryCurrency(),
                formattedSalary
            ),
            career.getImageUrl(),
            career.getIcon(),
            career.isFeatured(),
            career.isPopular(),
            career.isActive(),
            career.getDisplayOrder(),
            career.getJobOpenings(),
            career.getModulesCount(),
            career.getCertificationName(),
            skillNames
        );
    }

    public CareerDetailResponse mapToCareerDetailResponse(Career career) {
        return mapToCareerDetailResponse(career, null);
    }

    public CareerDetailResponse mapToCareerDetailResponse(Career career, User user) {
        List<CareerSkillDto> skillDtos = career.getSkills().stream()
            .map(s -> new CareerSkillDto(s.getId(), s.getSkillName(), s.getSkillType(), s.getDisplayOrder()))
            .collect(Collectors.toList());

        List<CareerResponsibilityDto> respDtos = career.getResponsibilities().stream()
            .map(r -> new CareerResponsibilityDto(r.getId(), r.getResponsibility(), r.getDisplayOrder()))
            .collect(Collectors.toList());

        List<CareerRoadmapDto> roadmapDtos = career.getRoadmaps().stream()
            .map(rm -> new CareerRoadmapDto(rm.getId(), rm.getTitle(), rm.getDescription(), rm.getDuration(), rm.getDisplayOrder()))
            .collect(Collectors.toList());

        List<CareerProjectDto> projectDtos = career.getProjects().stream()
            .map(p -> new CareerProjectDto(p.getId(), p.getTitle(), p.getDescription(), p.getDifficulty(), p.getTechnologies(), p.getDisplayOrder()))
            .collect(Collectors.toList());

        List<CareerCourseDto> courseDtos = career.getCareerCourses().stream()
            .filter(cc -> cc.getCourse() != null && cc.getCourse().isPublished())
            .map(cc -> mapCourseToDtoWithUserStatus(cc, user))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        List<CareerOpportunityDto> oppDtos = career.getOpportunities().stream()
            .map(o -> new CareerOpportunityDto(o.getId(), o.getTitle(), o.getCompany(), o.getLocation(), o.getType(), o.getSalary(), o.getDisplayOrder()))
            .collect(Collectors.toList());

        CareerStatsResponse stats = new CareerStatsResponse(
            courseDtos.size(),
            projectDtos.size(),
            oppDtos.size(),
            skillDtos.size()
        );

        String formattedSalary = formatSalary(career.getSalaryMin(), career.getSalaryMax());

        return new CareerDetailResponse(
            career.getId(),
            career.getTitle(),
            career.getSlug(),
            career.getCategory(),
            career.getDescription(),
            career.getShortDescription(),
            career.getLevel(),
            career.getDuration(),
            new CareerResponse.SalaryInfo(
                career.getSalaryMin(),
                career.getSalaryMax(),
                career.getSalaryCurrency(),
                formattedSalary
            ),
            career.getImageUrl(),
            career.getIcon(),
            career.isFeatured(),
            career.isPopular(),
            career.isActive(),
            career.getDisplayOrder(),
            career.getJobOpenings(),
            career.getModulesCount(),
            career.getCertificationName(),
            skillDtos,
            respDtos,
            roadmapDtos,
            projectDtos,
            courseDtos,
            oppDtos,
            stats,
            career.getCreatedAt(),
            career.getUpdatedAt()
        );
    }

    @Transactional
    public CareerCourseDto mapCourseToDtoWithUserStatus(CareerCourse cc, User user) {
        Course c = cc.getCourse();
        if (c == null) {
            return null;
        }

        String enrollmentStatus = "NOT_ENROLLED";
        String paymentStatus = null;
        int progress = 0;
        boolean courseAccess = false;
        boolean completed = false;

        if (user == null) {
            enrollmentStatus = "NOT_LOGGED_IN";
        } else {
            // Admin has universal access
            if (user.getRole() != null && user.getRole().name().equals("ADMIN")) {
                enrollmentStatus = "ENROLLED";
                paymentStatus = "PAID";
                courseAccess = true;
            } else {
                // 1. Check active enrollment in DB
                Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(user.getId(), c.getId());
                if (enrollmentOpt.isPresent()) {
                    Enrollment e = enrollmentOpt.get();
                    progress = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
                    if (e.getStatus() == EnrollmentStatus.COMPLETED || progress >= 100) {
                        enrollmentStatus = "COMPLETED";
                        paymentStatus = "PAID";
                        courseAccess = true;
                        completed = true;
                    } else if (e.getStatus() == EnrollmentStatus.ACTIVE) {
                        enrollmentStatus = "ENROLLED";
                        paymentStatus = "PAID";
                        courseAccess = true;
                    } else {
                        enrollmentStatus = "NOT_ENROLLED";
                    }
                } else {
                    // 2. Check Payment and Order records
                    Optional<Payment> paymentOpt = paymentRepository.findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(user.getId(), c.getId());
                    if (paymentOpt.isPresent()) {
                        Payment p = paymentOpt.get();
                        if (p.getPaymentStatus() == PaymentStatus.PAID) {
                            try {
                                enrollmentRepository.save(new Enrollment(user, c));
                            } catch (Exception ignored) {}
                            enrollmentStatus = "ENROLLED";
                            paymentStatus = "PAID";
                            courseAccess = true;
                        } else if (p.getPaymentStatus() == PaymentStatus.PENDING) {
                            paymentStatus = "PENDING";
                            enrollmentStatus = "NOT_ENROLLED";
                        } else if (p.getPaymentStatus() == PaymentStatus.FAILED) {
                            paymentStatus = "FAILED";
                            enrollmentStatus = "NOT_ENROLLED";
                        }
                    } else {
                        Optional<Order> orderOpt = orderRepository.findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(user.getId(), c.getId());
                        if (orderOpt.isPresent()) {
                            Order o = orderOpt.get();
                            if (o.getStatus() == OrderStatus.PAID) {
                                try {
                                    enrollmentRepository.save(new Enrollment(user, c));
                                } catch (Exception ignored) {}
                                enrollmentStatus = "ENROLLED";
                                paymentStatus = "PAID";
                                courseAccess = true;
                            } else if (o.getStatus() == OrderStatus.PENDING) {
                                paymentStatus = "PENDING";
                                enrollmentStatus = "NOT_ENROLLED";
                            } else if (o.getStatus() == OrderStatus.FAILED || o.getStatus() == OrderStatus.CANCELLED) {
                                paymentStatus = "FAILED";
                                enrollmentStatus = "NOT_ENROLLED";
                            }
                        } else if (c.getPrice() != null && c.getPrice() == 0) {
                            // Free course
                            enrollmentStatus = "NOT_ENROLLED";
                            paymentStatus = null;
                            courseAccess = false;
                        }
                    }
                }
            }
        }

        // If course is not enrolled directly, check if the career path is purchased and the course is included
        if ("NOT_ENROLLED".equals(enrollmentStatus) && user != null && cc.getCareer() != null) {
            if (hasUserPurchasedCareer(cc.getCareer(), user) && cc.isIncluded()) {
                enrollmentStatus = "INCLUDED";
                paymentStatus = "PAID";
                courseAccess = true;
            }
        }

        return new CareerCourseDto(
            cc.getId(),
            c.getId(),
            c.getTitle(),
            c.getThumbnail(),
            c.getCategory(),
            c.getLevel(),
            c.getPrice(),
            cc.getDisplayOrder(),
            enrollmentStatus,
            paymentStatus,
            progress,
            courseAccess,
            completed,
            cc.isIncluded(),
            cc.isRequiredForCompletion()
        );
    }

    private void applyRequestToEntity(Career career, CareerRequest request) {
        career.setTitle(request.title());
        career.setSlug(request.slug());
        career.setCategory(request.category());
        career.setDescription(request.description());
        career.setShortDescription(request.shortDescription());
        career.setLevel(request.level());
        career.setDuration(request.duration());
        career.setSalaryMin(request.salaryMin());
        career.setSalaryMax(request.salaryMax());
        if (request.salaryCurrency() != null) career.setSalaryCurrency(request.salaryCurrency());
        career.setImageUrl(request.imageUrl());
        career.setIcon(request.icon());
        if (request.featured() != null) career.setFeatured(request.featured());
        if (request.popular() != null) career.setPopular(request.popular());
        if (request.active() != null) career.setActive(request.active());
        if (request.displayOrder() != null) career.setDisplayOrder(request.displayOrder());
        career.setJobOpenings(request.jobOpenings());
        if (request.modulesCount() != null) career.setModulesCount(request.modulesCount());
        career.setCertificationName(request.certificationName());

        // Skills
        career.getSkills().clear();
        if (request.skills() != null) {
            int order = 0;
            for (CareerRequest.SkillItem s : request.skills()) {
                career.addSkill(new CareerSkill(s.name(), s.type() != null ? s.type() : "REQUIRED", s.order() != null ? s.order() : order++));
            }
        }

        // Responsibilities
        career.getResponsibilities().clear();
        if (request.responsibilities() != null) {
            int order = 0;
            for (String r : request.responsibilities()) {
                career.addResponsibility(new CareerResponsibility(r, order++));
            }
        }

        // Roadmaps
        career.getRoadmaps().clear();
        if (request.roadmaps() != null) {
            int order = 0;
            for (CareerRequest.RoadmapItem rm : request.roadmaps()) {
                career.addRoadmap(new CareerRoadmap(rm.title(), rm.description(), rm.duration(), rm.order() != null ? rm.order() : order++));
            }
        }

        // Projects
        career.getProjects().clear();
        if (request.projects() != null) {
            int order = 0;
            for (CareerRequest.ProjectItem p : request.projects()) {
                career.addProject(new CareerProject(p.title(), p.description(), p.difficulty(), p.technologies(), p.order() != null ? p.order() : order++));
            }
        }

        // Opportunities
        career.getOpportunities().clear();
        if (request.opportunities() != null) {
            int order = 0;
            for (CareerRequest.OpportunityItem o : request.opportunities()) {
                career.addOpportunity(new CareerOpportunity(o.title(), o.company(), o.location(), o.type(), o.salary(), o.order() != null ? o.order() : order++));
            }
        }

        // Courses
        career.getCareerCourses().clear();
        if (request.courseIds() != null) {
            int order = 0;
            for (Long cId : request.courseIds()) {
                courseRepository.findById(cId).ifPresent(course -> {
                    career.addCourse(new CareerCourse(career, course, order));
                });
            }
        }
    }

    private String formatSalary(Long min, Long max) {
        if (min == null && max == null) return "Competitive";
        long minLpa = min != null ? min / 100000 : 0;
        long maxLpa = max != null ? max / 100000 : 0;
        if (minLpa > 0 && maxLpa > 0) {
            return String.format("₹%d–%d LPA", minLpa, maxLpa);
        } else if (minLpa > 0) {
            return String.format("From ₹%d LPA", minLpa);
        } else {
            return String.format("Up to ₹%d LPA", maxLpa);
        }
    }
}
