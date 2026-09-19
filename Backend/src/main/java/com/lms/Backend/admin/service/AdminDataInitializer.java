package com.lms.Backend.admin.service;

import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseCategory;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.entity.CourseSection;
import com.lms.Backend.course.entity.CourseStatus;
import com.lms.Backend.course.entity.DiscountType;
import com.lms.Backend.course.entity.LessonType;
import com.lms.Backend.course.repository.CourseCategoryRepository;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.course.repository.CourseSectionRepository;
import com.lms.Backend.discount.entity.Discount;
import com.lms.Backend.discount.repository.DiscountRepository;
import com.lms.Backend.user.entity.AuthProvider;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Order(3)
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseSectionRepository sectionRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseCategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(
        UserRepository userRepository,
        CourseRepository courseRepository,
        CourseSectionRepository sectionRepository,
        CourseLessonRepository lessonRepository,
        CourseCategoryRepository categoryRepository,
        DiscountRepository discountRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.categoryRepository = categoryRepository;
        this.discountRepository = discountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdminUser();
        initCategories();
        initDiscounts();
        initCourseCurriculum();
    }

    private void initAdminUser() {
        if (!userRepository.existsByEmailIgnoreCase("admin@ingage.com")) {
            User admin = new User();
            admin.setName("Admin Ingage");
            admin.setEmail("admin@ingage.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setRole(UserRole.ADMIN);
            admin.setProvider(AuthProvider.LOCAL);
            admin.setEmailVerified(true);
            admin.setActive(true);
            admin.setPhone("+91 9876543210");
            admin.setLastLogin(Instant.now());
            userRepository.save(admin);
            log.info("[AdminDataInitializer] Created default Admin user: admin@ingage.com / Admin@123");
        }
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new CourseCategory("Data Science & AI", "data-science-ai", "Machine Learning, Analytics & Deep Learning", 1));
            categoryRepository.save(new CourseCategory("Software Development", "software-development", "Full Stack, Frontend, Backend & Mobile", 2));
            categoryRepository.save(new CourseCategory("Cloud & DevOps", "cloud-devops", "AWS, Docker, Kubernetes & CI/CD", 3));
            categoryRepository.save(new CourseCategory("Design & UI/UX", "design-ui-ux", "Figma, User Research & Interaction Design", 4));
            categoryRepository.save(new CourseCategory("Business & Product", "business-product", "Product Management, Agile & Strategy", 5));
            log.info("[AdminDataInitializer] Seeded 5 initial course categories.");
        }
    }

    private void initDiscounts() {
        if (discountRepository.count() == 0) {
            Instant now = Instant.now();
            Instant nextYear = now.plus(365, ChronoUnit.DAYS);

            Discount d1 = new Discount(
                "WELCOME20",
                DiscountType.PERCENTAGE,
                20.0,
                now,
                nextYear,
                500,
                1,
                499.0,
                2000.0,
                true
            );
            discountRepository.save(d1);

            Discount d2 = new Discount(
                "INGAGE500",
                DiscountType.FIXED_AMOUNT,
                500.0,
                now,
                nextYear,
                200,
                1,
                1499.0,
                500.0,
                true
            );
            discountRepository.save(d2);

            log.info("[AdminDataInitializer] Seeded initial discounts: WELCOME20, INGAGE500.");
        }
    }

    private void initCourseCurriculum() {
        List<Course> courses = courseRepository.findAll();
        for (Course course : courses) {
            if (sectionRepository.countByCourseId(course.getId()) == 0) {
                // Create Module 1
                CourseSection section1 = new CourseSection(
                    course,
                    "Module 1: Foundations & Architecture",
                    "Core concepts, tools, and initial hands-on environment setup.",
                    0
                );
                CourseSection s1 = sectionRepository.save(section1);

                // Add lessons to Module 1
                CourseLesson l1 = new CourseLesson();
                l1.setSection(s1);
                l1.setTitle("1.1 Course Orientation & Objectives");
                l1.setDescription("Overview of course syllabus, learning outcomes, and setup requirements.");
                l1.setLessonType(LessonType.VIDEO);
                l1.setContentUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
                l1.setDuration("12m 45s");
                l1.setDurationSeconds(765);
                l1.setFreePreview(true);
                l1.setRequired(true);
                l1.setDisplayOrder(0);
                lessonRepository.save(l1);

                CourseLesson l2 = new CourseLesson();
                l2.setSection(s1);
                l2.setTitle("1.2 Core Architectural Principles");
                l2.setDescription("Detailed reading and architectural diagrams on foundational patterns.");
                l2.setLessonType(LessonType.TEXT);
                l2.setContentUrl("#architectural-patterns");
                l2.setDuration("18m");
                l2.setDurationSeconds(1080);
                l2.setFreePreview(false);
                l2.setRequired(true);
                l2.setDisplayOrder(1);
                lessonRepository.save(l2);

                // Create Module 2
                CourseSection section2 = new CourseSection(
                    course,
                    "Module 2: Applied Implementations & Practice",
                    "Deep-dive implementation, practical code walkthroughs, and assessment.",
                    1
                );
                CourseSection s2 = sectionRepository.save(section2);

                CourseLesson l3 = new CourseLesson();
                l3.setSection(s2);
                l3.setTitle("2.1 Production-Grade Code Walkthrough");
                l3.setDescription("Step-by-step video demonstration of end-to-end features.");
                l3.setLessonType(LessonType.VIDEO);
                l3.setContentUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
                l3.setDuration("24m 10s");
                l3.setDurationSeconds(1450);
                l3.setFreePreview(false);
                l3.setRequired(true);
                l3.setDisplayOrder(0);
                lessonRepository.save(l3);

                CourseLesson l4 = new CourseLesson();
                l4.setSection(s2);
                l4.setTitle("2.2 Mastery Quiz & Certification Assessment");
                l4.setDescription("Comprehensive quiz evaluating module comprehension.");
                l4.setLessonType(LessonType.QUIZ);
                l4.setContentUrl("#quiz-module-2");
                l4.setDuration("15m");
                l4.setDurationSeconds(900);
                l4.setFreePreview(false);
                l4.setRequired(true);
                l4.setDisplayOrder(1);
                lessonRepository.save(l4);
            }
        }
    }
}
