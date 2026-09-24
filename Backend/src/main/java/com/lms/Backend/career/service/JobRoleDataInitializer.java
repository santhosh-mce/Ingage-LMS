package com.lms.Backend.career.service;

import com.lms.Backend.career.entity.JobRole;
import com.lms.Backend.career.repository.JobRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobRoleDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JobRoleDataInitializer.class);
    private final JobRoleRepository jobRoleRepository;

    public JobRoleDataInitializer(JobRoleRepository jobRoleRepository) {
        this.jobRoleRepository = jobRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking Job Roles initial seed data...");

        JobRole r1 = new JobRole();
        r1.setTitle("Data Analyst");
        r1.setSlug("data-analyst");
        r1.setDescription("A Data Analyst collects, cleans, and interprets data sets to answer questions or solve problems.");
        r1.setDifficultyLevel("Beginner");
        r1.setDurationMonths(5);
        r1.setMinimumSalary(6);
        r1.setMaximumSalary(10);
        r1.setJobOpenings(27098);
        r1.setModuleCount(10);
        r1.setTrending(true);
        r1.setActive(true);
        r1.setIconName("BarChart3");
        r1.setImageUrl("https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=800&q=80");

        JobRole r2 = new JobRole();
        r2.setTitle("Data Scientist");
        r2.setSlug("data-scientist");
        r2.setDescription("A Data Scientist analyzes large datasets to uncover insights and build predictive models.");
        r2.setDifficultyLevel("Intermediate");
        r2.setDurationMonths(8);
        r2.setMinimumSalary(12);
        r2.setMaximumSalary(18);
        r2.setJobOpenings(23744);
        r2.setModuleCount(15);
        r2.setTrending(true);
        r2.setActive(true);
        r2.setIconName("Database");
        r2.setImageUrl("https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80");

        JobRole r3 = new JobRole();
        r3.setTitle("Digital Marketing Specialist");
        r3.setSlug("digital-marketing-specialist");
        r3.setDescription("A Digital Marketing Specialist manages campaigns, optimizes SEO, and drives online engagement.");
        r3.setDifficultyLevel("Beginner");
        r3.setDurationMonths(4);
        r3.setMinimumSalary(4);
        r3.setMaximumSalary(8);
        r3.setJobOpenings(41506);
        r3.setModuleCount(9);
        r3.setTrending(false);
        r3.setActive(true);
        r3.setIconName("TrendingUp");
        r3.setImageUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80");

        JobRole r4 = new JobRole();
        r4.setTitle("Machine Learning Engineer");
        r4.setSlug("machine-learning-engineer");
        r4.setDescription("A Machine Learning Engineer builds and optimizes algorithms that enable computers to learn.");
        r4.setDifficultyLevel("Advanced");
        r4.setDurationMonths(10);
        r4.setMinimumSalary(15);
        r4.setMaximumSalary(25);
        r4.setJobOpenings(18932);
        r4.setModuleCount(16);
        r4.setTrending(true);
        r4.setActive(true);
        r4.setIconName("Bot");
        r4.setImageUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80");

        JobRole r5 = new JobRole();
        r5.setTitle("Business Intelligence Analyst");
        r5.setSlug("business-intelligence-analyst");
        r5.setDescription("A Business Intelligence Analyst transforms data into actionable insights for strategic decisions.");
        r5.setDifficultyLevel("Intermediate");
        r5.setDurationMonths(6);
        r5.setMinimumSalary(7);
        r5.setMaximumSalary(12);
        r5.setJobOpenings(19876);
        r5.setModuleCount(11);
        r5.setTrending(false);
        r5.setActive(true);
        r5.setIconName("LineChart");
        r5.setImageUrl("https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80");

        JobRole r6 = new JobRole();
        r6.setTitle("Full Stack Developer");
        r6.setSlug("full-stack-developer");
        r6.setDescription("Master both frontend and backend development to build complete web applications from scratch.");
        r6.setDifficultyLevel("Intermediate");
        r6.setDurationMonths(8);
        r6.setMinimumSalary(8);
        r6.setMaximumSalary(15);
        r6.setJobOpenings(45420);
        r6.setModuleCount(12);
        r6.setTrending(true);
        r6.setActive(true);
        r6.setIconName("Code");
        r6.setImageUrl("https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80");

        List<JobRole> roles = List.of(r1, r2, r3, r4, r5, r6);
        int seededCount = 0;
        for (JobRole role : roles) {
            if (!jobRoleRepository.existsBySlug(role.getSlug())) {
                jobRoleRepository.save(role);
                seededCount++;
                log.info("Seeded job role: {}", role.getSlug());
            }
        }
        log.info("Job roles seeding complete. {} new roles inserted, total active roles: {}", seededCount, jobRoleRepository.count());
    }
}
