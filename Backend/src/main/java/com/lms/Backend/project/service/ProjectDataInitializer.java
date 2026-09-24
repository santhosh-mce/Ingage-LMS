package com.lms.Backend.project.service;

import com.lms.Backend.project.entity.Project;
import com.lms.Backend.project.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(3)
public class ProjectDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProjectDataInitializer.class);

    private final ProjectRepository projectRepository;

    public ProjectDataInitializer(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) {
        if (projectRepository.count() > 0) {
            log.info("[ProjectDataInitializer] Projects already seeded. Count: {}", projectRepository.count());
            return;
        }

        log.info("[ProjectDataInitializer] Seeding initial 12 Project tracks...");

        List<ProjectSeedItem> seeds = List.of(
            // Healthcare
            new ProjectSeedItem(
                "Patient Health Tracker",
                "patient-health-tracker",
                "Healthcare",
                "Healthcare",
                "Build a web application to track patient vitals, medications, and appointments with data visualization.",
                "Beginner", "25h", 5, 3420,
                "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=800&q=80",
                List.of("HTML", "CSS", "JavaScript", "Chart.js", "Web Storage"),
                List.of("Dashboard to display patient health metrics", "Medication reminder system with notifications", "Appointment scheduling calendar", "Visual charts showing health trends over time"),
                List.of("Understand web development fundamentals", "Work with data storage and retrieval", "Create interactive user interfaces", "Implement data visualization techniques"),
                List.of("HTML", "CSS", "JavaScript", "Local Storage", "Charts"),
                "No prerequisites required! This project is perfect for beginners.", 1
            ),
            new ProjectSeedItem(
                "Telemedicine Booking System",
                "telemedicine-booking-system",
                "Healthcare",
                "Healthcare",
                "Create a full-stack telemedicine platform for booking doctor appointments and conducting video consultations.",
                "Intermediate", "40h", 5, 2890,
                "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=800&q=80",
                List.of("React", "Node.js", "MongoDB", "WebRTC", "Express"),
                List.of("Patient and doctor profile directories with specialty tags", "Real-time slot reservation system with instant calendar invites", "Secure peer-to-peer WebRTC video consultation room", "Prescription dispatch and encrypted clinical note repository"),
                List.of("Design RESTful microservices with Node.js & Express", "Manage authenticated patient sessions with JWT & bcrypt", "Establish low-latency WebRTC media streams", "Architect MongoDB schemas for medical record audits"),
                List.of("React", "Node.js", "MongoDB", "WebRTC", "Express"),
                "Familiarity with basic JavaScript and React component lifecycles.", 2
            ),

            // Gaming
            new ProjectSeedItem(
                "2D Puzzle Game",
                "2d-puzzle-game",
                "Gaming",
                "Gaming",
                "Develop an interactive 2D puzzle game with multiple levels, scoring, and animations.",
                "Beginner", "30h", 5, 4120,
                "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=800&q=80",
                List.of("JavaScript", "HTML Canvas", "Game Logic", "Audio API", "CSS Grid"),
                List.of("Dynamic HTML Canvas 60fps game render loop", "Grid-based tile movement and gravity physics algorithm", "Multi-tier level progression with high-score storage", "Sound effect synthesizers and victory particle bursts"),
                List.of("Master the HTML5 Canvas 2D rendering context", "Structure game state loops and delta time calculations", "Detect sprite boundary collisions and solve pathfinding", "Synthesize audio triggers via the Web Audio API"),
                List.of("JavaScript", "HTML Canvas", "Game Logic", "Audio API", "CSS Grid"),
                "No game dev experience needed! Basic JavaScript understanding is helpful.", 3
            ),
            new ProjectSeedItem(
                "Multiplayer Quiz Arena",
                "multiplayer-quiz-arena",
                "Gaming",
                "Gaming",
                "Build a real-time multiplayer quiz game where players compete against each other in live trivia battles.",
                "Advanced", "50h", 5, 1950,
                "https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=800&q=80",
                List.of("React", "WebSocket", "Node.js", "Redis", "Tailwind CSS"),
                List.of("Low-latency room orchestration broker with WebSocket channels", "Live countdown timers and concurrent answer scoring engine", "Redis-backed distributed leaderboards and session cache", "Post-match statistics breakdown with question analytics"),
                List.of("Architect bidirectional event-driven communication protocols", "Synchronize state across multiple concurrent browser clients", "Manage in-memory leaderboard ranking with Redis Sorted Sets", "Design responsive battle UIs with animated countdown bars"),
                List.of("React", "WebSocket", "Node.js", "Redis", "Tailwind CSS"),
                "Solid understanding of React hooks and asynchronous Node.js backend patterns.", 4
            ),

            // Smart Cities
            new ProjectSeedItem(
                "Public Transport Tracker",
                "public-transport-tracker",
                "Smart Cities",
                "Smart Cities",
                "Create a real-time tracking application for city buses and trains with interactive maps and arrival predictions.",
                "Intermediate", "35h", 5, 3100,
                "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80",
                List.of("React", "Leaflet", "Transit API", "Python", "FastAPI"),
                List.of("Interactive vector city map with real-time transit vehicle pins", "Live ETA prediction engine calculating traffic-adjusted arrivals", "Route explorer with stop schedules and transfer indicators", "Crowd density reporter enabling commuter feedback"),
                List.of("Render responsive geospatial layers using Leaflet and GeoJSON", "Consume and normalize streaming GTFS transit telemetry", "Build high-throughput async endpoints using FastAPI", "Implement client-side geospatial distance algorithms"),
                List.of("React", "Leaflet", "Transit API", "Python", "FastAPI"),
                "Familiarity with React and introductory Python syntax.", 5
            ),
            new ProjectSeedItem(
                "Smart Parking System",
                "smart-parking-system",
                "Smart Cities",
                "Smart Cities",
                "Develop an IoT-integrated smart parking system that monitors spot availability and enables reservations.",
                "Advanced", "45h", 5, 2240,
                "https://images.unsplash.com/photo-1506521781263-d8422e82f27a?auto=format&fit=crop&w=800&q=80",
                List.of("Next.js", "MQTT", "PostgreSQL", "Tailwind CSS", "Prisma"),
                List.of("Multi-floor parking grid visualization with real-time occupancy beacons", "Sensor ingestion pipeline consuming simulated MQTT telemetry", "Stripe-backed slot reservation and digital ticket generation", "Admin occupancy dashboard with peak-hour revenue heatmaps"),
                List.of("Handle streaming sensor events via lightweight MQTT brokers", "Design relational schemas for time-bracketed spot reservations", "Leverage Next.js Server Components and Prisma ORM", "Implement automated reservation expiry background jobs"),
                List.of("Next.js", "MQTT", "PostgreSQL", "Tailwind CSS", "Prisma"),
                "Experience with relational databases and modern full-stack web architectures.", 6
            ),

            // FinTech
            new ProjectSeedItem(
                "Personal Budget Planner",
                "personal-budget-planner",
                "FinTech",
                "FinTech",
                "Build a comprehensive personal finance app with expense tracking, budgeting categories, and financial goals.",
                "Beginner", "20h", 5, 5230,
                "https://images.unsplash.com/photo-1554224155-6726b3ff858f?auto=format&fit=crop&w=800&q=80",
                List.of("React", "Chart.js", "IndexedDB", "Tailwind CSS"),
                List.of("Multi-category transaction ledger with recurring expense tags", "Visual breakdown charts and monthly budget progress rings", "Target savings goal milestone tracker with confetti celebrations", "Encrypted client-side IndexedDB persistence for offline security"),
                List.of("Structure stateful client applications with clean data flow", "Generate dynamic pie, doughnut, and bar charts with Chart.js", "Implement offline-first client persistence via IndexedDB", "Design accessible financial summary dashboards"),
                List.of("React", "Chart.js", "IndexedDB", "Tailwind CSS"),
                "Basic HTML, CSS, and modern JavaScript fundamentals.", 7
            ),
            new ProjectSeedItem(
                "Cryptocurrency Portfolio Tracker",
                "cryptocurrency-portfolio-tracker",
                "FinTech",
                "FinTech",
                "Create a real-time crypto portfolio tracker with live price updates, profit/loss calculations, and market trends.",
                "Intermediate", "35h", 5, 3670,
                "https://images.unsplash.com/photo-1621416894569-0f39ed31d247?auto=format&fit=crop&w=800&q=80",
                List.of("React", "CoinGecko API", "Recharts", "Node.js", "Express"),
                List.of("Multi-asset holding ledger tracking average buy price and allocation %", "Live pricing polling engine syncing top 250 crypto market caps", "Interactive candlestick and line charts with time-range toggles", "Instant profit/loss calculations with 24h delta badges"),
                List.of("Consume public third-party REST APIs and handle rate-limits", "Render high-performance financial charts with Recharts", "Implement weighted average portfolio math algorithms", "Cache high-frequency market data in Node.js middleware"),
                List.of("React", "CoinGecko API", "Recharts", "Node.js", "Express"),
                "Intermediate knowledge of React hooks and API integration patterns.", 8
            ),

            // EdTech
            new ProjectSeedItem(
                "Interactive Quiz Builder",
                "interactive-quiz-builder",
                "EdTech",
                "EdTech",
                "Build a platform for creating, sharing, and taking interactive quizzes with multiple question types and instant scoring.",
                "Beginner", "25h", 5, 4450,
                "https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?auto=format&fit=crop&w=800&q=80",
                List.of("React", "Firebase", "Tailwind CSS", "React DnD"),
                List.of("Drag-and-drop question builder supporting multiple-choice and true/false", "Timed quiz runner with instant answer validation and explanations", "Shareable public quiz links with custom randomized question orders", "Automated score report and performance percentile card generator"),
                List.of("Implement drag-and-drop interfaces using modern libraries", "Connect React applications to Firebase Firestore databases", "Manage multi-stage form state and validation schemas", "Build responsive quiz review cards and results summaries"),
                List.of("React", "Firebase", "Tailwind CSS", "React DnD"),
                "Familiarity with React components, state, and event handlers.", 9
            ),
            new ProjectSeedItem(
                "Virtual Classroom Platform",
                "virtual-classroom-platform",
                "EdTech",
                "EdTech",
                "Develop a virtual learning environment with video lectures, discussion boards, assignment submissions, and grading.",
                "Advanced", "55h", 5, 1780,
                "https://images.unsplash.com/photo-1501504905252-473c47e087f8?auto=format&fit=crop&w=800&q=80",
                List.of("Next.js", "WebRTC", "PostgreSQL", "Prisma", "Socket.io"),
                List.of("Teacher-led collaborative digital whiteboard with real-time sync", "Multi-participant video grid with screen sharing capabilities", "Structured assignment submission portal with rubric grading tools", "Threaded topic forums with instructor highlight and upvoting"),
                List.of("Coordinate complex mesh WebRTC video streams", "Build synchronized collaborative whiteboards with Socket.io", "Implement role-based access control (Student vs Teacher)", "Manage file asset uploads and relational submission tables"),
                List.of("Next.js", "WebRTC", "PostgreSQL", "Prisma", "Socket.io"),
                "Proficiency with Next.js, database relationships, and real-time sockets.", 10
            ),

            // Manufacturing
            new ProjectSeedItem(
                "Inventory Management System",
                "inventory-management-system",
                "Manufacturing",
                "Manufacturing",
                "Create an enterprise inventory system with stock tracking, reorder alerts, supplier management, and reporting.",
                "Intermediate", "40h", 5, 2980,
                "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80",
                List.of("React", "Spring Boot", "PostgreSQL", "Docker"),
                List.of("SKU catalog with low-stock warning banners and threshold alerts", "Purchase order and automated supplier dispatch workflow", "Barcode and QR code scanner integration for warehouse intake", "Exportable PDF and Excel inventory audits and turnover reports"),
                List.of("Connect React to enterprise Spring Boot REST microservices", "Manage relational inventory transactions in PostgreSQL", "Containerize full-stack services using Docker Compose", "Implement reliable stock reservation concurrency controls"),
                List.of("React", "Spring Boot", "PostgreSQL", "Docker"),
                "Basic understanding of full-stack API patterns and SQL queries.", 11
            ),
            new ProjectSeedItem(
                "Production Line Monitor",
                "production-line-monitor",
                "Manufacturing",
                "Manufacturing",
                "Build an industrial IoT dashboard that monitors assembly line throughput, machine health, and downtime analytics.",
                "Advanced", "45h", 5, 1620,
                "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=800&q=80",
                List.of("Vue.js", "Kafka", "InfluxDB", "Grafana", "Node.js"),
                List.of("Real-time assembly line sensor telemetry streams with vibration and heat alerts", "Overall Equipment Effectiveness (OEE) KPI computation engine", "Predictive maintenance warning cards before anomaly thresholds", "Historical shift reports analyzing throughput downtime reasons"),
                List.of("Process high-velocity sensor telemetry with Apache Kafka", "Store and aggregate metric time-series data in InfluxDB", "Design industrial SCADA-style web monitoring panels", "Calculate standard industrial efficiency indices (OEE, MTBF)"),
                List.of("Vue.js", "Kafka", "InfluxDB", "Grafana", "Node.js"),
                "Understanding of event streaming, time-series data, and component states.", 12
            )
        );

        for (ProjectSeedItem s : seeds) {
            Project p = new Project();
            p.setTitle(s.title);
            p.setSlug(s.slug);
            p.setCategory(s.category);
            p.setIndustry(s.industry);
            p.setDescription(s.description);
            p.setDifficulty(s.difficulty);
            p.setDuration(s.duration);
            p.setSkillsCount(s.skillsCount);
            p.setLearnersCount(s.learnersCount);
            p.setImageUrl(s.imageUrl);
            p.setTechStack(s.techStack);
            p.setWhatYouWillBuild(s.whatYouWillBuild);
            p.setLearningOutcomes(s.learningOutcomes);
            p.setSkillsLearned(s.skillsLearned);
            p.setPrerequisites(s.prerequisites);
            p.setDisplayOrder(s.displayOrder);
            p.setActive(true);
            p.setPublished(true);
            projectRepository.save(p);
        }

        log.info("[ProjectDataInitializer] Successfully seeded {} Project tracks.", seeds.size());
    }

    private static class ProjectSeedItem {
        String title;
        String slug;
        String category;
        String industry;
        String description;
        String difficulty;
        String duration;
        Integer skillsCount;
        Integer learnersCount;
        String imageUrl;
        List<String> techStack;
        List<String> whatYouWillBuild;
        List<String> learningOutcomes;
        List<String> skillsLearned;
        String prerequisites;
        Integer displayOrder;

        ProjectSeedItem(String title, String slug, String category, String industry, String description,
                        String difficulty, String duration, Integer skillsCount, Integer learnersCount,
                        String imageUrl, List<String> techStack, List<String> whatYouWillBuild,
                        List<String> learningOutcomes, List<String> skillsLearned, String prerequisites,
                        Integer displayOrder) {
            this.title = title;
            this.slug = slug;
            this.category = category;
            this.industry = industry;
            this.description = description;
            this.difficulty = difficulty;
            this.duration = duration;
            this.skillsCount = skillsCount;
            this.learnersCount = learnersCount;
            this.imageUrl = imageUrl;
            this.techStack = techStack;
            this.whatYouWillBuild = whatYouWillBuild;
            this.learningOutcomes = learningOutcomes;
            this.skillsLearned = skillsLearned;
            this.prerequisites = prerequisites;
            this.displayOrder = displayOrder;
        }
    }
}
