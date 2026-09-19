package com.lms.Backend.admin.service;

import com.lms.Backend.career.entity.Career;
import com.lms.Backend.career.entity.CareerCourse;
import com.lms.Backend.career.repository.CareerCourseRepository;
import com.lms.Backend.career.repository.CareerRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminCareerService {

    private final CareerRepository careerRepository;
    private final CareerCourseRepository careerCourseRepository;
    private final CourseRepository courseRepository;

    public AdminCareerService(
        CareerRepository careerRepository,
        CareerCourseRepository careerCourseRepository,
        CourseRepository courseRepository
    ) {
        this.careerRepository = careerRepository;
        this.careerCourseRepository = careerCourseRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllCareers() {
        List<Career> careers = careerRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Career c : careers) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", c.getId());
            dto.put("title", c.getTitle());
            dto.put("slug", c.getSlug());
            dto.put("category", c.getCategory());
            dto.put("level", c.getLevel());
            dto.put("duration", c.getDuration());
            dto.put("salaryMin", c.getSalaryMin());
            dto.put("salaryMax", c.getSalaryMax());
            dto.put("salaryCurrency", c.getSalaryCurrency());
            dto.put("avgSalary", (c.getSalaryMin() != null && c.getSalaryMax() != null)
                ? "₹" + (c.getSalaryMin() / 100000) + "–" + (c.getSalaryMax() / 100000) + " LPA"
                : "");
            dto.put("jobOpenings", c.getJobOpenings());
            dto.put("description", c.getDescription());
            dto.put("featured", c.isFeatured());
            dto.put("popular", c.isPopular());
            dto.put("published", c.isPublished());
            dto.put("active", c.isActive());
            dto.put("icon", c.getIcon());
            dto.put("skillsCount", c.getSkills() != null ? c.getSkills().size() : 0);

            List<CareerCourse> courses = careerCourseRepository.findByCareerIdOrderByDisplayOrderAsc(c.getId());
            dto.put("assignedCoursesCount", courses.size());

            List<Map<String, Object>> courseList = new ArrayList<>();
            for (CareerCourse cc : courses) {
                Map<String, Object> cMap = new LinkedHashMap<>();
                cMap.put("id", cc.getCourse().getId());
                cMap.put("title", cc.getCourse().getTitle());
                cMap.put("price", cc.getCourse().getPrice());
                courseList.add(cMap);
            }
            dto.put("assignedCourses", courseList);

            result.add(dto);
        }
        return result;
    }

    @Transactional
    public Career createCareer(Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String slug = (String) payload.get("slug");
        if (slug == null || slug.isBlank()) {
            slug = title.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }

        Career career = new Career();
        career.setTitle(title);
        career.setSlug(slug);
        career.setCategory((String) payload.get("category"));
        career.setLevel((String) payload.get("level"));
        career.setDuration((String) payload.get("duration"));

        Object sMin = payload.get("salaryMin");
        career.setSalaryMin(sMin != null ? Long.parseLong(sMin.toString()) : 600000L);
        Object sMax = payload.get("salaryMax");
        career.setSalaryMax(sMax != null ? Long.parseLong(sMax.toString()) : 1200000L);

        career.setJobOpenings((String) payload.getOrDefault("jobOpenings", "10,000+"));
        career.setDescription((String) payload.get("description"));
        career.setImageUrl((String) payload.get("imageUrl"));
        career.setIcon((String) payload.getOrDefault("icon", "Briefcase"));
        career.setFeatured(Boolean.parseBoolean(payload.getOrDefault("featured", "false").toString()));
        career.setPopular(Boolean.parseBoolean(payload.getOrDefault("popular", "false").toString()));
        career.setActive(Boolean.parseBoolean(payload.getOrDefault("active", "true").toString()));
        career.setPublished(Boolean.parseBoolean(payload.getOrDefault("published", "true").toString()));

        return careerRepository.save(career);
    }

    @Transactional
    public Career updateCareer(Long id, Map<String, Object> payload) {
        Career career = careerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Career not found: " + id));

        if (payload.containsKey("title")) career.setTitle((String) payload.get("title"));
        if (payload.containsKey("slug")) career.setSlug((String) payload.get("slug"));
        if (payload.containsKey("category")) career.setCategory((String) payload.get("category"));
        if (payload.containsKey("level")) career.setLevel((String) payload.get("level"));
        if (payload.containsKey("duration")) career.setDuration((String) payload.get("duration"));
        if (payload.containsKey("salaryMin")) career.setSalaryMin(Long.parseLong(payload.get("salaryMin").toString()));
        if (payload.containsKey("salaryMax")) career.setSalaryMax(Long.parseLong(payload.get("salaryMax").toString()));
        if (payload.containsKey("jobOpenings")) career.setJobOpenings((String) payload.get("jobOpenings"));
        if (payload.containsKey("description")) career.setDescription((String) payload.get("description"));
        if (payload.containsKey("imageUrl")) career.setImageUrl((String) payload.get("imageUrl"));
        if (payload.containsKey("icon")) career.setIcon((String) payload.get("icon"));
        if (payload.containsKey("featured")) career.setFeatured(Boolean.parseBoolean(payload.get("featured").toString()));
        if (payload.containsKey("popular")) career.setPopular(Boolean.parseBoolean(payload.get("popular").toString()));
        if (payload.containsKey("published")) career.setPublished(Boolean.parseBoolean(payload.get("published").toString()));
        if (payload.containsKey("active")) career.setActive(Boolean.parseBoolean(payload.get("active").toString()));

        return careerRepository.save(career);
    }

    @Transactional
    public Career togglePublish(Long id, Boolean published) {
        Career career = careerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Career not found: " + id));
        career.setPublished(published != null ? published : !career.isPublished());
        return careerRepository.save(career);
    }

    @Transactional
    public Career toggleStatus(Long id, Boolean active) {
        Career career = careerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Career not found: " + id));
        career.setActive(active != null ? active : !career.isActive());
        return careerRepository.save(career);
    }

    @Transactional
    public void deleteCareer(Long id) {
        careerCourseRepository.deleteByCareerId(id);
        careerRepository.deleteById(id);
    }

    @Transactional
    public void assignCourseToCareer(Long careerId, Long courseId) {
        Career career = careerRepository.findById(careerId)
            .orElseThrow(() -> new IllegalArgumentException("Career not found: " + careerId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (!careerCourseRepository.existsByCareerIdAndCourseId(careerId, courseId)) {
            int order = (int) careerCourseRepository.countByCareerId(careerId);
            CareerCourse cc = new CareerCourse(career, course, order);
            careerCourseRepository.save(cc);
        }
    }

    @Transactional
    public void removeCourseFromCareer(Long careerId, Long courseId) {
        List<CareerCourse> list = careerCourseRepository.findByCareerIdOrderByDisplayOrderAsc(careerId);
        for (CareerCourse cc : list) {
            if (cc.getCourse().getId().equals(courseId)) {
                careerCourseRepository.delete(cc);
            }
        }
    }
}
