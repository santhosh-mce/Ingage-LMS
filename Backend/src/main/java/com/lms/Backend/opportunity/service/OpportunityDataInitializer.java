package com.lms.Backend.opportunity.service;

import com.lms.Backend.opportunity.entity.Opportunity;
import com.lms.Backend.opportunity.repository.OpportunityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class OpportunityDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OpportunityDataInitializer.class);
    private final OpportunityRepository opportunityRepository;

    public OpportunityDataInitializer(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
    }

    @Override
    public void run(String... args) {
        if (opportunityRepository.count() > 0) {
            log.info("[OpportunityDataInitializer] Opportunities already seeded. Count: {}", opportunityRepository.count());
            return;
        }

        log.info("[OpportunityDataInitializer] Seeding initial 6 Opportunities into PostgreSQL...");

        // 1. Data Analyst Intern
        Opportunity opp1 = new Opportunity();
        opp1.setTitle("Data Analyst Intern");
        opp1.setCompany("DataCorp Analytics");
        opp1.setCompanyLogo("https://images.unsplash.com/photo-1549923746-c502d488b3ea?q=80&w=120&auto=format&fit=crop");
        opp1.setLocation("Remote");
        opp1.setType("Internship");
        opp1.setWorkMode("Remote");
        opp1.setSalary("₹25,000 - ₹35,000 / month");
        opp1.setSalaryMin(25000.0);
        opp1.setSalaryMax(35000.0);
        opp1.setSalaryPeriod("month");
        opp1.setExperienceLevel("Entry Level");
        opp1.setCategory("Data & Analytics");
        opp1.setMatchScore(92);
        opp1.setDeadline("In 2 weeks");
        opp1.setRoleTrackId("data-analyst");
        opp1.setDescription("DataCorp Analytics is seeking a highly motivated Data Analyst Intern to join our business intelligence unit. You will work directly with our engineering and product teams to transform raw event data into actionable operational insights.");
        opp1.setAboutCompany("DataCorp Analytics is a leading data intelligence firm supporting Fortune 500 enterprises with real-time reporting, customer analytics, and ETL pipeline management.");
        opp1.setRequiredSkills(List.of("SQL", "Excel", "Python", "Power BI"));
        opp1.setResponsibilities(List.of(
            "Extract and transform transactional records using PostgreSQL and MySQL queries",
            "Maintain weekly business performance dashboards in Excel and automated Python scripts",
            "Collaborate with product managers to define tracking KPIs for user retention",
            "Assist senior analysts in synthesizing A/B test results into executive summaries"
        ));
        opp1.setQualifications(List.of(
            "Proficiency in SQL querying (joins, aggregations, CTEs, window functions)",
            "Working knowledge of Python for data manipulation (pandas, numpy)",
            "Advanced spreadsheet modeling skills in MS Excel or Google Sheets",
            "Strong communication and data storytelling aptitude"
        ));
        opp1.setBenefits(List.of(
            "Mentorship from Senior Data Principals",
            "Certificate of Internship Completion & Letter of Recommendation",
            "Full-time PPO conversion based on performance",
            "Flexible remote work hours"
        ));
        opp1.setActive(true);
        opp1.setPublished(true);
        opp1.setDisplayOrder(1);

        // 2. Junior Full Stack Developer
        Opportunity opp2 = new Opportunity();
        opp2.setTitle("Junior Full Stack Developer");
        opp2.setCompany("Tech Solutions Inc.");
        opp2.setCompanyLogo("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?q=80&w=120&auto=format&fit=crop");
        opp2.setLocation("Bangalore, India");
        opp2.setType("Job");
        opp2.setWorkMode("Hybrid");
        opp2.setSalary("₹7 - ₹9 LPA");
        opp2.setSalaryMin(700000.0);
        opp2.setSalaryMax(900000.0);
        opp2.setSalaryPeriod("year");
        opp2.setExperienceLevel("0 - 2 Years");
        opp2.setCategory("Software Engineering");
        opp2.setMatchScore(88);
        opp2.setDeadline("In 3 weeks");
        opp2.setRoleTrackId("full-stack-developer");
        opp2.setDescription("Tech Solutions Inc. is seeking a talented Junior Full Stack Developer to build modern web applications and customer-facing dashboards using React, TypeScript, and Node.js.");
        opp2.setAboutCompany("Tech Solutions Inc. powers cloud software solutions for thousands of global SaaS companies with cutting-edge telemetry and developer tooling.");
        opp2.setRequiredSkills(List.of("React", "TypeScript", "Node.js", "Tailwind CSS"));
        opp2.setResponsibilities(List.of(
            "Develop dynamic, responsive user interfaces using React and modern CSS frameworks",
            "Integrate RESTful microservices and PostgreSQL databases with high reliability",
            "Write automated unit and integration tests to ensure robust deployment pipeline",
            "Participate in code reviews and collaborate closely with UX designers"
        ));
        opp2.setQualifications(List.of(
            "Solid command of modern JavaScript (ES6+), TypeScript, and React hooks",
            "Understanding of client-server architecture, HTTP protocols, and REST API design",
            "Familiarity with Git workflow, CI/CD pipelines, and cloud deployment basics",
            "Passionate about clean code and delightful user experiences"
        ));
        opp2.setBenefits(List.of(
            "Comprehensive Health & Wellness Insurance",
            "Annual Learning & Certification Allowance (₹50,000/yr)",
            "Hybrid office model with modern campus in Indiranagar, Bangalore",
            "Competitive equity stock options"
        ));
        opp2.setActive(true);
        opp2.setPublished(true);
        opp2.setDisplayOrder(2);

        // 3. React Native Mobile App Specialist
        Opportunity opp3 = new Opportunity();
        opp3.setTitle("React Native Mobile App Specialist");
        opp3.setCompany("FinFlow Technologies");
        opp3.setCompanyLogo("https://images.unsplash.com/photo-1551836022-d5d88e9218df?q=80&w=120&auto=format&fit=crop");
        opp3.setLocation("Remote");
        opp3.setType("Freelance");
        opp3.setWorkMode("Remote");
        opp3.setSalary("₹60,000 - ₹90,000 / project");
        opp3.setSalaryMin(60000.0);
        opp3.setSalaryMax(90000.0);
        opp3.setSalaryPeriod("project");
        opp3.setExperienceLevel("Intermediate");
        opp3.setCategory("Mobile Development");
        opp3.setMatchScore(82);
        opp3.setDeadline("In 1 week");
        opp3.setRoleTrackId("mobile-developer");
        opp3.setDescription("FinFlow is looking for a freelance mobile app engineer to implement feature updates for our cross-platform personal finance mobile app built on React Native.");
        opp3.setAboutCompany("FinFlow Technologies builds intuitive micro-savings and automated budget tracking apps used by over 200,000 young professionals across India.");
        opp3.setRequiredSkills(List.of("React Native", "Redux Toolkit", "iOS/Android", "REST APIs"));
        opp3.setResponsibilities(List.of(
            "Implement newly designed onboarding screens and banking transaction widgets",
            "Optimize component rendering speed and memory usage on low-spec Android devices",
            "Integrate secure biometric authentication and notification push listeners"
        ));
        opp3.setQualifications(List.of(
            "Prior published apps on Google Play Store or Apple App Store",
            "Proficiency in React Native and state management with Redux Toolkit",
            "Strong eye for smooth animations and fluid mobile UI interactions"
        ));
        opp3.setBenefits(List.of(
            "100% remote asynchronous workflow with flexible hours",
            "Milestone-based prompt weekly payouts",
            "Long-term contract extension possibilities"
        ));
        opp3.setActive(true);
        opp3.setPublished(true);
        opp3.setDisplayOrder(3);

        // 4. Cloud & DevOps Apprentice
        Opportunity opp4 = new Opportunity();
        opp4.setTitle("Cloud & DevOps Apprentice");
        opp4.setCompany("InfraScale Cloud Systems");
        opp4.setCompanyLogo("https://images.unsplash.com/photo-1522071820081-009f0129c71c?q=80&w=120&auto=format&fit=crop");
        opp4.setLocation("Hyderabad, India");
        opp4.setType("Apprenticeship");
        opp4.setWorkMode("On-site");
        opp4.setSalary("₹20,000 / month + Full Tuition");
        opp4.setSalaryMin(20000.0);
        opp4.setSalaryMax(20000.0);
        opp4.setSalaryPeriod("month");
        opp4.setExperienceLevel("Fresher / Trainee");
        opp4.setCategory("Cloud & DevOps");
        opp4.setMatchScore(75);
        opp4.setDeadline("In 4 weeks");
        opp4.setRoleTrackId("devops-engineer");
        opp4.setDescription("Kickstart your cloud engineering career through our structured 12-month Apprenticeship program. You will receive certified AWS training while assisting our site reliability engineering squad.");
        opp4.setAboutCompany("InfraScale delivers multi-cloud infrastructure automation, Kubernetes orchestration, and 24/7 reliability engineering for enterprise clients.");
        opp4.setRequiredSkills(List.of("Linux", "Docker", "AWS Basics", "Networking"));
        opp4.setResponsibilities(List.of(
            "Learn and shadow senior SREs in managing Docker container clusters on AWS",
            "Automate system monitoring scripts and configure Prometheus / Grafana alerts",
            "Participate in disaster recovery drills and incident response walkthroughs"
        ));
        opp4.setQualifications(List.of(
            "Basic knowledge of Linux terminal commands and shell scripting",
            "Foundational understanding of TCP/IP, DNS, and HTTP networking concepts",
            "Eagerness to learn cloud architectures and achieve AWS certifications"
        ));
        opp4.setBenefits(List.of(
            "Sponsored AWS Certified Solutions Architect exam fee",
            "Dedicated industry mentor throughout the 12-month program",
            "Guaranteed interview for full-time Cloud Engineer role upon graduation"
        ));
        opp4.setActive(true);
        opp4.setPublished(true);
        opp4.setDisplayOrder(4);

        // 5. UI/UX Design Intern
        Opportunity opp5 = new Opportunity();
        opp5.setTitle("UI/UX Design Intern");
        opp5.setCompany("PixelCraft Creative Studio");
        opp5.setCompanyLogo("https://images.unsplash.com/photo-1572021335469-31706a17aaef?q=80&w=120&auto=format&fit=crop");
        opp5.setLocation("Mumbai, India");
        opp5.setType("Internship");
        opp5.setWorkMode("Hybrid");
        opp5.setSalary("₹20,000 - ₹28,000 / month");
        opp5.setSalaryMin(20000.0);
        opp5.setSalaryMax(28000.0);
        opp5.setSalaryPeriod("month");
        opp5.setExperienceLevel("Entry Level");
        opp5.setCategory("Design & Creative");
        opp5.setMatchScore(84);
        opp5.setDeadline("In 10 days");
        opp5.setRoleTrackId("ui-ux-designer");
        opp5.setDescription("PixelCraft is looking for an imaginative UI/UX design intern to collaborate on enterprise SaaS design systems and consumer mobile experiences.");
        opp5.setAboutCompany("PixelCraft is an award-winning digital experience agency that crafts brand identities, UI design systems, and web apps for hyper-growth startups.");
        opp5.setRequiredSkills(List.of("Figma", "Wireframing", "User Research", "Design Systems"));
        opp5.setResponsibilities(List.of(
            "Create high-fidelity wireframes, interactive prototypes, and vector icons in Figma",
            "Conduct usability interviews and synthesize user feedback into iterative mockups",
            "Collaborate with front-end developers to ensure pixel-perfect design implementation"
        ));
        opp5.setQualifications(List.of(
            "A portfolio showing 2+ UX case studies or interactive Figma prototypes",
            "Understanding of visual hierarchy, accessibility (WCAG), and responsive typography",
            "Great collaborative attitude and openness to constructive critique"
        ));
        opp5.setBenefits(List.of(
            "Work on global client projects across FinTech, HealthTech, and EdTech",
            "Direct 1-on-1 feedback sessions from Design Directors",
            "Fast-track PPO offer"
        ));
        opp5.setActive(true);
        opp5.setPublished(true);
        opp5.setDisplayOrder(5);

        // 6. Frontend Web Developer (React)
        Opportunity opp6 = new Opportunity();
        opp6.setTitle("Frontend Web Developer (React)");
        opp6.setCompany("Apex Digital Labs");
        opp6.setCompanyLogo("https://images.unsplash.com/photo-1568602471122-7832951cc4c5?q=80&w=120&auto=format&fit=crop");
        opp6.setLocation("Pune, India");
        opp6.setType("Job");
        opp6.setWorkMode("Hybrid");
        opp6.setSalary("₹8 - ₹12 LPA");
        opp6.setSalaryMin(800000.0);
        opp6.setSalaryMax(1200000.0);
        opp6.setSalaryPeriod("year");
        opp6.setExperienceLevel("1 - 3 Years");
        opp6.setCategory("Software Engineering");
        opp6.setMatchScore(90);
        opp6.setDeadline("In 2 weeks");
        opp6.setRoleTrackId("frontend-developer");
        opp6.setDescription("Apex Digital Labs is expanding our core engineering team. We are looking for an ambitious React Frontend Developer to architect high-performance web experiences.");
        opp6.setAboutCompany("Apex Digital Labs provides enterprise digital transformation solutions, powering customer portals and e-commerce engines for international brands.");
        opp6.setRequiredSkills(List.of("React", "TypeScript", "Tailwind CSS", "Redux Toolkit"));
        opp6.setResponsibilities(List.of(
            "Build scalable, modular React components using clean architecture patterns",
            "Optimize web vitals (LCP, FID, CLS) across desktop and mobile devices",
            "Collaborate with backend engineers to integrate GraphQL and REST APIs"
        ));
        opp6.setQualifications(List.of(
            "Proven expertise in React 18+, TypeScript, and Tailwind CSS",
            "Experience managing complex global state with Redux Toolkit or Zustand",
            "Knowledge of accessibility and cross-browser compatibility standards"
        ));
        opp6.setBenefits(List.of(
            "Health insurance covering employee, spouse, and parents",
            "Flexible work hours and generous paid time off",
            "Annual technology allowance for home office setup"
        ));
        opp6.setActive(true);
        opp6.setPublished(true);
        opp6.setDisplayOrder(6);

        opportunityRepository.saveAll(List.of(opp1, opp2, opp3, opp4, opp5, opp6));
        log.info("[OpportunityDataInitializer] Successfully saved 6 Opportunities in PostgreSQL.");
    }
}
