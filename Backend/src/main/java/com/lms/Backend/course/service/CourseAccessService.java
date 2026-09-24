package com.lms.Backend.course.service;

import com.lms.Backend.career.entity.Career;
import com.lms.Backend.career.entity.CareerCourse;
import com.lms.Backend.career.entity.CareerEnrollment;
import com.lms.Backend.career.repository.CareerCourseRepository;
import com.lms.Backend.career.repository.CareerEnrollmentRepository;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.course.dto.CourseAccessDetailDto;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.payment.entity.OrderStatus;
import com.lms.Backend.payment.entity.PaymentStatus;
import com.lms.Backend.payment.repository.OrderRepository;
import com.lms.Backend.payment.repository.PaymentRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CourseAccessService {

    private static final Logger log = LoggerFactory.getLogger(CourseAccessService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final CareerEnrollmentRepository careerEnrollmentRepository;
    private final CareerCourseRepository careerCourseRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public CourseAccessService(
        EnrollmentRepository enrollmentRepository,
        CareerEnrollmentRepository careerEnrollmentRepository,
        CareerCourseRepository careerCourseRepository,
        CourseRepository courseRepository,
        UserRepository userRepository,
        CertificateRepository certificateRepository,
        PaymentRepository paymentRepository,
        OrderRepository orderRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.careerEnrollmentRepository = careerEnrollmentRepository;
        this.careerCourseRepository = careerCourseRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.certificateRepository = certificateRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Unified access check: does this user have access to the course?
     * Returns true if:
     * 1. User is an ADMIN
     * 2. User has an active direct CourseEnrollment
     * 3. User has an active CareerPathEnrollment in a Career Path that maps this course as included
     */
    public boolean hasCourseAccess(UUID userId, Long courseId) {
        if (userId == null || courseId == null) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Condition 1: Direct Course Enrollment
        Optional<Enrollment> directEnrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (directEnrollment.isPresent()) {
            EnrollmentStatus status = directEnrollment.get().getStatus();
            if (status == EnrollmentStatus.ACTIVE || status == EnrollmentStatus.COMPLETED) {
                return true;
            }
        }

        // Direct paid order check fallback
        if (paymentRepository.existsByUserIdAndCourseIdAndPaymentStatus(userId, courseId, PaymentStatus.PAID)) {
            return true;
        }

        // Condition 2: Active Career Path Enrollment including this course
        List<CareerEnrollment> careerEnrollments = careerEnrollmentRepository.findByUserId(userId);
        for (CareerEnrollment ce : careerEnrollments) {
            if (ce.getStatus() == EnrollmentStatus.ACTIVE || ce.getStatus() == EnrollmentStatus.COMPLETED) {
                Career career = ce.getCareer();
                if (career != null) {
                    Optional<CareerCourse> mapping = careerCourseRepository.findByCareerIdAndCourseId(career.getId(), courseId);
                    if (mapping.isPresent() && mapping.get().isIncluded()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns detailed access status for UI rendering (badge, CTA, access type)
     */
    public CourseAccessDetailDto getCourseAccessDetail(UUID userId, Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return new CourseAccessDetailDto(courseId, false, "NONE", null, null, null, null, null, 0, false, false);
        }

        if (userId == null) {
            return new CourseAccessDetailDto(courseId, false, "NONE", null, null, null, null, null, 0, false, false);
        }

        User user = userRepository.findById(userId).orElse(null);
        boolean isAdmin = user != null && user.getRole() == UserRole.ADMIN;

        // 1. Direct course enrollment check
        boolean directEnrolled = false;
        Long directEnrollmentId = null;
        String directStatus = null;
        int progress = 0;
        boolean directCompleted = false;

        Optional<Enrollment> directOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (directOpt.isPresent()) {
            Enrollment e = directOpt.get();
            directEnrollmentId = e.getId();
            directStatus = e.getStatus().name();
            progress = e.getProgressPercentage() != null ? e.getProgressPercentage() : 0;
            directCompleted = e.getStatus() == EnrollmentStatus.COMPLETED || progress >= 100;
            if (e.getStatus() == EnrollmentStatus.ACTIVE || directCompleted) {
                directEnrolled = true;
            }
        } else if (paymentRepository.existsByUserIdAndCourseIdAndPaymentStatus(userId, courseId, PaymentStatus.PAID)) {
            directEnrolled = true;
            directStatus = "PAID";
        }

        // 2. Career Path included check
        boolean careerIncluded = false;
        Long careerId = null;
        String careerName = null;
        String careerSlug = null;

        List<CareerEnrollment> careerEnrollments = careerEnrollmentRepository.findByUserId(userId);
        for (CareerEnrollment ce : careerEnrollments) {
            if (ce.getStatus() == EnrollmentStatus.ACTIVE || ce.getStatus() == EnrollmentStatus.COMPLETED) {
                Career career = ce.getCareer();
                if (career != null) {
                    Optional<CareerCourse> mapping = careerCourseRepository.findByCareerIdAndCourseId(career.getId(), courseId);
                    if (mapping.isPresent() && mapping.get().isIncluded()) {
                        careerIncluded = true;
                        careerId = career.getId();
                        careerName = career.getTitle();
                        careerSlug = career.getSlug();
                        break;
                    }
                }
            }
        }

        // Determine Access Type
        String accessType = "NONE";
        boolean hasAccess = isAdmin || directEnrolled || careerIncluded;

        if (isAdmin) {
            accessType = directEnrolled ? "BOTH" : "DIRECT_COURSE";
        } else if (directEnrolled && careerIncluded) {
            accessType = "BOTH";
        } else if (directEnrolled) {
            accessType = "DIRECT_COURSE";
        } else if (careerIncluded) {
            accessType = "CAREER_PATH_INCLUDED";
        }

        boolean certAvailable = certificateRepository.existsByUserIdAndCourseId(userId, courseId);

        return new CourseAccessDetailDto(
            courseId,
            hasAccess,
            accessType,
            careerId,
            careerName,
            careerSlug,
            directEnrollmentId,
            directStatus,
            progress,
            directCompleted,
            certAvailable
        );
    }
}
