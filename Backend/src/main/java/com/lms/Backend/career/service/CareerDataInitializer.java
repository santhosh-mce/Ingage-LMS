package com.lms.Backend.career.service;

import com.lms.Backend.career.entity.*;
import com.lms.Backend.career.repository.CareerRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(2) // Run after CourseDataInitializer
public class CareerDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CareerDataInitializer.class);

    private final CareerRepository careerRepository;
    private final CourseRepository courseRepository;

    public CareerDataInitializer(CareerRepository careerRepository, CourseRepository courseRepository) {
        this.careerRepository = careerRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Checking initial Careers seed data...");

        List<Course> courses = courseRepository.findAll();
        Course cFullStack = findCourseByKeyword(courses, "Full Stack");
        Course cJava = findCourseByKeyword(courses, "Java Spring Boot");
        Course cReact = findCourseByKeyword(courses, "React");
        Course cPython = findCourseByKeyword(courses, "Python");
        Course cData = findCourseByKeyword(courses, "Data Analytics");
        Course cDevOps = findCourseByKeyword(courses, "DevOps");
        Course cArch = findCourseByKeyword(courses, "Microservices");

        // 1. Data Analyst
        seedCareer(
            "Data Analyst",
            "data-analyst",
            "Data & Analytics",
            "A Data Analyst collects, cleans, and interprets data sets to answer questions or solve problems.",
            "Transform raw data into meaningful business insights.",
            "Beginner", "5 months", 600000L, 1000000L,
            "https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=800&q=80",
            "BarChart3", true, true, 1, "27,098+", 10,
            "Certified Enterprise Data Analyst (CEDA)",
            List.of("SQL", "Tableau", "Python for Data Analysis", "Excel Analytics", "Statistical Modeling"),
            List.of("Clean and validate business data", "Build executive dashboards in Tableau & PowerBI", "Query relational databases with advanced SQL"),
            List.of(
                new RoadmapItem("Foundations of Modern Data Analysis", "Master spreadsheet analytics, business metrics, and core statistics.", "3 weeks"),
                new RoadmapItem("Relational Databases & Advanced SQL", "Complex joins, window functions, CTEs, and query optimization.", "4 weeks"),
                new RoadmapItem("Interactive BI Dashboards", "Transform raw data into executive dashboards with Tableau & PowerBI.", "4 weeks"),
                new RoadmapItem("Capstone: Growth Analytics", "Live simulated dataset with 1M rows evaluating churn and lifetime value.", "5 weeks")
            ),
            List.of(new ProjectItem("Executive Sales Intelligence Dashboard", "Build a high-impact dashboard tracking multi-channel sales and retention.", "Beginner", "SQL, Tableau, Excel")),
            courseList(cData, cPython)
        );

        // 2. Data Scientist
        seedCareer(
            "Data Scientist",
            "data-scientist",
            "Data & Analytics",
            "A Data Scientist analyzes large datasets to uncover insights and build predictive models.",
            "Harness predictive modeling and machine learning algorithms.",
            "Intermediate", "8 months", 1200000L, 1800000L,
            "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80",
            "Database", true, true, 2, "23,744+", 15,
            "Certified Applied Data Scientist (CADS)",
            List.of("Python", "Machine Learning", "Pandas & NumPy", "Deep Learning", "MLOps & Deployment"),
            List.of("Train supervised and unsupervised machine learning models", "Build end-to-end predictive feature pipelines", "Evaluate statistical significance and feature importance"),
            List.of(
                new RoadmapItem("Scientific Computing with NumPy and Scipy", "Vectorized computing and mathematical data foundations.", "3 weeks"),
                new RoadmapItem("Supervised & Unsupervised Learning", "Regression, Decision Trees, Ensemble Random Forests and XGBoost.", "5 weeks"),
                new RoadmapItem("Deep Learning & Neural Networks", "PyTorch fundamentals, convolutional networks, and tuning.", "6 weeks"),
                new RoadmapItem("Production MLOps", "Model deployment, tracking experiments, and monitoring drift.", "4 weeks")
            ),
            List.of(new ProjectItem("Customer Churn Prediction Engine", "Train an XGBoost classifier predicting user churn with 92% accuracy.", "Intermediate", "Python, Scikit-learn, FastAPI")),
            courseList(cPython, cData)
        );

        // 3. Digital Marketing Specialist
        seedCareer(
            "Digital Marketing Specialist",
            "digital-marketing-specialist",
            "Growth & Marketing",
            "A Digital Marketing Specialist manages campaigns, optimizes SEO, and drives online engagement.",
            "Drive scalable user acquisition and brand visibility.",
            "Beginner", "4 months", 400000L, 800000L,
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80",
            "TrendingUp", true, false, 3, "41,506+", 9,
            "Certified Growth & Digital Marketer (CGDM)",
            List.of("Google Ads", "SEO Strategy", "Performance Analytics", "Social Media Paid Ads", "Email Funnels"),
            List.of("Design and optimize multi-channel ad campaigns", "Perform keyword research and on-page technical SEO audits", "Track conversion funnels and calculate CAC/ROAS"),
            List.of(
                new RoadmapItem("Search Engine Optimization & Content", "Technical audits, keyword planning, and backlink strategies.", "3 weeks"),
                new RoadmapItem("Paid Advertising & Search Marketing", "Google Ads bidding, meta campaigns, and audience segmentation.", "4 weeks"),
                new RoadmapItem("Growth Marketing & Funnels", "Retention strategies, email workflows, and A/B test experiments.", "3 weeks")
            ),
            List.of(new ProjectItem("SaaS Acquisition Funnel Campaign", "Launch an end-to-end mock campaign driving 10k leads under targeted CAC.", "Beginner", "Google Ads, GA4, HubSpot")),
            List.of()
        );

        // 4. Machine Learning Engineer
        seedCareer(
            "Machine Learning Engineer",
            "machine-learning-engineer",
            "Data & Analytics",
            "A Machine Learning Engineer builds and optimizes algorithms that enable computers to learn.",
            "Engineer scalable model inference and distributed training pipelines.",
            "Advanced", "10 months", 1500000L, 2500000L,
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80",
            "Bot", true, true, 4, "18,932+", 16,
            "Certified Machine Learning Architect (CMLA)",
            List.of("PyTorch", "TensorFlow", "Model Serving", "Transformers", "Distributed Training"),
            List.of("Scale deep learning inference servers with Triton/TorchServe", "Fine-tune open-weight LLMs with LoRA and PEFT", "Implement low-latency vector search and RAG pipelines"),
            List.of(
                new RoadmapItem("Advanced Deep Learning & Architectures", "Attention mechanisms, transformers, and diffusion models.", "6 weeks"),
                new RoadmapItem("Large Language Models & GenAI", "Fine-tuning, prompt optimization, and vector databases.", "5 weeks"),
                new RoadmapItem("High Performance Model Serving", "Quantization, ONNX runtime, and distributed inference clusters.", "5 weeks")
            ),
            List.of(new ProjectItem("Enterprise RAG Knowledge Assistant", "Build a high-performance documentation chatbot with hybrid vector search.", "Advanced", "PyTorch, LangChain, Milvus, Docker")),
            courseList(cPython)
        );

        // 5. Business Intelligence Analyst
        seedCareer(
            "Business Intelligence Analyst",
            "business-intelligence-analyst",
            "Data & Analytics",
            "A Business Intelligence Analyst transforms data into actionable insights for strategic decisions.",
            "Build executive reporting tools and enterprise analytics pipelines.",
            "Intermediate", "6 months", 700000L, 1200000L,
            "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
            "LineChart", false, false, 5, "19,876+", 11,
            "Certified BI Professional (CBIP)",
            List.of("Power BI", "SQL Warehousing", "DAX", "ETL Pipelines", "Executive Reporting"),
            List.of("Model dimensional schemas (star/snowflake)", "Write complex DAX calculations for KPIs", "Automate ETL pipelines feeding data warehouses"),
            List.of(
                new RoadmapItem("Dimensional Modeling & Data Warehouses", "Star schemas, facts, and dimensions in Snowflake.", "4 weeks"),
                new RoadmapItem("Advanced PowerBI & DAX", "Time intelligence, dynamic measures, and row-level security.", "5 weeks"),
                new RoadmapItem("Automated ETL Workflows", "Airflow and DBT for robust data transformation.", "4 weeks")
            ),
            List.of(new ProjectItem("Enterprise Financial KPI Cockpit", "Build real-time revenue and margin monitoring dashboards with drill-downs.", "Intermediate", "Power BI, DAX, PostgreSQL")),
            courseList(cData)
        );

        // 6. Full Stack Developer
        seedCareer(
            "Full Stack Developer",
            "full-stack-developer",
            "Software Engineering",
            "Master both frontend and backend development to build complete web applications from scratch.",
            "Architect and deploy modern web applications end-to-end.",
            "Intermediate", "8 months", 800000L, 1500000L,
            "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80",
            "Code", true, true, 6, "45,420+", 12,
            "Certified Full Stack Engineer (CFSE)",
            List.of("React & Next.js", "Node.js", "PostgreSQL", "TypeScript", "Docker"),
            List.of("Build responsive web clients with React, TypeScript & Tailwind", "Develop RESTful APIs with database ORMs", "Configure Docker containers and CI/CD deployment pipelines"),
            List.of(
                new RoadmapItem("Modern Frontend Architecture", "React, state management, hooks, and responsive design.", "4 weeks"),
                new RoadmapItem("Scalable Backend APIs", "Node.js, Express, Spring Boot REST, and relational design.", "5 weeks"),
                new RoadmapItem("DevOps, Testing & Deployment", "Dockerization, unit testing, and cloud hosting.", "3 weeks")
            ),
            List.of(new ProjectItem("Collaborative Real-time Workspace", "Build a live Kanban board with websockets, auth, and database persistence.", "Intermediate", "React, TypeScript, Node.js, PostgreSQL")),
            courseList(cFullStack, cReact, cJava)
        );

        // 7. Backend Developer
        seedCareer(
            "Backend Developer",
            "backend-developer",
            "Software Engineering",
            "Build scalable server-side applications, APIs, and database systems.",
            "Design resilient microservices, high-throughput APIs, and data layers.",
            "Intermediate", "6 months", 700000L, 1400000L,
            "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=800&q=80",
            "Settings", false, true, 7, "31,800+", 11,
            "Certified Backend Developer (CBD)",
            List.of("Node.js", "Go / Python", "REST & gRPC", "Microservices", "Redis Caching"),
            List.of("Architect microservice communication with REST and gRPC", "Implement caching layers using Redis for low latency", "Design database schemas and optimize SQL query execution plans"),
            List.of(
                new RoadmapItem("API Design & Protocol Mastery", "RESTful patterns, OpenAPI documentation, and gRPC.", "3 weeks"),
                new RoadmapItem("Database Systems & Query Optimization", "Indexes, transactions, connection pooling, and replication.", "4 weeks"),
                new RoadmapItem("Caching & Asynchronous Processing", "Redis cache-aside, message queues, and worker pools.", "4 weeks")
            ),
            List.of(new ProjectItem("High-Throughput Payment Processing API", "Build an idempotent payment service with distributed locks and webhooks.", "Intermediate", "Java, Spring Boot, Redis, PostgreSQL")),
            courseList(cJava, cPython, cArch)
        );

        // 8. DevOps Engineer
        seedCareer(
            "DevOps Engineer",
            "devops-engineer",
            "Security & Infrastructure",
            "Automate deployment pipelines and ensure smooth software delivery processes.",
            "Bridge the gap between development and cloud infrastructure.",
            "Intermediate", "7 months", 1000000L, 1800000L,
            "https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?auto=format&fit=crop&w=800&q=80",
            "Cpu", true, true, 8, "24,300+", 12,
            "Certified DevOps Engineer (CDE)",
            List.of("Kubernetes", "Docker", "Terraform", "CI/CD Pipelines", "AWS / GCP Cloud"),
            List.of("Provision cloud infrastructure as code using Terraform", "Deploy and manage containerized workloads on Kubernetes clusters", "Build automated CI/CD testing and deployment pipelines"),
            List.of(
                new RoadmapItem("Containers & Linux Systems", "Docker containerization, multi-stage builds, and shell scripting.", "3 weeks"),
                new RoadmapItem("Kubernetes Orchestration", "Pods, deployments, services, ingress, and Helm charts.", "4 weeks"),
                new RoadmapItem("Infrastructure as Code & CI/CD", "Terraform modules, GitHub Actions, and GitOps with ArgoCD.", "4 weeks")
            ),
            List.of(new ProjectItem("Zero-Downtime GitOps Deployment Pipeline", "Automate cluster deployment with Terraform, GitHub Actions, and ArgoCD.", "Intermediate", "Terraform, Kubernetes, Docker, GitHub Actions")),
            courseList(cDevOps)
        );

        // 9. Product Manager
        seedCareer(
            "Product Manager",
            "product-manager",
            "Design & Product",
            "Define product strategy, roadmap, and features to deliver customer value.",
            "Lead product lifecycle from discovery and roadmaps to execution.",
            "Intermediate", "6 months", 1200000L, 2200000L,
            "https://images.unsplash.com/photo-1531403009284-440f080d1e12?auto=format&fit=crop&w=800&q=80",
            "Box", false, false, 9, "18,600+", 10,
            "Certified Product Manager (CPM)",
            List.of("Product Strategy", "User Stories", "Roadmapping", "A/B Testing", "Agile Delivery"),
            List.of("Author detailed PRDs, user stories, and acceptance criteria", "Conduct customer interviews and validate feature hypotheses", "Align cross-functional teams across engineering, design, and marketing"),
            List.of(
                new RoadmapItem("Product Discovery & Market Research", "User interviews, market sizing, and competitive teardowns.", "3 weeks"),
                new RoadmapItem("Roadmapping & Prioritization", "RICE frameworks, stakeholder alignment, and sprint backlogs.", "4 weeks"),
                new RoadmapItem("Metrics & Experimentation", "North star metrics, funnel analytics, and hypothesis testing.", "3 weeks")
            ),
            List.of(new ProjectItem("B2B SaaS Onboarding Overhaul PRD", "Develop an end-to-end product requirements document improving user activation by 30%.", "Intermediate", "Figma, Jira, Mixpanel")),
            List.of()
        );

        // 10. UI/UX Designer
        seedCareer(
            "UI/UX Designer",
            "ui-ux-designer",
            "Design & Product",
            "A UI/UX Designer creates intuitive and visually appealing user interfaces and experiences.",
            "Craft delightful, accessible user journeys and design systems.",
            "Beginner", "4 months", 500000L, 1000000L,
            "https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?auto=format&fit=crop&w=800&q=80",
            "Palette", false, true, 10, "21,400+", 10,
            "Certified Product Design Professional (CPDP)",
            List.of("Figma Mastery", "User Research", "Information Architecture", "Design Systems", "Prototyping"),
            List.of("Design responsive web and mobile interfaces in Figma", "Build accessible component design systems and tokens", "Conduct usability testing sessions and iterate based on feedback"),
            List.of(
                new RoadmapItem("UX Research & Wireframing", "User personas, journey maps, and low-fidelity prototypes.", "3 weeks"),
                new RoadmapItem("Visual Design & Design Systems", "Typography, color theory, layout grids, and Figma components.", "4 weeks"),
                new RoadmapItem("Advanced Interactive Prototyping", "Micro-interactions, component states, and handoff to dev.", "3 weeks")
            ),
            List.of(new ProjectItem("FinTech Mobile Wallet Experience", "Design a complete mobile banking app featuring dark mode, onboarding, and transfers.", "Beginner", "Figma, Protopie, FigJam")),
            courseList(cReact)
        );

        // 11. Cyber Security Analyst
        seedCareer(
            "Cyber Security Analyst",
            "cyber-security-analyst",
            "Security & Infrastructure",
            "A Cyber Security Analyst monitors IT systems, analyzes threats, and protects digital assets.",
            "Guard digital perimeters, detect vulnerabilities, and mitigate attacks.",
            "Intermediate", "6 months", 800000L, 1600000L,
            "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80",
            "Shield", false, false, 11, "19,300+", 12,
            "Certified Cyber Defense Specialist (CCDS)",
            List.of("Threat Intelligence", "Network Security", "SIEM & Splunk", "Ethical Hacking", "SOC Operations"),
            List.of("Investigate security alerts in SIEM tools like Splunk and Sentinel", "Perform vulnerability assessments and pen-testing on web applications", "Formulate incident response playbooks for zero-day threats"),
            List.of(
                new RoadmapItem("Networking & Operating System Security", "TCP/IP, firewalls, Linux hardening, and packet inspection.", "3 weeks"),
                new RoadmapItem("SOC Operations & Threat Hunting", "Log analysis, SIEM correlation rules, and forensic analysis.", "4 weeks"),
                new RoadmapItem("Vulnerability Management & Web App Security", "OWASP Top 10, Burp Suite, and secure code review.", "4 weeks")
            ),
            List.of(new ProjectItem("Simulated Enterprise SOC Incident Response", "Detect, isolate, and document a multi-stage brute force and injection breach.", "Intermediate", "Splunk, Wireshark, Linux, Burp Suite")),
            List.of()
        );

        // 12. Project Manager
        seedCareer(
            "Project Manager",
            "project-manager",
            "Management & Operations",
            "A Project Manager plans, executes, and oversees projects to ensure timely delivery.",
            "Deliver complex projects on time, within budget, and to standard.",
            "Intermediate", "5 months", 800000L, 1600000L,
            "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80",
            "Briefcase", false, false, 12, "22,100+", 10,
            "Certified Agile Project Director (CAPD)",
            List.of("Agile / Scrum", "Jira & Confluence", "Risk Mitigation", "Budgeting & Forecasts", "Stakeholder Management"),
            List.of("Facilitate sprint planning, retrospectives, and standups", "Manage project scope, burn-down charts, and budget allocation", "Mitigate technical and operational blockers across teams"),
            List.of(
                new RoadmapItem("Agile & Scrum Frameworks", "Sprint cadences, backlog grooming, and velocity metrics.", "3 weeks"),
                new RoadmapItem("Project Governance & Risk Planning", "Gantt schedules, resource planning, and critical paths.", "4 weeks"),
                new RoadmapItem("Cross-functional Leadership", "Conflict resolution, executive status reporting, and vendor delivery.", "3 weeks")
            ),
            List.of(new ProjectItem("Multi-Sprint Platform Migration Plan", "Formulate full agile delivery plan for migrating on-prem infrastructure to cloud.", "Intermediate", "Jira, Confluence, Gantt")),
            List.of()
        );

        // 13. Cloud Solutions Architect
        seedCareer(
            "Cloud Solutions Architect",
            "cloud-solutions-architect",
            "Security & Infrastructure",
            "Design robust, scalable cloud infrastructure and enterprise multi-region systems on AWS and GCP.",
            "Architect fault-tolerant, highly available cloud systems.",
            "Advanced", "9 months", 1800000L, 2800000L,
            "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
            "Cloud", true, true, 13, "16,400+", 14,
            "Certified Cloud Solutions Architect (CCSA)",
            List.of("AWS / Azure", "High Availability Design", "Cloud Security", "Serverless", "Cost Optimization"),
            List.of("Design multi-region disaster recovery and failover systems", "Architect serverless and microservices platforms with API Gateways", "Audit and optimize cloud spend across enterprise accounts"),
            List.of(
                new RoadmapItem("Cloud Architecture & Well-Architected Framework", "Reliability, security, efficiency, and cost optimization.", "4 weeks"),
                new RoadmapItem("Enterprise Networking & Hybrid Cloud", "VPCs, DirectConnect, transit gateways, and IAM least privilege.", "5 weeks"),
                new RoadmapItem("Serverless & Event-Driven Patterns", "Lambda, SQS, EventBridge, and global database replicas.", "4 weeks")
            ),
            List.of(new ProjectItem("Multi-Region E-Commerce Cloud Topology", "Architect an active-active global retail platform with 99.99% SLA.", "Advanced", "AWS, Terraform, Route53, DynamoDB Global")),
            courseList(cDevOps, cArch)
        );

        // 14. Data Engineer
        seedCareer(
            "Data Engineer",
            "data-engineer",
            "Data & Analytics",
            "Construct big data pipelines, distributed ETL systems, and streaming data architectures.",
            "Build high-throughput data lakes and automated ingestion engines.",
            "Intermediate", "7 months", 1100000L, 1800000L,
            "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=800&q=80",
            "Layers", true, false, 14, "25,600+", 12,
            "Certified Big Data Engineer (CBDE)",
            List.of("Apache Spark", "Kafka", "Airflow", "Snowflake", "SQL & Python"),
            List.of("Build real-time streaming pipelines with Apache Kafka", "Process terabyte-scale analytical queries using Spark and Databricks", "Orchestrate DAGs and schema migrations in Airflow"),
            List.of(
                new RoadmapItem("Data Ingestion & Distributed Processing", "PySpark, Hadoop HDFS, and batch processing pipelines.", "4 weeks"),
                new RoadmapItem("Streaming Architecture with Kafka", "Producers, consumers, stream processing, and dead-letter queues.", "4 weeks"),
                new RoadmapItem("Data Warehousing & Orchestration", "Snowflake, dbt models, and automated Airflow scheduling.", "4 weeks")
            ),
            List.of(new ProjectItem("Real-time Clickstream Analytics Pipeline", "Ingest web click events into Kafka, process with Spark, and load into Snowflake.", "Intermediate", "Kafka, PySpark, Airflow, Snowflake")),
            courseList(cData, cPython)
        );

        // 15. Mobile App Developer
        seedCareer(
            "Mobile App Developer",
            "mobile-app-developer",
            "Software Engineering",
            "Develop native and cross-platform mobile apps for iOS and Android using React Native & Flutter.",
            "Create fast, fluid mobile experiences for millions of smartphones.",
            "Intermediate", "6 months", 700000L, 1300000L,
            "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?auto=format&fit=crop&w=800&q=80",
            "Smartphone", false, true, 15, "28,900+", 11,
            "Certified Mobile Application Developer (CMAD)",
            List.of("React Native", "Flutter / Dart", "Mobile State Management", "Native Device APIs", "App Store Deployment"),
            List.of("Build smooth 60fps animations and offline-first mobile apps", "Integrate push notifications, camera, geolocation, and biometric auth", "Automate App Store and Google Play release pipelines with Fastlane"),
            List.of(
                new RoadmapItem("Cross-Platform Frameworks", "React Native core, navigation, and styling paradigms.", "3 weeks"),
                new RoadmapItem("Device APIs & Offline Persistence", "SQLite, Realm, camera, biometric auth, and push alerts.", "4 weeks"),
                new RoadmapItem("Performance & App Store Publishing", "Bundle size reduction, Fastlane automation, and review compliance.", "3 weeks")
            ),
            List.of(new ProjectItem("Fitness Tracking & Workout Companion App", "Build an offline-first fitness app with Apple Health / Google Fit sync.", "Intermediate", "React Native, TypeScript, SQLite, Fastlane")),
            courseList(cReact)
        );

        // 16. Site Reliability Engineer
        seedCareer(
            "Site Reliability Engineer",
            "site-reliability-engineer",
            "Security & Infrastructure",
            "Maintain high availability, observability, and automated recovery for mission-critical systems.",
            "Engineer reliability into distributed infrastructure and eliminate toil.",
            "Advanced", "8 months", 1400000L, 2400000L,
            "https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=80",
            "Activity", false, false, 16, "14,800+", 13,
            "Certified Site Reliability Engineer (CSRE)",
            List.of("SLIs / SLOs", "Incident Management", "Chaos Engineering", "Prometheus & Grafana", "Distributed Tracing"),
            List.of("Define and track Service Level Objectives (SLOs) and error budgets", "Set up distributed tracing with OpenTelemetry and Jaeger", "Run chaos engineering experiments to test cluster resilience"),
            List.of(
                new RoadmapItem("Observability & Metrics Engineering", "Prometheus alerts, Grafana dashboards, and OpenTelemetry.", "4 weeks"),
                new RoadmapItem("Chaos Engineering & Self-Healing Systems", "LitmusChaos, auto-scaling, and circuit breakers.", "4 weeks"),
                new RoadmapItem("Incident Response & Production Readiness", "Blameless postmortems, on-call runbooks, and disaster drills.", "4 weeks")
            ),
            List.of(new ProjectItem("Autonomous Kubernetes Self-Healing Watcher", "Build custom Kubernetes controller restarting unhealthy replicas automatically.", "Advanced", "Go, Kubernetes API, Prometheus, Grafana")),
            courseList(cDevOps, cArch)
        );

        // 17. AI Research Scientist
        seedCareer(
            "AI Research Scientist",
            "ai-research-scientist",
            "Data & Analytics",
            "Pioneer state-of-the-art neural architectures, LLMs, and computer vision models.",
            "Conduct cutting-edge machine learning research and publish breakthroughs.",
            "Advanced", "12 months", 2000000L, 3500000L,
            "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?auto=format&fit=crop&w=800&q=80",
            "Sparkles", true, true, 17, "9,400+", 16,
            "Certified AI Research Fellow (CARF)",
            List.of("Deep Learning", "PyTorch / JAX", "NLP & LLMs", "Diffusion Models", "Scientific Writing"),
            List.of("Design novel transformer architectures and optimization algorithms", "Train multi-billion parameter foundation models across GPU clusters", "Publish experimental findings and benchmark evaluation results"),
            List.of(
                new RoadmapItem("Advanced Mathematics for Deep Learning", "Information theory, matrix calculus, and stochastic optimization.", "4 weeks"),
                new RoadmapItem("Modern Generative Modeling", "Diffusion, Latent Consistency Models, and Transformer scaling laws.", "6 weeks"),
                new RoadmapItem("Distributed Multi-GPU Training", "DeepSpeed, Megatron-LM, FSDP, and cluster orchestration.", "5 weeks")
            ),
            List.of(new ProjectItem("Custom Vision-Language Multimodal Transformer", "Train a compact vision-language model on custom multimodal datasets.", "Advanced", "PyTorch, JAX, DeepSpeed, CUDA")),
            courseList(cPython)
        );

        // 18. QA & Automation Engineer
        seedCareer(
            "QA & Automation Engineer",
            "qa-automation-engineer",
            "Software Engineering",
            "Build automated testing suites, CI regression pipelines, and performance benchmark frameworks.",
            "Guarantee software quality, release reliability, and zero-defect deployments.",
            "Beginner", "4 months", 500000L, 900000L,
            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=800&q=80",
            "CheckSquare", false, false, 18, "26,700+", 9,
            "Certified Quality Automation Engineer (CQAE)",
            List.of("Selenium", "Cypress / Playwright", "API Testing (Postman)", "Test Automation", "Jest"),
            List.of("Write end-to-end automated regression suites in Playwright", "Perform automated API contract testing and load testing with k6", "Integrate automated test runs into pull request CI checks"),
            List.of(
                new RoadmapItem("Test Automation Principles & Tools", "Test pyramids, mocking, and Playwright browser automation.", "3 weeks"),
                new RoadmapItem("API & Performance Benchmarking", "Postman Newman, REST Assured, and k6 stress testing.", "4 weeks"),
                new RoadmapItem("Continuous Testing in CI/CD", "Parallel test executions, flaky test detection, and reporting.", "3 weeks")
            ),
            List.of(new ProjectItem("Full-Suite E2E & Load Testing Automation", "Build a complete Playwright + k6 automated test framework for an LMS.", "Beginner", "Playwright, TypeScript, k6, GitHub Actions")),
            courseList(cFullStack)
        );

        // 19. Blockchain Developer
        seedCareer(
            "Blockchain Developer",
            "blockchain-developer",
            "Software Engineering",
            "Architect decentralized applications, audited smart contracts, and cryptographic protocols.",
            "Build secure Web3 applications and audited smart contracts.",
            "Advanced", "8 months", 1400000L, 2600000L,
            "https://images.unsplash.com/photo-1639762681485-074b7f938ba0?auto=format&fit=crop&w=800&q=80",
            "Share2", false, false, 19, "11,200+", 13,
            "Certified Web3 & Smart Contract Developer (CWSD)",
            List.of("Solidity", "Web3.js / Ethers", "Smart Contract Auditing", "Zero-Knowledge Proofs", "Hardhat"),
            List.of("Develop production EVM smart contracts adhering to ERC standards", "Audit contracts for reentrancy, integer overflow, and access flaws", "Integrate web frontends with MetaMask and wallet providers"),
            List.of(
                new RoadmapItem("Smart Contract Engineering in Solidity", "EVM fundamentals, gas optimization, and security patterns.", "4 weeks"),
                new RoadmapItem("DApp Frontend Integration", "Ethers.js, Wagmi, WalletConnect, and contract state queries.", "4 weeks"),
                new RoadmapItem("Auditing & Advanced Protocols", "Static analysis with Slither, automated fuzzing with Foundry.", "4 weeks")
            ),
            List.of(new ProjectItem("Decentralized Escrow & Milestone Payment DApp", "Build an audited Solidity escrow smart contract and web client.", "Advanced", "Solidity, Foundry, Next.js, Ethers.js")),
            courseList(cFullStack)
        );

        // 20. Systems Architect
        seedCareer(
            "Systems Architect",
            "systems-architect",
            "Software Engineering",
            "Direct high-level design choices and technical standards for enterprise-grade distributed systems.",
            "Lead high-level technical direction for mission-critical enterprise systems.",
            "Advanced", "10 months", 2200000L, 3800000L,
            "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80",
            "Layers", true, true, 20, "12,300+", 15,
            "Certified Enterprise Systems Architect (CESA)",
            List.of("System Design", "Distributed Consensus", "Event-Driven Systems", "Scalability", "Domain-Driven Design"),
            List.of("Formulate technical roadmaps and architectural decision records (ADRs)", "Guide domain-driven design (DDD) boundaries across engineering divisions", "Design fault-tolerant distributed consensus with Raft and Paxos"),
            List.of(
                new RoadmapItem("Domain-Driven Design & Enterprise Modeling", "Bounded contexts, aggregates, and ubiquitous language.", "4 weeks"),
                new RoadmapItem("Distributed Systems Theory & Consensus", "CAP theorem, Paxos, Raft, eventual consistency, and sagas.", "5 weeks"),
                new RoadmapItem("Evolutionary Architecture & Governance", "Fitness functions, micro-frontends, and technical strategy.", "4 weeks")
            ),
            List.of(new ProjectItem("Global Financial Transaction Backbone Architecture", "Complete architectural specification for processing 50k transactions/sec with zero loss.", "Advanced", "UML, ADRs, Kafka, Distributed Sagas")),
            courseList(cArch, cJava)
        );

        log.info("Careers initial seed finished. Total careers in database: {}", careerRepository.count());
    }

    private void seedCareer(
        String title,
        String slug,
        String category,
        String description,
        String shortDescription,
        String level,
        String duration,
        Long salaryMin,
        Long salaryMax,
        String imageUrl,
        String icon,
        boolean featured,
        boolean popular,
        int displayOrder,
        String jobOpenings,
        int modulesCount,
        String certificationName,
        List<String> skills,
        List<String> responsibilities,
        List<RoadmapItem> roadmaps,
        List<ProjectItem> projects,
        List<Course> relatedCourses
    ) {
        if (careerRepository.existsBySlug(slug)) {
            log.debug("Career '{}' already exists. Skipping.", slug);
            return;
        }

        Career career = new Career();
        career.setTitle(title);
        career.setSlug(slug);
        career.setCategory(category);
        career.setDescription(description);
        career.setShortDescription(shortDescription);
        career.setLevel(level);
        career.setDuration(duration);
        career.setSalaryMin(salaryMin);
        career.setSalaryMax(salaryMax);
        career.setImageUrl(imageUrl);
        career.setIcon(icon);
        career.setFeatured(featured);
        career.setPopular(popular);
        career.setActive(true);
        career.setDisplayOrder(displayOrder);
        career.setJobOpenings(jobOpenings);
        career.setModulesCount(modulesCount);
        career.setCertificationName(certificationName);

        // Skills
        int skillOrder = 0;
        for (String s : skills) {
            career.addSkill(new CareerSkill(s, "REQUIRED", skillOrder++));
        }

        // Responsibilities
        int respOrder = 0;
        for (String r : responsibilities) {
            career.addResponsibility(new CareerResponsibility(r, respOrder++));
        }

        // Roadmaps
        int rmOrder = 0;
        for (RoadmapItem rm : roadmaps) {
            career.addRoadmap(new CareerRoadmap(rm.title, rm.description, rm.duration, rmOrder++));
        }

        // Projects
        int projOrder = 0;
        for (ProjectItem p : projects) {
            career.addProject(new CareerProject(p.title, p.description, p.difficulty, p.technologies, projOrder++));
        }

        // Courses
        int courseOrder = 0;
        for (Course c : relatedCourses) {
            if (c != null) {
                career.addCourse(new CareerCourse(career, c, courseOrder++));
            }
        }

        // Sample Opportunities
        career.addOpportunity(new CareerOpportunity(title, "TechCorp Global", "Remote / Hybrid", "Full-time", String.format("₹%d–%d LPA", salaryMin / 100000, salaryMax / 100000), 1));
        career.addOpportunity(new CareerOpportunity("Associate " + title, "InnovateHub", "Bangalore / Remote", "Full-time", String.format("₹%d–%d LPA", salaryMin / 100000, (salaryMin + salaryMax) / 200000), 2));

        careerRepository.save(career);
        log.info("Successfully seeded career: {}", slug);
    }

    private Course findCourseByKeyword(List<Course> courses, String keyword) {
        if (courses == null) return null;
        return courses.stream()
            .filter(c -> c.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .findFirst()
            .orElse(null);
    }

    private List<Course> courseList(Course... courses) {
        if (courses == null) return List.of();
        List<Course> list = new java.util.ArrayList<>();
        for (Course c : courses) {
            if (c != null) {
                list.add(c);
            }
        }
        return list;
    }

    private static class RoadmapItem {
        final String title;
        final String description;
        final String duration;
        RoadmapItem(String title, String description, String duration) {
            this.title = title;
            this.description = description;
            this.duration = duration;
        }
    }

    private static class ProjectItem {
        final String title;
        final String description;
        final String difficulty;
        final String technologies;
        ProjectItem(String title, String description, String difficulty, String technologies) {
            this.title = title;
            this.description = description;
            this.difficulty = difficulty;
            this.technologies = technologies;
        }
    }
}
