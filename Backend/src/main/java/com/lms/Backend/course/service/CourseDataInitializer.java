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
        log.info("Checking initial course seed data...");

        List<Course> initialCourses = List.of(
            new Course(
                "Full Stack Development",
                "Learn modern frontend and backend development with React, Node.js, TypeScript, and relational databases.",
                "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80",
                "Web Development",
                "Beginner",
                "8 weeks",
                "Alex Rivera",
                4999,
                true
            ),
            new Course(
                "Java Spring Boot Mastery",
                "Build scalable enterprise REST APIs, microservices, and database layers with Spring Boot, Spring Security, and PostgreSQL.",
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=800&q=80",
                "Backend Development",
                "Intermediate",
                "6 weeks",
                "Marcus Vance",
                3999,
                true
            ),
            new Course(
                "React Development & Modern UI",
                "Build high-performance web applications using modern React, custom hooks, Tailwind CSS, and global state management.",
                "https://images.unsplash.com/photo-1633356122544-f134324a6cee?auto=format&fit=crop&w=800&q=80",
                "Frontend Development",
                "Beginner",
                "5 weeks",
                "Elena Rostova",
                3499,
                true
            ),
            new Course(
                "Python Backend Development",
                "Develop clean, asynchronous backends and automated pipelines with Python, FastAPI, SQLAlchemy, and Docker.",
                "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80",
                "Backend Development",
                "Intermediate",
                "7 weeks",
                "David Chen",
                4499,
                true
            ),
            new Course(
                "Data Analytics with SQL & Tableau",
                "Extract business intelligence, run advanced SQL aggregations, and build interactive executive dashboards.",
                "https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=800&q=80",
                "Data & Analytics",
                "Beginner",
                "6 weeks",
                "Priya Sharma",
                3799,
                true
            ),
            new Course(
                "DevOps & Cloud Infrastructure",
                "Master CI/CD pipelines, container orchestration with Kubernetes, Docker, and infrastructure as code.",
                "https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?auto=format&fit=crop&w=800&q=80",
                "Cloud & DevOps",
                "Intermediate",
                "8 weeks",
                "Sarah Jenkins",
                5499,
                true
            ),
            new Course(
                "Advanced Java Microservices Architecture",
                "Deep dive into distributed systems, event-driven architecture with Kafka, resilience patterns, and performance tuning.",
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=800&q=80",
                "Backend Development",
                "Advanced",
                "10 weeks",
                "Marcus Vance",
                5999,
                true
            ),
            // Unpublished course (Draft) to ensure unpublished courses are excluded from public discovery
            new Course(
                "Quantum Computing Fundamentals (Draft)",
                "Internal draft course on quantum algorithms and Qiskit simulator fundamentals.",
                "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?auto=format&fit=crop&w=800&q=80",
                "Emerging Tech",
                "Advanced",
                "12 weeks",
                "Dr. Aris Thorne",
                9999,
                false
            )
        );

        int seededCount = 0;
        for (Course course : initialCourses) {
            if (!courseRepository.existsByTitle(course.getTitle())) {
                courseRepository.save(course);
                seededCount++;
                log.info("Seeded course: {} (published={})", course.getTitle(), course.isPublished());
            }
        }

        log.info("Course seeding finished. {} new courses added, total courses in DB: {}", seededCount, courseRepository.count());
    }
}
