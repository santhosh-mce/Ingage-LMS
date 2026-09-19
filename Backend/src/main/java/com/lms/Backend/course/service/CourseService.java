package com.lms.Backend.course.service;

import com.lms.Backend.common.exception.ResourceNotFoundException;
import com.lms.Backend.course.dto.CourseEnrollmentStatusResponse;
import com.lms.Backend.course.dto.CourseResponse;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.course.repository.CourseSectionRepository;
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
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseSectionRepository sectionRepository;
    private final CourseLessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public CourseService(
        CourseRepository courseRepository,
        CourseSectionRepository sectionRepository,
        CourseLessonRepository lessonRepository,
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository,
        PaymentRepository paymentRepository,
        OrderRepository orderRepository
    ) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public java.util.Map<String, Object> getCourseContent(Long id, java.security.Principal principal) {
        Course course = courseRepository.findByIdAndPublishedTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        boolean isEnrolled = false;
        if (course.getPrice() == 0 || (course.getFinalPrice() != null && course.getFinalPrice() == 0.0)) {
            isEnrolled = true;
        } else if (principal != null) {
            java.util.Optional<com.lms.Backend.user.entity.User> userOpt = userRepository.findByEmailIgnoreCase(principal.getName());
            if (userOpt.isPresent()) {
                com.lms.Backend.user.entity.User user = userOpt.get();
                if (user.getRole() != null && user.getRole().name().equals("ADMIN")) {
                    isEnrolled = true;
                } else {
                    isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(user.getId(), id);
                }
            }
        }

        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("id", course.getId());
        result.put("title", course.getTitle());
        result.put("slug", course.getSlug());
        result.put("description", course.getDescription());
        result.put("category", course.getCategory());
        result.put("level", course.getLevel());
        result.put("duration", course.getDuration());
        result.put("instructor", course.getInstructor());
        result.put("price", course.getPrice());
        result.put("finalPrice", course.getFinalPrice());
        result.put("isEnrolled", isEnrolled);

        java.util.List<com.lms.Backend.course.entity.CourseSection> sections = sectionRepository.findByCourseIdOrderByDisplayOrderAsc(id);
        java.util.List<java.util.Map<String, Object>> secList = new java.util.ArrayList<>();

        for (com.lms.Backend.course.entity.CourseSection sec : sections) {
            java.util.Map<String, Object> secMap = new java.util.LinkedHashMap<>();
            secMap.put("id", sec.getId());
            secMap.put("title", sec.getTitle());
            secMap.put("description", sec.getDescription());
            secMap.put("displayOrder", sec.getDisplayOrder());

            java.util.List<com.lms.Backend.course.entity.CourseLesson> lessons = lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sec.getId());
            java.util.List<java.util.Map<String, Object>> lesList = new java.util.ArrayList<>();
            for (com.lms.Backend.course.entity.CourseLesson l : lessons) {
                java.util.Map<String, Object> lesMap = new java.util.LinkedHashMap<>();
                lesMap.put("id", l.getId());
                lesMap.put("title", l.getTitle());
                lesMap.put("description", l.getDescription());
                lesMap.put("lessonType", l.getLessonType().name());
                
                boolean canAccessLesson = isEnrolled || l.isFreePreview();
                lesMap.put("contentUrl", canAccessLesson ? l.getContentUrl() : null);
                lesMap.put("locked", !canAccessLesson);
                
                lesMap.put("duration", l.getDuration());
                lesMap.put("freePreview", l.isFreePreview());
                lesMap.put("required", l.isRequired());
                lesMap.put("displayOrder", l.getDisplayOrder());
                lesList.add(lesMap);
            }
            secMap.put("lessons", lesList);
            secList.add(secMap);
        }

        result.put("sections", secList);
        return result;
    }

    public List<CourseResponse> getPublishedCourses(String search) {
        List<Course> courses;
        if (search != null && !search.trim().isEmpty()) {
            courses = courseRepository.searchPublishedCourses(search.trim());
        } else {
            courses = courseRepository.findByPublishedTrueOrderByIdAsc();
        }

        return courses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        return courseRepository.findByIdAndPublishedTrue(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private CourseResponse mapToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnail(),
                course.getCategory(),
                course.getLevel(),
                course.getDuration(),
                course.getInstructor(),
                course.getPrice(),
                course.isPublished(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }

    public CourseEnrollmentStatusResponse getEnrollmentStatus(Long id, Principal principal) {
        Course course = courseRepository.findByIdAndPublishedTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        if (principal == null || principal.getName() == null) {
            return new CourseEnrollmentStatusResponse(
                course.getId(),
                false,
                "NOT_LOGGED_IN",
                null,
                0,
                false,
                false
            );
        }

        User user = userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
        if (user == null) {
            return new CourseEnrollmentStatusResponse(
                course.getId(),
                false,
                "NOT_LOGGED_IN",
                null,
                0,
                false,
                false
            );
        }

        if (user.getRole() != null && user.getRole().name().equals("ADMIN")) {
            return new CourseEnrollmentStatusResponse(
                course.getId(),
                true,
                "ENROLLED",
                "PAID",
                0,
                true,
                false
            );
        }

        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        if (enrollmentOpt.isPresent()) {
            Enrollment e = enrollmentOpt.get();
            int progress = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
            boolean completed = e.getStatus() == EnrollmentStatus.COMPLETED || progress >= 100;
            return new CourseEnrollmentStatusResponse(
                course.getId(),
                true,
                completed ? "COMPLETED" : "ENROLLED",
                "PAID",
                progress,
                true,
                completed
            );
        }

        Optional<Payment> paymentOpt = paymentRepository.findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(user.getId(), course.getId());
        if (paymentOpt.isPresent()) {
            Payment p = paymentOpt.get();
            if (p.getPaymentStatus() == PaymentStatus.PAID) {
                try {
                    enrollmentRepository.save(new Enrollment(user, course));
                } catch (Exception ignored) {}
                return new CourseEnrollmentStatusResponse(
                    course.getId(),
                    true,
                    "ENROLLED",
                    "PAID",
                    0,
                    true,
                    false
                );
            } else if (p.getPaymentStatus() == PaymentStatus.PENDING) {
                return new CourseEnrollmentStatusResponse(
                    course.getId(),
                    false,
                    "NOT_ENROLLED",
                    "PENDING",
                    0,
                    false,
                    false
                );
            } else if (p.getPaymentStatus() == PaymentStatus.FAILED) {
                return new CourseEnrollmentStatusResponse(
                    course.getId(),
                    false,
                    "NOT_ENROLLED",
                    "FAILED",
                    0,
                    false,
                    false
                );
            }
        }

        Optional<Order> orderOpt = orderRepository.findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(user.getId(), course.getId());
        if (orderOpt.isPresent()) {
            Order o = orderOpt.get();
            if (o.getStatus() == OrderStatus.PAID) {
                try {
                    enrollmentRepository.save(new Enrollment(user, course));
                } catch (Exception ignored) {}
                return new CourseEnrollmentStatusResponse(
                    course.getId(),
                    true,
                    "ENROLLED",
                    "PAID",
                    0,
                    true,
                    false
                );
            } else if (o.getStatus() == OrderStatus.PENDING) {
                return new CourseEnrollmentStatusResponse(
                    course.getId(),
                    false,
                    "NOT_ENROLLED",
                    "PENDING",
                    0,
                    false,
                    false
                );
            } else if (o.getStatus() == OrderStatus.FAILED || o.getStatus() == OrderStatus.CANCELLED) {
                return new CourseEnrollmentStatusResponse(
                    course.getId(),
                    false,
                    "NOT_ENROLLED",
                    "FAILED",
                    0,
                    false,
                    false
                );
            }
        }

        return new CourseEnrollmentStatusResponse(
            course.getId(),
            false,
            "NOT_ENROLLED",
            null,
            0,
            false,
            false
        );
    }

    public java.util.Map<String, Object> getCourseLearnAccess(Long id, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new AccessDeniedException("Authentication required to access course content");
        }

        Course course = courseRepository.findByIdAndPublishedTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new AccessDeniedException("Authenticated user record not found"));

        boolean hasAccess = false;
        if (user.getRole() != null && user.getRole().name().equals("ADMIN")) {
            hasAccess = true;
        } else if (course.getPrice() != null && course.getPrice() == 0) {
            hasAccess = true;
        } else {
            hasAccess = enrollmentRepository.existsByUserIdAndCourseId(user.getId(), id);
        }

        if (!hasAccess) {
            throw new AccessDeniedException("Active enrollment required to access course learning materials");
        }

        return getCourseContent(id, principal);
    }
}
