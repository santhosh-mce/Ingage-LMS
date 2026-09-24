package com.lms.Backend.admin.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.entity.CourseSection;
import com.lms.Backend.course.entity.CourseStatus;
import com.lms.Backend.course.entity.DiscountType;
import com.lms.Backend.course.entity.LessonType;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.course.repository.CourseSectionRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.payment.entity.Payment;
import com.lms.Backend.payment.entity.PaymentStatus;
import com.lms.Backend.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminCourseService {

    private final CourseRepository courseRepository;
    private final CourseSectionRepository sectionRepository;
    private final CourseLessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final CertificateRepository certificateRepository;
    private final com.lms.Backend.career.repository.CareerCourseRepository careerCourseRepository;

    public AdminCourseService(
        CourseRepository courseRepository,
        CourseSectionRepository sectionRepository,
        CourseLessonRepository lessonRepository,
        EnrollmentRepository enrollmentRepository,
        PaymentRepository paymentRepository,
        CertificateRepository certificateRepository,
        com.lms.Backend.career.repository.CareerCourseRepository careerCourseRepository
    ) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
        this.certificateRepository = certificateRepository;
        this.careerCourseRepository = careerCourseRepository;
    }

    public List<Map<String, Object>> getAllCourses(String search, String statusFilter, String categoryFilter) {
        List<Course> courses;
        if (search != null && !search.trim().isEmpty()) {
            courses = courseRepository.searchAllCourses(search.trim());
        } else {
            courses = courseRepository.findAllByOrderByIdDesc();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL") && !statusFilter.isBlank()) {
                if (c.getStatus() == null || !c.getStatus().name().equalsIgnoreCase(statusFilter)) {
                    continue;
                }
            }

            if (categoryFilter != null && !categoryFilter.equalsIgnoreCase("ALL") && !categoryFilter.isBlank()) {
                if (c.getCategory() == null || !c.getCategory().equalsIgnoreCase(categoryFilter.trim())) {
                    continue;
                }
            }

            long enrolledCount = enrollmentRepository.countByCourseId(c.getId());
            long completedCount = enrollmentRepository.countByCourseIdAndStatus(c.getId(), EnrollmentStatus.COMPLETED);
            double completionRate = enrolledCount > 0 ? (double) completedCount / enrolledCount * 100.0 : 0.0;
            Double revenue = paymentRepository.getRevenueByCourseId(c.getId());

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", c.getId());
            dto.put("title", c.getTitle());
            dto.put("slug", c.getSlug() != null ? c.getSlug() : "course-" + c.getId());
            dto.put("description", c.getDescription());
            dto.put("shortDescription", c.getShortDescription());
            dto.put("category", c.getCategory());
            dto.put("level", c.getLevel());
            dto.put("duration", c.getDuration());
            dto.put("instructor", c.getInstructor());
            dto.put("price", c.getPrice());
            dto.put("discountType", c.getDiscountType() != null ? c.getDiscountType().name() : null);
            dto.put("discountValue", c.getDiscountValue());
            dto.put("finalPrice", c.getFinalPrice());
            dto.put("currency", c.getCurrency());
            dto.put("thumbnail", c.getThumbnail());
            dto.put("banner", c.getBanner());
            dto.put("language", c.getLanguage());
            dto.put("status", c.getStatus().name());
            dto.put("published", c.isPublished());
            dto.put("students", enrolledCount);
            dto.put("completedStudents", completedCount);
            dto.put("completionRate", Math.round(completionRate * 10.0) / 10.0);
            dto.put("revenue", revenue != null ? revenue : 0.0);
            dto.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : "");
            dto.put("updatedAt", c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : "");

            result.add(dto);
        }
        return result;
    }

    public Map<String, Object> getCourseDetailsWithContent(Long courseId) {
        Course c = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("id", c.getId());
        details.put("title", c.getTitle());
        details.put("slug", c.getSlug());
        details.put("shortDescription", c.getShortDescription());
        details.put("description", c.getDescription());
        details.put("category", c.getCategory());
        details.put("level", c.getLevel());
        details.put("language", c.getLanguage());
        details.put("duration", c.getDuration());
        details.put("instructor", c.getInstructor());
        details.put("price", c.getPrice());
        details.put("discountType", c.getDiscountType() != null ? c.getDiscountType().name() : null);
        details.put("discountValue", c.getDiscountValue());
        details.put("finalPrice", c.getFinalPrice());
        details.put("currency", c.getCurrency());
        details.put("thumbnail", c.getThumbnail());
        details.put("banner", c.getBanner());
        details.put("status", c.getStatus().name());
        details.put("published", c.isPublished());

        List<CourseSection> sections = sectionRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
        List<Map<String, Object>> sectionList = new ArrayList<>();

        for (CourseSection sec : sections) {
            Map<String, Object> secMap = new LinkedHashMap<>();
            secMap.put("id", sec.getId());
            secMap.put("title", sec.getTitle());
            secMap.put("description", sec.getDescription());
            secMap.put("displayOrder", sec.getDisplayOrder());

            List<CourseLesson> lessons = lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sec.getId());
            List<Map<String, Object>> lessonList = new ArrayList<>();
            for (CourseLesson l : lessons) {
                Map<String, Object> lesMap = new LinkedHashMap<>();
                lesMap.put("id", l.getId());
                lesMap.put("title", l.getTitle());
                lesMap.put("description", l.getDescription());
                lesMap.put("lessonType", l.getLessonType().name());
                lesMap.put("contentUrl", l.getContentUrl());
                lesMap.put("duration", l.getDuration());
                lesMap.put("durationSeconds", l.getDurationSeconds());
                lesMap.put("fileSize", l.getFileSize());
                lesMap.put("freePreview", l.isFreePreview());
                lesMap.put("required", l.isRequired());
                lesMap.put("displayOrder", l.getDisplayOrder());
                lessonList.add(lesMap);
            }
            secMap.put("lessons", lessonList);
            sectionList.add(secMap);
        }

        details.put("sections", sectionList);
        return details;
    }

    @Transactional
    public Course createCourse(Map<String, Object> payload) {
        validateCourseData(payload, true, null);

        String title = ((String) payload.get("title")).trim();
        Course course = new Course();
        course.setTitle(title);
        course.setSlug(generateSlug(title, (String) payload.get("slug"), null));
        course.setShortDescription((String) payload.get("shortDescription"));
        course.setDescription(((String) payload.get("description")).trim());
        course.setCategory(((String) payload.get("category")).trim());
        course.setLevel((String) payload.getOrDefault("level", "Beginner"));
        course.setDuration((String) payload.getOrDefault("duration", "8 weeks"));
        course.setInstructor((String) payload.getOrDefault("instructor", "Ingage Lead Mentor"));
        course.setThumbnail((String) payload.get("thumbnail"));
        course.setBanner((String) payload.get("banner"));
        course.setLanguage((String) payload.getOrDefault("language", "English"));

        Object priceObj = payload.get("price");
        int price = priceObj != null ? Integer.parseInt(priceObj.toString()) : 0;
        course.setPrice(price);

        String discTypeStr = (String) payload.get("discountType");
        if (discTypeStr != null && !discTypeStr.isBlank()) {
            course.setDiscountType(DiscountType.valueOf(discTypeStr.toUpperCase()));
        }
        Object discValObj = payload.get("discountValue");
        if (discValObj != null) {
            course.setDiscountValue(Double.parseDouble(discValObj.toString()));
        }

        String statusStr = (String) payload.getOrDefault("status", "DRAFT");
        CourseStatus status = CourseStatus.valueOf(statusStr.toUpperCase());
        course.setStatus(status);
        course.setPublished(status == CourseStatus.PUBLISHED);
        course.calculateFinalPrice();

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long id, Map<String, Object> payload) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));

        validateCourseData(payload, false, course.getPrice());

        if (payload.containsKey("title")) course.setTitle(((String) payload.get("title")).trim());
        if (payload.containsKey("slug")) course.setSlug(generateSlug(course.getTitle(), (String) payload.get("slug"), course.getId()));
        if (payload.containsKey("shortDescription")) course.setShortDescription((String) payload.get("shortDescription"));
        if (payload.containsKey("description")) course.setDescription(((String) payload.get("description")).trim());
        if (payload.containsKey("category")) course.setCategory(((String) payload.get("category")).trim());
        if (payload.containsKey("level")) course.setLevel((String) payload.get("level"));
        if (payload.containsKey("duration")) course.setDuration((String) payload.get("duration"));
        if (payload.containsKey("instructor")) course.setInstructor((String) payload.get("instructor"));
        if (payload.containsKey("thumbnail")) course.setThumbnail((String) payload.get("thumbnail"));
        if (payload.containsKey("banner")) course.setBanner((String) payload.get("banner"));
        if (payload.containsKey("language")) course.setLanguage((String) payload.get("language"));

        if (payload.containsKey("price")) {
            int p = Integer.parseInt(payload.get("price").toString());
            course.setPrice(p);
        }

        if (payload.containsKey("discountType")) {
            String dt = (String) payload.get("discountType");
            course.setDiscountType(dt != null && !dt.isBlank() ? DiscountType.valueOf(dt.toUpperCase()) : null);
        }

        if (payload.containsKey("discountValue")) {
            Object dv = payload.get("discountValue");
            course.setDiscountValue(dv != null ? Double.parseDouble(dv.toString()) : 0.0);
        }

        if (payload.containsKey("status")) {
            CourseStatus status = CourseStatus.valueOf(((String) payload.get("status")).toUpperCase());
            course.setStatus(status);
            course.setPublished(status == CourseStatus.PUBLISHED);
        }

        course.calculateFinalPrice();
        return courseRepository.save(course);
    }

    private void validateCourseData(Map<String, Object> payload, boolean isCreate, Integer existingPrice) {
        if (isCreate || payload.containsKey("title")) {
            String title = (String) payload.get("title");
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Course title is required and cannot be empty");
            }
        }

        if (isCreate || payload.containsKey("description")) {
            String desc = (String) payload.get("description");
            if (desc == null || desc.trim().isEmpty()) {
                throw new IllegalArgumentException("Course description is required and cannot be empty");
            }
        }

        if (isCreate || payload.containsKey("category")) {
            String cat = (String) payload.get("category");
            if (cat == null || cat.trim().isEmpty()) {
                throw new IllegalArgumentException("Course category is required");
            }
        }

        int effectivePrice = existingPrice != null ? existingPrice : 0;
        if (payload.containsKey("price")) {
            Object priceObj = payload.get("price");
            if (priceObj == null) {
                throw new IllegalArgumentException("Valid price is required");
            }
            effectivePrice = Integer.parseInt(priceObj.toString());
            if (effectivePrice < 0) {
                throw new IllegalArgumentException("Course price must not be negative");
            }
        } else if (isCreate && !payload.containsKey("price")) {
            throw new IllegalArgumentException("Course price is required");
        }

        if (payload.containsKey("discountValue")) {
            Object discValObj = payload.get("discountValue");
            if (discValObj != null) {
                double discVal = Double.parseDouble(discValObj.toString());
                if (discVal < 0) {
                    throw new IllegalArgumentException("Discount price/value must not be negative");
                }
                String discTypeStr = (String) payload.get("discountType");
                if ("PERCENTAGE".equalsIgnoreCase(discTypeStr) && discVal > 100.0) {
                    throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
                }
                if ("FIXED_AMOUNT".equalsIgnoreCase(discTypeStr) && discVal > effectivePrice) {
                    throw new IllegalArgumentException("Fixed discount amount cannot exceed original course price");
                }
            }
        }
    }

    @Transactional
    public Map<String, Object> publishCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));

        List<String> errors = new ArrayList<>();
        if (course.getTitle() == null || course.getTitle().isBlank()) errors.add("Course title is required");
        if (course.getDescription() == null || course.getDescription().isBlank()) errors.add("Course description is required");
        if (course.getCategory() == null || course.getCategory().isBlank()) errors.add("Category is required");
        if (course.getPrice() == null || course.getPrice() < 0) errors.add("Valid price is required");
        if (course.getThumbnail() == null || course.getThumbnail().isBlank()) errors.add("Course thumbnail is required");

        long sectionCount = sectionRepository.countByCourseId(id);
        if (sectionCount == 0) errors.add("Course must have at least one section");

        long lessonCount = lessonRepository.countBySection_Course_Id(id);
        if (lessonCount == 0) errors.add("Course must have at least one lesson");

        if (!errors.isEmpty()) {
            return Map.of("success", false, "errors", errors, "message", "Validation failed before publishing");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        course.setPublished(true);
        courseRepository.save(course);
        return Map.of("success", true, "message", "Course published successfully!");
    }

    @Transactional
    public void unpublishCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        course.setStatus(CourseStatus.UNPUBLISHED);
        course.setPublished(false);
        courseRepository.save(course);
    }

    @Transactional
    public Map<String, Object> archiveCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        course.setStatus(CourseStatus.ARCHIVED);
        course.setPublished(false);
        courseRepository.save(course);
        return Map.of("archived", true, "message", "Course archived successfully. Existing enrollments and certificates are preserved.");
    }

    @Transactional
    public Map<String, Object> deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));

        long enrollments = enrollmentRepository.countByCourseId(id);
        long payments = paymentRepository.countByCourseId(id);
        long certificates = certificateRepository.countByCourseId(id);

        if (enrollments > 0 || payments > 0 || certificates > 0) {
            course.setStatus(CourseStatus.ARCHIVED);
            course.setPublished(false);
            courseRepository.save(course);
            return Map.of("archived", true, "message", "Course has active enrollments/payments/certificates. Safely changed status to ARCHIVED instead of deletion.");
        } else {
            // Delete career course mappings first to prevent FK violation
            careerCourseRepository.deleteByCourseId(id);
            // Delete lessons and sections then course
            List<CourseSection> sections = sectionRepository.findByCourseIdOrderByDisplayOrderAsc(id);
            for (CourseSection s : sections) {
                List<CourseLesson> lessons = lessonRepository.findBySectionIdOrderByDisplayOrderAsc(s.getId());
                lessonRepository.deleteAll(lessons);
            }
            sectionRepository.deleteAll(sections);
            courseRepository.delete(course);
            return Map.of("deleted", true, "message", "Course removed completely.");
        }
    }

    // Sections & Lessons Builder
    @Transactional
    public CourseSection addSection(Long courseId, Map<String, Object> payload) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        String title = (String) payload.get("title");
        String desc = (String) payload.get("description");
        int count = (int) sectionRepository.countByCourseId(courseId);
        CourseSection section = new CourseSection(course, title, desc, count);
        return sectionRepository.save(section);
    }

    @Transactional
    public CourseSection updateSection(Long sectionId, Map<String, Object> payload) {
        CourseSection section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));
        if (payload.containsKey("title")) section.setTitle((String) payload.get("title"));
        if (payload.containsKey("description")) section.setDescription((String) payload.get("description"));
        if (payload.containsKey("displayOrder")) section.setDisplayOrder((Integer) payload.get("displayOrder"));
        return sectionRepository.save(section);
    }

    @Transactional
    public void deleteSection(Long sectionId) {
        List<CourseLesson> lessons = lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sectionId);
        lessonRepository.deleteAll(lessons);
        sectionRepository.deleteById(sectionId);
    }

    @Transactional
    public CourseLesson addLesson(Long sectionId, Map<String, Object> payload) {
        CourseSection section = sectionRepository.findById(sectionId)
            .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));

        CourseLesson lesson = new CourseLesson();
        lesson.setSection(section);
        lesson.setTitle((String) payload.get("title"));
        lesson.setDescription((String) payload.get("description"));

        String typeStr = (String) payload.getOrDefault("lessonType", "VIDEO");
        lesson.setLessonType(LessonType.valueOf(typeStr.toUpperCase()));
        lesson.setContentUrl((String) payload.get("contentUrl"));
        lesson.setDuration((String) payload.get("duration"));

        if (payload.containsKey("durationSeconds")) {
            lesson.setDurationSeconds(Integer.parseInt(payload.get("durationSeconds").toString()));
        }
        if (payload.containsKey("fileSize")) {
            lesson.setFileSize(Long.parseLong(payload.get("fileSize").toString()));
        }
        if (payload.containsKey("freePreview")) {
            lesson.setFreePreview(Boolean.parseBoolean(payload.get("freePreview").toString()));
        }
        if (payload.containsKey("required")) {
            lesson.setRequired(Boolean.parseBoolean(payload.get("required").toString()));
        }

        int count = lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sectionId).size();
        lesson.setDisplayOrder(count);

        return lessonRepository.save(lesson);
    }

    @Transactional
    public CourseLesson updateLesson(Long lessonId, Map<String, Object> payload) {
        CourseLesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));

        if (payload.containsKey("title")) lesson.setTitle((String) payload.get("title"));
        if (payload.containsKey("description")) lesson.setDescription((String) payload.get("description"));
        if (payload.containsKey("lessonType")) lesson.setLessonType(LessonType.valueOf(((String) payload.get("lessonType")).toUpperCase()));
        if (payload.containsKey("contentUrl")) lesson.setContentUrl((String) payload.get("contentUrl"));
        if (payload.containsKey("duration")) lesson.setDuration((String) payload.get("duration"));
        if (payload.containsKey("durationSeconds")) lesson.setDurationSeconds(Integer.parseInt(payload.get("durationSeconds").toString()));
        if (payload.containsKey("fileSize")) lesson.setFileSize(Long.parseLong(payload.get("fileSize").toString()));
        if (payload.containsKey("freePreview")) lesson.setFreePreview(Boolean.parseBoolean(payload.get("freePreview").toString()));
        if (payload.containsKey("required")) lesson.setRequired(Boolean.parseBoolean(payload.get("required").toString()));
        if (payload.containsKey("displayOrder")) lesson.setDisplayOrder((Integer) payload.get("displayOrder"));

        return lessonRepository.save(lesson);
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    public Map<String, Object> getCourseAnalytics(Long courseId) {
        Course c = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        long totalEnrolled = enrollmentRepository.countByCourseId(courseId);
        long activeLearners = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.ACTIVE);
        long completedLearners = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.COMPLETED);
        double completionRate = totalEnrolled > 0 ? (double) completedLearners / totalEnrolled * 100.0 : 0.0;
        Double avgProgress = enrollmentRepository.getAverageProgressByCourseId(courseId);
        Double revenue = paymentRepository.getRevenueByCourseId(courseId);
        long totalPayments = paymentRepository.countByCourseIdAndPaymentStatus(courseId, PaymentStatus.PAID);
        long certificatesIssued = certificateRepository.countByCourseId(courseId);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("courseId", c.getId());
        data.put("courseTitle", c.getTitle());
        data.put("totalEnrolled", totalEnrolled);
        data.put("activeLearners", activeLearners);
        data.put("completedLearners", completedLearners);
        data.put("completionRate", Math.round(completionRate * 10.0) / 10.0);
        data.put("averageProgress", avgProgress != null ? Math.round(avgProgress * 10.0) / 10.0 : 0.0);
        data.put("totalRevenue", revenue != null ? revenue : 0.0);
        data.put("totalPayments", totalPayments);
        data.put("refunds", 0);
        data.put("certificatesIssued", certificatesIssued);
        return data;
    }

    public List<Map<String, Object>> getCourseStudents(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdOrderByEnrolledAtDesc(courseId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Map<String, Object> student = new LinkedHashMap<>();
            student.put("enrollmentId", e.getId());
            student.put("userId", e.getUser().getId());
            student.put("userName", e.getUser().getName());
            student.put("userEmail", e.getUser().getEmail());
            student.put("enrollmentDate", e.getEnrolledAt().toString());
            student.put("progress", e.getProgressPercentage());
            student.put("lastAccessed", e.getLastAccessedAt() != null ? e.getLastAccessedAt().toString() : "");
            student.put("completed", e.getStatus() == EnrollmentStatus.COMPLETED);
            student.put("completionDate", e.getCompletedAt() != null ? e.getCompletedAt().toString() : "");

            Optional<Certificate> cert = certificateRepository.findByUserIdAndCourseId(e.getUser().getId(), courseId);
            student.put("hasCertificate", cert.isPresent());
            student.put("certificateNumber", cert.map(Certificate::getCertificateNumber).orElse(null));
            student.put("verificationCode", cert.map(Certificate::getVerificationCode).orElse(null));

            student.put("paymentStatus", e.getCourse().getPrice() == 0 ? "FREE" : "PAID");
            result.add(student);
        }
        return result;
    }

    private String generateSlug(String title, String customSlug, Long currentCourseId) {
        String baseSlug;
        if (customSlug != null && !customSlug.isBlank()) {
            baseSlug = customSlug.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        } else if (title != null && !title.isBlank()) {
            baseSlug = title.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        } else {
            baseSlug = "course";
        }
        if (baseSlug.isBlank()) {
            baseSlug = "course";
        }

        String candidate = baseSlug;
        int counter = 2;
        while (true) {
            Optional<Course> existing = courseRepository.findBySlug(candidate);
            if (existing.isEmpty() || (currentCourseId != null && existing.get().getId().equals(currentCourseId))) {
                return candidate;
            }
            candidate = baseSlug + "-" + counter;
            counter++;
        }
    }
}
