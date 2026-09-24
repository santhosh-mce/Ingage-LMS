package com.lms.Backend.credential.config;

import com.lms.Backend.credential.entity.CredentialCourse;
import com.lms.Backend.credential.entity.CredentialModule;
import com.lms.Backend.credential.repository.CredentialCourseRepository;
import com.lms.Backend.credential.repository.CredentialModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CredentialCourseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CredentialCourseInitializer.class);

    private final CredentialCourseRepository courseRepository;
    private final CredentialModuleRepository moduleRepository;

    public CredentialCourseInitializer(
        CredentialCourseRepository courseRepository,
        CredentialModuleRepository moduleRepository
    ) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            log.info("[CredentialCourseInitializer] Google credential courses already initialized (count={})", courseRepository.count());
            return;
        }

        log.info("[CredentialCourseInitializer] Seeding initial Google Certified Courses...");

        // 1. Google Data Analytics
        CredentialCourse dataAnalytics = createCourse(
            "Google Data Analytics",
            "google-data-analytics",
            "Google",
            "Data & Analytics",
            "Beginner",
            "Approx. 6 months",
            "Develop practical data analytics skills using spreadsheets, SQL, data visualization and analytical thinking.",
            "Develop practical data analytics skills using spreadsheets, SQL, data visualization and analytical thinking.",
            "https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=80",
            "Google Data Analytics Professional Certificate",
            "Professional Certificate",
            "https://grow.google/certificates/data-analytics/",
            0.0,
            0.0,
            true,
            true,
            true,
            4.8,
            24500,
            "data-analyst",
            Arrays.asList(
                "Data analysis fundamentals",
                "Spreadsheet analysis and formula mastery",
                "SQL database queries and joins",
                "Data cleaning and transformation",
                "Data visualization with Tableau and charts",
                "Analytical thinking and storytelling",
                "Data-driven strategic decision making"
            ),
            Arrays.asList(
                "No prior analytics or coding experience required",
                "Basic familiarity with computers and web browsers"
            )
        );
        addModules(dataAnalytics, Arrays.asList(
            new ModuleSeed("Module 1 — Foundations of Data Analytics", "Understand the data ecosystem, role of an analyst, and foundational tools.", "4 weeks"),
            new ModuleSeed("Module 2 — Data Preparation and Processing", "Explore data types, structures, and ethical collection strategies.", "4 weeks"),
            new ModuleSeed("Module 3 — Spreadsheet Analysis and Formulas", "Master conditional formatting, pivot tables, and lookup functions.", "5 weeks"),
            new ModuleSeed("Module 4 — SQL Database Extraction and Aggregation", "Write queries, join relational tables, and filter large datasets.", "5 weeks"),
            new ModuleSeed("Module 5 — Data Visualization and Dashboards", "Design impactful visual presentations in Tableau and presentation decks.", "4 weeks"),
            new ModuleSeed("Module 6 — Capstone Case Study Project", "Complete an end-to-end case study analyzing real business problem datasets.", "3 weeks")
        ));

        // 2. Google Cybersecurity
        CredentialCourse cybersecurity = createCourse(
            "Google Cybersecurity",
            "google-cybersecurity",
            "Google",
            "Cybersecurity",
            "Beginner",
            "Approx. 6 months",
            "Build foundational cybersecurity skills including security concepts, risk management, networks and security operations.",
            "Build foundational cybersecurity skills including security concepts, risk management, networks and security operations.",
            "https://images.unsplash.com/photo-1563986768609-322da13575f3?auto=format&fit=crop&w=800&q=80",
            "Google Cybersecurity Professional Certificate",
            "Professional Certificate",
            "https://grow.google/certificates/cybersecurity/",
            0.0,
            0.0,
            true,
            true,
            true,
            4.9,
            18300,
            "security-analyst",
            Arrays.asList(
                "Security operations center (SOC) analyst responsibilities",
                "Core security frameworks and compliance principles",
                "Network architecture, packet inspection and packet filters",
                "Linux operating system commands and permissions",
                "SQL querying for security incident investigation",
                "Security Information and Event Management (SIEM) tools"
            ),
            Arrays.asList(
                "No prior cybersecurity or technical experience required",
                "Interest in analytical problem solving and defensive computing"
            )
        );
        addModules(cybersecurity, Arrays.asList(
            new ModuleSeed("Module 1 — Foundations of Cybersecurity", "Explore security terminology, threat landscapes, and ethical principles.", "3 weeks"),
            new ModuleSeed("Module 2 — Play It Safe: Manage Security Risks", "Learn the NIST CSF and risk management methodologies.", "4 weeks"),
            new ModuleSeed("Module 3 — Connect and Protect: Networks and Network Security", "Study TCP/IP, OSI model, ports, routers, and firewalls.", "5 weeks"),
            new ModuleSeed("Module 4 — Tools of the Trade: Linux and SQL", "Navigate Linux file systems and query databases for security logs.", "5 weeks"),
            new ModuleSeed("Module 5 — Assets, Threats, and Vulnerabilities", "Identify vulnerabilities, malware classifications, and defensive controls.", "4 weeks"),
            new ModuleSeed("Module 6 — Detection and Response & Incident Handling", "Execute incident containment, eradication, and post-incident documentation.", "4 weeks")
        ));

        // 3. Google Generative AI
        CredentialCourse genAi = createCourse(
            "Google Generative AI",
            "google-generative-ai",
            "Google",
            "Generative AI",
            "Beginner",
            "Approx. 2 months",
            "Learn foundational generative AI concepts and practical applications for modern workplace and technical workflows.",
            "Learn foundational generative AI concepts and practical applications for modern workplace and technical workflows.",
            "https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&w=800&q=80",
            "Google Generative AI Course",
            "Credential Course",
            "https://cloud.google.com/training/generative-ai",
            0.0,
            0.0,
            true,
            true,
            true,
            4.7,
            14200,
            "ai-engineer",
            Arrays.asList(
                "Foundational generative AI and deep learning mechanics",
                "Large Language Model (LLM) architectures and attention mechanisms",
                "Prompt engineering methodologies and iterative refinement",
                "Context windows, hallucination mitigation, and grounding",
                "Responsible AI ethics, transparency, and bias reduction"
            ),
            Arrays.asList(
                "Basic understanding of modern digital workflows and AI concepts"
            )
        );
        addModules(genAi, Arrays.asList(
            new ModuleSeed("Module 1 — Introduction to Generative AI", "Understand what generative AI is, how it works, and how it differs from traditional machine learning.", "2 weeks"),
            new ModuleSeed("Module 2 — Large Language Models & Transformers", "Explore transformers, self-attention, and pre-training versus fine-tuning.", "2 weeks"),
            new ModuleSeed("Module 3 — Prompt Engineering Best Practices", "Design effective system prompts, few-shot prompts, and chain-of-thought instructions.", "2 weeks"),
            new ModuleSeed("Module 4 — Responsible AI & Safety Principles", "Implement responsible AI principles, privacy safeguards, and safety evaluations.", "2 weeks")
        ));

        // 4. Google Cloud Engineering
        CredentialCourse cloudEng = createCourse(
            "Google Cloud Engineering",
            "google-cloud-engineering",
            "Google",
            "Cloud",
            "Intermediate",
            "Approx. 4 months",
            "Build cloud engineering knowledge and prepare for cloud-focused learning and certification pathways.",
            "Build cloud engineering knowledge and prepare for cloud-focused learning and certification pathways.",
            "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
            "Google Cloud Engineering Pathway",
            "Career Track Pathway",
            "https://cloud.google.com/learn/training",
            0.0,
            0.0,
            true,
            true,
            false,
            4.8,
            9800,
            "cloud-architect",
            Arrays.asList(
                "Compute Engine VM deployment and scaling strategies",
                "Google Kubernetes Engine (GKE) cluster deployment",
                "VPC networks, subnets, firewalls, and cloud routers",
                "Cloud Storage buckets and persistent block storage",
                "Cloud IAM permissions, service accounts, and least privilege"
            ),
            Arrays.asList(
                "Basic understanding of operating systems, networking, and virtualization",
                "Familiarity with command-line tools"
            )
        );
        addModules(cloudEng, Arrays.asList(
            new ModuleSeed("Module 1 — Google Cloud Fundamentals: Core Infrastructure", "Overview of Google Cloud computing and storage services.", "3 weeks"),
            new ModuleSeed("Module 2 — Essential Google Cloud Infrastructure: Foundation", "Configure networks, virtual machines, and identity controls.", "3 weeks"),
            new ModuleSeed("Module 3 — Scaling and Automation: Compute Engine & GKE", "Deploy managed instance groups, autoscaling, and container workloads.", "4 weeks"),
            new ModuleSeed("Module 4 — Cloud Networking & Hybrid Connectivity", "Design VPC topologies, peering, VPN tunnels, and Cloud Interconnect.", "3 weeks"),
            new ModuleSeed("Module 5 — Cloud Security, Monitoring & Auditing", "Set up Cloud Operations monitoring, alerting, and log analysis.", "3 weeks")
        ));

        // 5. Google Cloud Computing Foundations
        CredentialCourse cloudFoundations = createCourse(
            "Google Cloud Computing Foundations",
            "google-cloud-computing-foundations",
            "Google",
            "Cloud",
            "Beginner",
            "Approx. 3 months",
            "Learn foundational cloud concepts, infrastructure, networking, security and cloud computing principles.",
            "Learn foundational cloud concepts, infrastructure, networking, security and cloud computing principles.",
            "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?auto=format&fit=crop&w=800&q=80",
            "Google Cloud Computing Foundations Certificate",
            "Foundations Certificate",
            "https://cloud.google.com/learn/training/foundations",
            0.0,
            0.0,
            true,
            true,
            false,
            4.6,
            11200,
            "cloud-architect",
            Arrays.asList(
                "Cloud computing core principles and architectural drivers",
                "Virtualization versus containerization fundamentals",
                "Cloud networking, latency, and global infrastructure",
                "Storage classes and database choices in Google Cloud"
            ),
            Arrays.asList(
                "No prior cloud experience required"
            )
        );
        addModules(cloudFoundations, Arrays.asList(
            new ModuleSeed("Module 1 — Cloud Computing Concepts Overview", "Learn cloud paradigms, shared responsibility model, and cost models.", "2 weeks"),
            new ModuleSeed("Module 2 — Infrastructure Fundamentals: Compute & Storage", "Explore VMs, containers, object storage, and block disks.", "3 weeks"),
            new ModuleSeed("Module 3 — Networking and Security Essentials", "Understand IP addresses, firewalls, and encryption in transit/rest.", "3 weeks"),
            new ModuleSeed("Module 4 — Data and Cloud Operations", "Learn big data fundamentals, AI options, and system health logging.", "3 weeks")
        ));

        // 6. Google Associate Engineer
        CredentialCourse assocEngineer = createCourse(
            "Google Associate Engineer",
            "google-associate-engineer",
            "Google",
            "Cloud & Infrastructure",
            "Intermediate",
            "Approx. 5 months",
            "Cloud-focused preparation and learning pathway for an associate-level engineering credential.",
            "Cloud-focused preparation and learning pathway for an associate-level engineering credential.",
            "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80",
            "Google Associate Cloud Engineer Preparation",
            "Preparation Pathway",
            "https://cloud.google.com/certification/cloud-engineer",
            0.0,
            0.0,
            true,
            true,
            false,
            4.8,
            8700,
            "cloud-architect",
            Arrays.asList(
                "Google Cloud CLI (gcloud) and Cloud Shell mastery",
                "Solution environment configuration and project billing setup",
                "Workload deployment on Compute Engine, Cloud Run, and GKE",
                "Configuring secure network routing and firewall rules",
                "Identity & Access Management (IAM) role auditing"
            ),
            Arrays.asList(
                "6+ months hands-on experience with cloud infrastructure or completion of Google Cloud Foundations"
            )
        );
        addModules(assocEngineer, Arrays.asList(
            new ModuleSeed("Module 1 — Setting Up a Cloud Solution Environment", "Manage projects, accounts, billing, and resource hierarchies.", "3 weeks"),
            new ModuleSeed("Module 2 — Planning and Configuring Cloud Solutions", "Compute resource sizing, storage class selection, and network planning.", "4 weeks"),
            new ModuleSeed("Module 3 — Deploying and Implementing Cloud Solutions", "Hands-on deployments across Compute Engine, GKE, and Cloud Run.", "5 weeks"),
            new ModuleSeed("Module 4 — Ensuring Successful Operation", "Monitor metrics, optimize costs, and backup databases.", "4 weeks"),
            new ModuleSeed("Module 5 — Configuring Access and Security Policies", "Enforce IAM roles, audit service accounts, and inspect network logs.", "3 weeks")
        ));

        log.info("[CredentialCourseInitializer] Successfully seeded 6 Google credential courses.");
    }

    private CredentialCourse createCourse(
        String title, String slug, String provider, String category, String level,
        String duration, String desc, String shortDesc, String thumb, String credName,
        String credType, String credUrl, Double price, Double discount, boolean free,
        boolean published, boolean featured, Double rating, Integer learners, String careerSlug,
        List<String> outcomes, List<String> prereqs
    ) {
        CredentialCourse c = new CredentialCourse();
        c.setTitle(title);
        c.setSlug(slug);
        c.setProvider(provider);
        c.setCategory(category);
        c.setLevel(level);
        c.setDuration(duration);
        c.setDescription(desc);
        c.setShortDescription(shortDesc);
        c.setThumbnail(thumb);
        c.setCredentialName(credName);
        c.setCredentialType(credType);
        c.setCredentialUrl(credUrl);
        c.setPrice(price);
        c.setDiscount(discount);
        c.setFree(free);
        c.setPublished(published);
        c.setFeatured(featured);
        c.setRating(rating);
        c.setLearnersCount(learners);
        c.setCareerSlug(careerSlug);
        c.setLearningOutcomes(outcomes);
        c.setPrerequisites(prereqs);
        return courseRepository.save(c);
    }

    private void addModules(CredentialCourse course, List<ModuleSeed> moduleSeeds) {
        for (int i = 0; i < moduleSeeds.size(); i++) {
            ModuleSeed s = moduleSeeds.get(i);
            CredentialModule m = new CredentialModule(s.title, s.description, s.duration, i + 1, course);
            moduleRepository.save(m);
        }
    }

    private static class ModuleSeed {
        String title;
        String description;
        String duration;

        ModuleSeed(String title, String description, String duration) {
            this.title = title;
            this.description = description;
            this.duration = duration;
        }
    }
}
