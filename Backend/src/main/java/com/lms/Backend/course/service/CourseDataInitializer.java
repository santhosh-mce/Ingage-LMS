package com.lms.Backend.course.service;

import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Order(1)
@Component
public class CourseDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CourseDataInitializer.class);
    private final CourseRepository courseRepository;

    public CourseDataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Course seeding is disabled. Courses are dynamically managed via Admin Control Center.");
    }
}
