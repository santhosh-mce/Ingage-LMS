import { JobRole, ProjectTrack, JobOpportunity } from '../types';

export const JOB_ROLES: JobRole[] = [
  {
    id: 'data-analyst',
    slug: 'data-analyst',
    title: 'Data Analyst',
    category: 'Data & Analytics',
    description: 'A Data Analyst collects, cleans, and interprets data sets to answer questions or solve problems.',
    level: 'Beginner',
    duration: '5 months',
    avgSalary: '₹6–10 LPA',
    jobOpenings: '27,098+',
    modulesCount: 10,
    trending: true,
    iconName: 'BarChart3',
    imageUrl: 'https://images.unsplash.com/photo-1551836022-d5d88e9218df?auto=format&fit=crop&w=800&q=80',
    skills: ['SQL', 'Tableau', 'Python for Data Analysis', 'Excel Analytics', 'Statistical Modeling'],
    prerequisites: ['Basic mathematics', 'Logical problem solving'],
    certificationName: 'Certified Enterprise Data Analyst (CEDA)',
    modules: [
      {
        id: 'da-m1',
        title: 'Foundations of Modern Data Analysis',
        duration: '3 weeks',
        lessonsCount: 6,
        description: 'Master spreadsheet analytics, business metrics, and core statistics.',
        isUnlocked: true,
        isCompleted: true,
        lessons: [
          { id: 'da-l1', title: 'Welcome & Role Competency Matrix', duration: '8 min', type: 'video', isCompleted: true },
          { id: 'da-l2', title: 'Data Cleaning Principles in Real-world Business', duration: '14 min', type: 'video', isCompleted: true },
          { id: 'da-l3', title: 'Module 1 Knowledge Check & Practice Quiz', duration: '20 min', type: 'quiz', isCompleted: true, passingScore: 70 }
        ]
      },
      {
        id: 'da-m2',
        title: 'Relational Databases & Advanced SQL',
        duration: '4 weeks',
        lessonsCount: 8,
        description: 'Complex joins, window functions, CTEs, and query optimization for warehouse scale.',
        isUnlocked: true,
        isCompleted: false,
        lessons: [
          { id: 'da-l4', title: 'SQL Joins, Aggregations & Window Functions', duration: '18 min', type: 'video', isCompleted: true },
          { id: 'da-l5', title: 'Cohort Retention Queries & Case Studies', duration: '22 min', type: 'video', isCompleted: false },
          { id: 'da-l6', title: 'SQL Practical Assessment (Checkpoint Quiz)', duration: '25 min', type: 'quiz', isCompleted: false, passingScore: 70 }
        ]
      },
      {
        id: 'da-m3',
        title: 'Interactive BI Dashboards with Tableau & PowerBI',
        duration: '4 weeks',
        lessonsCount: 7,
        description: 'Transform raw data into executive dashboards that drive key decisions.',
        isUnlocked: false,
        isCompleted: false,
        lessons: [
          { id: 'da-l7', title: 'Design Philosophy of Executive Dashboards', duration: '16 min', type: 'video', isCompleted: false },
          { id: 'da-l8', title: 'Interactive Filters and Drill-down Paths', duration: '20 min', type: 'video', isCompleted: false }
        ]
      },
      {
        id: 'da-m4',
        title: 'Capstone: End-to-End Enterprise Growth Analytics',
        duration: '5 weeks',
        lessonsCount: 4,
        description: 'Live simulated dataset with 1M rows evaluating churn and lifetime customer value.',
        isUnlocked: false,
        isCompleted: false,
        lessons: [
          { id: 'da-l9', title: 'Capstone Project Brief & Industry Guidelines', duration: '12 min', type: 'reading', isCompleted: false },
          { id: 'da-l10', title: 'Peer Review & Defense Presentation', duration: '45 min', type: 'project', isCompleted: false }
        ]
      }
    ]
  },
  {
    id: 'data-scientist',
    slug: 'data-scientist',
    title: 'Data Scientist',
    category: 'Data & Analytics',
    description: 'A Data Scientist analyzes large datasets to uncover insights and build predictive models.',
    level: 'Intermediate',
    duration: '8 months',
    avgSalary: '₹12–18 LPA',
    jobOpenings: '23,744+',
    modulesCount: 15,
    trending: true,
    iconName: 'Database',
    imageUrl: 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80',
    skills: ['Python', 'Machine Learning', 'Pandas & NumPy', 'Deep Learning', 'MLOps & Deployment'],
    prerequisites: ['Python basics', 'Linear algebra & probability'],
    certificationName: 'Certified Applied Data Scientist (CADS)',
    modules: [
      {
        id: 'ds-m1',
        title: 'Scientific Computing with NumPy and Scipy',
        duration: '3 weeks',
        lessonsCount: 6,
        description: 'Vectorized computing and mathematical data foundations.',
        isUnlocked: true,
        isCompleted: true,
        lessons: [
          { id: 'ds-l1', title: 'High performance numerical arrays', duration: '15 min', type: 'video', isCompleted: true }
        ]
      },
      {
        id: 'ds-m2',
        title: 'Supervised Learning Algorithms',
        duration: '4 weeks',
        lessonsCount: 8,
        description: 'Regression, Decision Trees, Ensemble Random Forests and XGBoost.',
        isUnlocked: true,
        isCompleted: false,
        lessons: [
          { id: 'ds-l2', title: 'Gradient Boosted Trees in Practice', duration: '24 min', type: 'video', isCompleted: false }
        ]
      }
    ]
  },
  {
    id: 'digital-marketing-specialist',
    slug: 'digital-marketing-specialist',
    title: 'Digital Marketing Specialist',
    category: 'Growth & Marketing',
    description: 'A Digital Marketing Specialist manages campaigns, optimizes SEO, and drives online engagement.',
    level: 'Beginner',
    duration: '4 months',
    avgSalary: '₹4–8 LPA',
    jobOpenings: '41,506+',
    modulesCount: 9,
    trending: true,
    iconName: 'TrendingUp',
    imageUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80',
    skills: ['Google Ads', 'SEO Strategy', 'Performance Analytics', 'Social Media Paid Ads', 'Email Funnels'],
    prerequisites: ['Good written communication', 'Analytical mindset'],
    certificationName: 'Certified Growth & Digital Marketer (CGDM)',
    modules: []
  },
  {
    id: 'machine-learning-engineer',
    slug: 'machine-learning-engineer',
    title: 'Machine Learning Engineer',
    category: 'Data & Analytics',
    description: 'A Machine Learning Engineer builds and optimizes algorithms that enable computers to learn.',
    level: 'Advanced',
    duration: '10 months',
    avgSalary: '₹15–25 LPA',
    jobOpenings: '18,932+',
    modulesCount: 16,
    trending: true,
    iconName: 'Bot',
    imageUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80',
    skills: ['PyTorch', 'TensorFlow', 'Model Serving', 'Transformers', 'Distributed Training'],
    prerequisites: ['Strong Python', 'Calculus & Linear Algebra', 'Data structures'],
    certificationName: 'Certified Machine Learning Architect (CMLA)',
    modules: []
  },
  {
    id: 'business-intelligence-analyst',
    slug: 'business-intelligence-analyst',
    title: 'Business Intelligence Analyst',
    category: 'Data & Analytics',
    description: 'A Business Intelligence Analyst transforms data into actionable insights for strategic decisions.',
    level: 'Intermediate',
    duration: '6 months',
    avgSalary: '₹7–12 LPA',
    jobOpenings: '19,876+',
    modulesCount: 11,
    trending: false,
    iconName: 'LineChart',
    imageUrl: 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80',
    skills: ['Power BI', 'SQL Warehousing', 'DAX', 'ETL Pipelines', 'Executive Reporting'],
    prerequisites: ['Basic business acumen', 'Spreadsheet fluency'],
    certificationName: 'Certified BI Professional (CBIP)',
    modules: []
  },
  {
    id: 'full-stack-developer',
    slug: 'full-stack-developer',
    title: 'Full Stack Developer',
    category: 'Software Engineering',
    description: 'Master both frontend and backend development to build complete web applications from scratch.',
    level: 'Intermediate',
    duration: '8 months',
    avgSalary: '₹8–15 LPA',
    jobOpenings: '45,420+',
    modulesCount: 12,
    trending: true,
    iconName: 'Code',
    imageUrl: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=800&q=80',
    skills: ['React & Next.js', 'Node.js', 'PostgreSQL', 'TypeScript', 'Docker'],
    prerequisites: ['HTML/CSS and JavaScript fundamentals'],
    certificationName: 'Certified Full Stack Engineer (CFSE)',
    modules: []
  },
  {
    id: 'backend-developer',
    slug: 'backend-developer',
    title: 'Backend Developer',
    category: 'Software Engineering',
    description: 'Build scalable server-side applications, APIs, and database systems.',
    level: 'Intermediate',
    duration: '6 months',
    avgSalary: '₹7–14 LPA',
    jobOpenings: '31,800+',
    modulesCount: 11,
    trending: false,
    iconName: 'Settings',
    imageUrl: 'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=800&q=80',
    skills: ['Node.js', 'Go / Python', 'REST & gRPC', 'Microservices', 'Redis Caching'],
    prerequisites: ['Foundational programming experience'],
    certificationName: 'Certified Backend Developer (CBD)',
    modules: []
  },
  {
    id: 'devops-engineer',
    slug: 'devops-engineer',
    title: 'DevOps Engineer',
    category: 'Security & Infrastructure',
    description: 'Automate deployment pipelines and ensure smooth software delivery processes.',
    level: 'Intermediate',
    duration: '7 months',
    avgSalary: '₹10–18 LPA',
    jobOpenings: '24,300+',
    modulesCount: 12,
    trending: true,
    iconName: 'Cpu',
    imageUrl: 'https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?auto=format&fit=crop&w=800&q=80',
    skills: ['Kubernetes', 'Docker', 'Terraform', 'CI/CD Pipelines', 'AWS / GCP Cloud'],
    prerequisites: ['Linux command line', 'Basic networking knowledge'],
    certificationName: 'Certified DevOps Engineer (CDE)',
    modules: []
  },
  {
    id: 'product-manager',
    slug: 'product-manager',
    title: 'Product Manager',
    category: 'Design & Product',
    description: 'Define product strategy, roadmap, and features to deliver customer value.',
    level: 'Intermediate',
    duration: '6 months',
    avgSalary: '₹12–22 LPA',
    jobOpenings: '18,600+',
    modulesCount: 10,
    trending: false,
    iconName: 'Box',
    imageUrl: 'https://images.unsplash.com/photo-1531403009284-440f080d1e12?auto=format&fit=crop&w=800&q=80',
    skills: ['Product Strategy', 'User Stories', 'Roadmapping', 'A/B Testing', 'Agile Delivery'],
    prerequisites: ['Effective communication', 'Analytical thinking'],
    certificationName: 'Certified Product Manager (CPM)',
    modules: []
  },
  {
    id: 'ui-ux-designer',
    slug: 'ui-ux-designer',
    title: 'UI/UX Designer',
    category: 'Design & Product',
    description: 'A UI/UX Designer creates intuitive and visually appealing user interfaces and experiences.',
    level: 'Beginner',
    duration: '4 months',
    avgSalary: '₹5–10 LPA',
    jobOpenings: '21,400+',
    modulesCount: 10,
    trending: false,
    iconName: 'Palette',
    imageUrl: 'https://images.unsplash.com/photo-1581291518857-4e27b48ff24e?auto=format&fit=crop&w=800&q=80',
    skills: ['Figma Mastery', 'User Research', 'Information Architecture', 'Design Systems', 'Prototyping'],
    prerequisites: ['Visual curiosity', 'Basic computer literacy'],
    certificationName: 'Certified Product Design Professional (CPDP)',
    modules: []
  },
  {
    id: 'cyber-security-analyst',
    slug: 'cyber-security-analyst',
    title: 'Cyber Security Analyst',
    category: 'Security & Infrastructure',
    description: 'A Cyber Security Analyst monitors IT systems, analyzes threats, and protects digital assets.',
    level: 'Intermediate',
    duration: '6 months',
    avgSalary: '₹8–16 LPA',
    jobOpenings: '19,300+',
    modulesCount: 12,
    trending: false,
    iconName: 'Shield',
    imageUrl: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80',
    skills: ['Threat Intelligence', 'Network Security', 'SIEM & Splunk', 'Ethical Hacking', 'SOC Operations'],
    prerequisites: ['Networking fundamentals', 'Linux command line'],
    certificationName: 'Certified Cyber Defense Specialist (CCDS)',
    modules: []
  },
  {
    id: 'project-manager',
    slug: 'project-manager',
    title: 'Project Manager',
    category: 'Management & Operations',
    description: 'A Project Manager plans, executes, and oversees projects to ensure timely delivery.',
    level: 'Intermediate',
    duration: '5 months',
    avgSalary: '₹8–16 LPA',
    jobOpenings: '22,100+',
    modulesCount: 10,
    trending: false,
    iconName: 'Briefcase',
    imageUrl: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80',
    skills: ['Agile / Scrum', 'Jira & Confluence', 'Risk Mitigation', 'Budgeting & Forecasts', 'Stakeholder Management'],
    prerequisites: ['Organizational skills', 'Team collaboration experience'],
    certificationName: 'Certified Agile Project Director (CAPD)',
    modules: []
  },
  {
    id: 'cloud-solutions-architect',
    slug: 'cloud-solutions-architect',
    title: 'Cloud Solutions Architect',
    category: 'Security & Infrastructure',
    description: 'Design robust, scalable cloud infrastructure and enterprise multi-region systems on AWS and GCP.',
    level: 'Advanced',
    duration: '9 months',
    avgSalary: '₹18–28 LPA',
    jobOpenings: '16,400+',
    modulesCount: 14,
    trending: true,
    iconName: 'Cloud',
    imageUrl: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80',
    skills: ['AWS / Azure', 'High Availability Design', 'Cloud Security', 'Serverless', 'Cost Optimization'],
    prerequisites: ['System architecture experience', 'Networking principles'],
    certificationName: 'Certified Cloud Solutions Architect (CCSA)',
    modules: []
  },
  {
    id: 'data-engineer',
    slug: 'data-engineer',
    title: 'Data Engineer',
    category: 'Data & Analytics',
    description: 'Construct big data pipelines, distributed ETL systems, and streaming data architectures.',
    level: 'Intermediate',
    duration: '7 months',
    avgSalary: '₹11–18 LPA',
    jobOpenings: '25,600+',
    modulesCount: 12,
    trending: true,
    iconName: 'Layers',
    imageUrl: 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=800&q=80',
    skills: ['Apache Spark', 'Kafka', 'Airflow', 'Snowflake', 'SQL & Python'],
    prerequisites: ['Relational SQL', 'Basic Python scripting'],
    certificationName: 'Certified Big Data Engineer (CBDE)',
    modules: []
  },
  {
    id: 'mobile-app-developer',
    slug: 'mobile-app-developer',
    title: 'Mobile App Developer',
    category: 'Software Engineering',
    description: 'Develop native and cross-platform mobile apps for iOS and Android using React Native & Flutter.',
    level: 'Intermediate',
    duration: '6 months',
    avgSalary: '₹7–13 LPA',
    jobOpenings: '28,900+',
    modulesCount: 11,
    trending: false,
    iconName: 'Smartphone',
    imageUrl: 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c?auto=format&fit=crop&w=800&q=80',
    skills: ['React Native', 'Flutter / Dart', 'Mobile State Management', 'Native Device APIs', 'App Store Deployment'],
    prerequisites: ['JavaScript or OOP foundations'],
    certificationName: 'Certified Mobile Application Developer (CMAD)',
    modules: []
  },
  {
    id: 'site-reliability-engineer',
    slug: 'site-reliability-engineer',
    title: 'Site Reliability Engineer',
    category: 'Security & Infrastructure',
    description: 'Maintain high availability, observability, and automated recovery for mission-critical systems.',
    level: 'Advanced',
    duration: '8 months',
    avgSalary: '₹14–24 LPA',
    jobOpenings: '14,800+',
    modulesCount: 13,
    trending: false,
    iconName: 'Activity',
    imageUrl: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=800&q=80',
    skills: ['SLIs / SLOs', 'Incident Management', 'Chaos Engineering', 'Prometheus & Grafana', 'Distributed Tracing'],
    prerequisites: ['Linux internals', 'Networking and scripting'],
    certificationName: 'Certified Site Reliability Engineer (CSRE)',
    modules: []
  },
  {
    id: 'ai-research-scientist',
    slug: 'ai-research-scientist',
    title: 'AI Research Scientist',
    category: 'Data & Analytics',
    description: 'Pioneer state-of-the-art neural architectures, LLMs, and computer vision models.',
    level: 'Advanced',
    duration: '12 months',
    avgSalary: '₹20–35 LPA',
    jobOpenings: '9,400+',
    modulesCount: 16,
    trending: true,
    iconName: 'Sparkles',
    imageUrl: 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?auto=format&fit=crop&w=800&q=80',
    skills: ['Deep Learning', 'PyTorch / JAX', 'NLP & LLMs', 'Diffusion Models', 'Scientific Writing'],
    prerequisites: ['Multivariate calculus', 'Deep knowledge of linear algebra'],
    certificationName: 'Certified AI Research Fellow (CARF)',
    modules: []
  },
  {
    id: 'qa-automation-engineer',
    slug: 'qa-automation-engineer',
    title: 'QA & Automation Engineer',
    category: 'Software Engineering',
    description: 'Build automated testing suites, CI regression pipelines, and performance benchmark frameworks.',
    level: 'Beginner',
    duration: '4 months',
    avgSalary: '₹5–9 LPA',
    jobOpenings: '26,700+',
    modulesCount: 9,
    trending: false,
    iconName: 'CheckSquare',
    imageUrl: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=800&q=80',
    skills: ['Selenium', 'Cypress / Playwright', 'API Testing (Postman)', 'Test Automation', 'Jest'],
    prerequisites: ['Basic JavaScript or Python knowledge'],
    certificationName: 'Certified Quality Automation Engineer (CQAE)',
    modules: []
  },
  {
    id: 'blockchain-developer',
    slug: 'blockchain-developer',
    title: 'Blockchain Developer',
    category: 'Software Engineering',
    description: 'Architect decentralized applications, audited smart contracts, and cryptographic protocols.',
    level: 'Advanced',
    duration: '8 months',
    avgSalary: '₹14–26 LPA',
    jobOpenings: '11,200+',
    modulesCount: 13,
    trending: false,
    iconName: 'Share2',
    imageUrl: 'https://images.unsplash.com/photo-1639762681485-074b7f938ba0?auto=format&fit=crop&w=800&q=80',
    skills: ['Solidity', 'Web3.js / Ethers', 'Smart Contract Auditing', 'Zero-Knowledge Proofs', 'Hardhat'],
    prerequisites: ['Object oriented programming', 'Basic cryptography'],
    certificationName: 'Certified Web3 & Smart Contract Developer (CWSD)',
    modules: []
  },
  {
    id: 'systems-architect',
    slug: 'systems-architect',
    title: 'Systems Architect',
    category: 'Software Engineering',
    description: 'Direct high-level design choices and technical standards for enterprise-grade distributed systems.',
    level: 'Advanced',
    duration: '10 months',
    avgSalary: '₹22–38 LPA',
    jobOpenings: '12,100+',
    modulesCount: 15,
    trending: false,
    iconName: 'Cpu',
    imageUrl: 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=800&q=80',
    skills: ['Distributed Systems', 'CAP Theorem', 'Domain-Driven Design', 'Event-Driven Architecture', 'Enterprise Security'],
    prerequisites: ['Extensive software engineering leadership'],
    certificationName: 'Certified Enterprise Systems Architect (CESA)',
    modules: []
  }
];

export const PROJECTS_TRACKS: ProjectTrack[] = [
  // Healthcare (2 projects)
  {
    id: 'proj-patient-health-tracker',
    title: 'Patient Health Tracker',
    industry: 'Healthcare',
    category: 'Healthcare',
    description: 'Build a web application to track patient vitals, medications, and appointments with data visualization.',
    difficulty: 'Beginner',
    duration: '25h',
    skillsCount: 5,
    techStack: ['HTML', 'CSS', 'JavaScript', 'Chart.js', 'Web Storage'],
    learnersCount: 3420,
    imageUrl: 'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Dashboard to display patient health metrics',
      'Medication reminder system with notifications',
      'Appointment scheduling calendar',
      'Visual charts showing health trends over time'
    ],
    learningOutcomes: [
      'Understand web development fundamentals',
      'Work with data storage and retrieval',
      'Create interactive user interfaces',
      'Implement data visualization techniques'
    ],
    skillsLearned: ['HTML', 'CSS', 'JavaScript', 'Local Storage', 'Charts'],
    prerequisites: 'No prerequisites required! This project is perfect for beginners.'
  },
  {
    id: 'proj-telemedicine-booking',
    title: 'Telemedicine Booking System',
    industry: 'Healthcare',
    category: 'Healthcare',
    description: 'Create a full-stack telemedicine platform for booking doctor appointments and conducting video consultations.',
    difficulty: 'Intermediate',
    duration: '40h',
    skillsCount: 5,
    techStack: ['React', 'Node.js', 'MongoDB', 'WebRTC', 'Express'],
    learnersCount: 2890,
    imageUrl: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Patient and doctor profile directories with specialty tags',
      'Real-time slot reservation system with instant calendar invites',
      'Secure peer-to-peer WebRTC video consultation room',
      'Prescription dispatch and encrypted clinical note repository'
    ],
    learningOutcomes: [
      'Design RESTful microservices with Node.js & Express',
      'Manage authenticated patient sessions with JWT & bcrypt',
      'Establish low-latency WebRTC media streams',
      'Architect MongoDB schemas for medical record audits'
    ],
    skillsLearned: ['React', 'Node.js', 'MongoDB', 'WebRTC', 'Express'],
    prerequisites: 'Familiarity with basic JavaScript and React component lifecycles.'
  },

  // Gaming (2 projects)
  {
    id: 'proj-2d-puzzle-game',
    title: '2D Puzzle Game',
    industry: 'Gaming',
    category: 'Gaming',
    description: 'Develop an interactive 2D puzzle game with multiple levels, scoring, and animations.',
    difficulty: 'Beginner',
    duration: '30h',
    skillsCount: 5,
    techStack: ['JavaScript', 'HTML Canvas', 'Game Logic', 'Audio API', 'CSS Grid'],
    learnersCount: 4120,
    imageUrl: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Dynamic HTML Canvas 60fps game render loop',
      'Grid-based tile movement and gravity physics algorithm',
      'Multi-tier level progression with high-score storage',
      'Sound effect synthesizers and victory particle bursts'
    ],
    learningOutcomes: [
      'Master the HTML5 Canvas 2D rendering context',
      'Structure game state loops and delta time calculations',
      'Detect sprite boundary collisions and solve pathfinding',
      'Synthesize audio triggers via the Web Audio API'
    ],
    skillsLearned: ['JavaScript', 'HTML Canvas', 'Game Logic', 'Audio API', 'CSS Grid'],
    prerequisites: 'No game dev experience needed! Basic JavaScript understanding is helpful.'
  },
  {
    id: 'proj-multiplayer-quiz-arena',
    title: 'Multiplayer Quiz Arena',
    industry: 'Gaming',
    category: 'Gaming',
    description: 'Build a real-time multiplayer quiz game where players compete against each other in live trivia battles.',
    difficulty: 'Advanced',
    duration: '50h',
    skillsCount: 5,
    techStack: ['React', 'WebSocket', 'Node.js', 'Redis', 'Tailwind CSS'],
    learnersCount: 3780,
    imageUrl: 'https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Real-time matchmaking lobby with room codes and player avatars',
      'Synchronized countdown timers and answer broadcasts via WebSockets',
      'Redis in-memory leaderboard with live score rank recalculations',
      'Interactive spectator mode and post-match victory celebrations'
    ],
    learningOutcomes: [
      'Implement duplex bidirectional WebSocket communication',
      'Manage concurrent distributed room states with Redis pub/sub',
      'Handle race conditions and latency compensation in competitive games',
      'Build responsive, responsive game interfaces with Tailwind CSS'
    ],
    skillsLearned: ['React', 'WebSocket', 'Node.js', 'Redis', 'Tailwind CSS'],
    prerequisites: 'Solid foundation in React, Node.js, and asynchronous programming.'
  },

  // Smart Cities (2 projects)
  {
    id: 'proj-public-transport-tracker',
    title: 'Public Transport Tracker',
    industry: 'Smart Cities',
    category: 'Smart Cities',
    description: 'Create a mobile app to track public buses and trains in real-time with route planning.',
    difficulty: 'Intermediate',
    duration: '35h',
    skillsCount: 4,
    techStack: ['React Native', 'Google Maps API', 'Real-time Data', 'GeoJSON'],
    learnersCount: 2650,
    imageUrl: 'https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Interactive transit vector map displaying live vehicle coordinates',
      'Turn-by-turn multimodal route planning between arbitrary city stops',
      'Arrival time prediction algorithms based on current road traffic',
      'Offline transit schedule caching with bookmarkable commute routes'
    ],
    learningOutcomes: [
      'Render GeoJSON feature layers and smooth marker transitions',
      'Parse real-time transit telemetry streams (GTFS-RT)',
      'Optimize mobile rendering performance for high-frequency coordinate updates',
      'Work with device location permissions and geofencing triggers'
    ],
    skillsLearned: ['React Native', 'Google Maps API', 'Real-time Data', 'GeoJSON'],
    prerequisites: 'Basic knowledge of JavaScript and mobile component paradigms.'
  },
  {
    id: 'proj-smart-parking-system',
    title: 'Smart Parking System',
    industry: 'Smart Cities',
    category: 'Smart Cities',
    description: 'Design a smart parking solution with sensor integration, occupancy tracking, and automated billing.',
    difficulty: 'Advanced',
    duration: '45h',
    skillsCount: 5,
    techStack: ['IoT', 'Python', 'Sensors', 'MQTT', 'Dashboard UI'],
    learnersCount: 1980,
    imageUrl: 'https://images.unsplash.com/photo-1506521781263-d8422e82f27a?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'MQTT message ingestion pipeline from ultrasonic bay sensors',
      'Live parking garage layout visualizer with color-coded bay statuses',
      'License plate OCR simulated check-in and barrier gate controllers',
      'Automated parking duration calculator and contactless payment gateway'
    ],
    learningOutcomes: [
      'Connect IoT sensors with lightweight publish-subscribe MQTT brokers',
      'Process high-throughput sensor telemetry in Python event loops',
      'Design clean administrative dashboards with SVG floorplans',
      'Implement transactional payment and billing reconciliation logic'
    ],
    skillsLearned: ['IoT', 'Python', 'Sensors', 'MQTT', 'Dashboard UI'],
    prerequisites: 'Comfortable with Python and basic networking protocols.'
  },

  // FinTech (2 projects)
  {
    id: 'proj-personal-budget-planner',
    title: 'Personal Budget Planner',
    industry: 'FinTech',
    category: 'FinTech',
    description: 'Build a budget tracking app to manage income, expenses, and savings goals with visual analytics.',
    difficulty: 'Beginner',
    duration: '20h',
    skillsCount: 4,
    techStack: ['React', 'Chart.js', 'Local Storage', 'Tailwind CSS'],
    learnersCount: 5310,
    imageUrl: 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Income and recurring expense category manager with balance ledger',
      'Interactive monthly budget allocation rings and target meters',
      'Visual breakdown charts displaying spending velocity by category',
      'CSV transaction export and recurring bill payment notifications'
    ],
    learningOutcomes: [
      'Create reusable stateful UI forms with strict input validation',
      'Manage persistent financial data using browser local storage',
      'Configure interactive Chart.js donut and line graphs',
      'Implement clean, accessible layouts using Tailwind CSS'
    ],
    skillsLearned: ['React', 'Chart.js', 'Local Storage', 'Tailwind CSS'],
    prerequisites: 'No prerequisites required! Accessible to everyone starting with React.'
  },
  {
    id: 'proj-crypto-portfolio-tracker',
    title: 'Cryptocurrency Portfolio Tracker',
    industry: 'FinTech',
    category: 'FinTech',
    description: 'Create a crypto portfolio manager that tracks holdings, prices via API, and calculates profit/loss.',
    difficulty: 'Intermediate',
    duration: '35h',
    skillsCount: 5,
    techStack: ['React', 'Crypto APIs', 'Tailwind CSS', 'Axios', 'ApexCharts'],
    learnersCount: 3940,
    imageUrl: 'https://images.unsplash.com/photo-1621416894569-0f39ed31d247?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Live ticker tracking top 100 cryptocurrencies via public REST APIs',
      'Holdings management system calculating average cost basis and ROI',
      'Interactive candlestick and area charts with historical price range toggles',
      'Price alert threshold triggers with desktop push notifications'
    ],
    learningOutcomes: [
      'Poll and cache third-party REST API endpoints efficiently',
      'Compute weighted profit and loss calculations across currency pairs',
      'Visualize financial time-series data with ApexCharts',
      'Design dark-mode compliant financial dashboard components'
    ],
    skillsLearned: ['React', 'Crypto APIs', 'Tailwind CSS', 'Axios', 'ApexCharts'],
    prerequisites: 'Basic familiarity with React hooks (useState, useEffect) and HTTP requests.'
  },

  // EdTech (2 projects)
  {
    id: 'proj-interactive-quiz-builder',
    title: 'Interactive Quiz Builder',
    industry: 'EdTech',
    category: 'EdTech',
    description: 'Develop a quiz creation and assessment tool for educators and students with auto-grading.',
    difficulty: 'Beginner',
    duration: '25h',
    skillsCount: 4,
    techStack: ['JavaScript', 'HTML/CSS', 'LocalStorage', 'State Management'],
    learnersCount: 3120,
    imageUrl: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Drag-and-drop question authoring canvas with multiple question formats',
      'Student timed examination interface with immediate visual feedback',
      'Automated grading engine calculating percentiles and score breakdowns',
      'Sharable quiz links and printable student assessment report cards'
    ],
    learningOutcomes: [
      'Manage complex multi-step form state and quiz submission payloads',
      'Implement client-side auto-grading and time limit mechanisms',
      'Store quizzes, question banks, and student scores locally',
      'Build responsive, mobile-friendly test-taking interfaces'
    ],
    skillsLearned: ['JavaScript', 'HTML/CSS', 'LocalStorage', 'State Management'],
    prerequisites: 'No prerequisites required! Perfect for beginners exploring JavaScript.'
  },
  {
    id: 'proj-virtual-classroom-platform',
    title: 'Virtual Classroom Platform',
    industry: 'EdTech',
    category: 'EdTech',
    description: 'Build a comprehensive online classroom with video conferencing, screen sharing, and collaborative whiteboard.',
    difficulty: 'Advanced',
    duration: '55h',
    skillsCount: 5,
    techStack: ['React', 'WebRTC', 'Node.js', 'Socket.io', 'Canvas API'],
    learnersCount: 2760,
    imageUrl: 'https://images.unsplash.com/photo-1588702547919-26089e690ecc?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Multi-participant video grid with active speaker spotlighting',
      'Low-latency collaborative digital whiteboard with real-time drawing sync',
      'In-class text messaging, hand-raising queue, and live emoji reactions',
      'Classroom attendance logs and downloadable lecture slide annotations'
    ],
    learningOutcomes: [
      'Handle mesh WebRTC peer connections with selective forwarding',
      'Synchronize collaborative vector drawing strokes over Socket.io',
      'Implement screen sharing capture via browser MediaStream APIs',
      'Structure accessible, high-performance multimedia React layouts'
    ],
    skillsLearned: ['React', 'WebRTC', 'Node.js', 'Socket.io', 'Canvas API'],
    prerequisites: 'Strong proficiency with React, Node.js, and WebSocket concepts.'
  },

  // Manufacturing (2 projects)
  {
    id: 'proj-inventory-management-system',
    title: 'Inventory Management System',
    industry: 'Manufacturing',
    category: 'Manufacturing',
    description: 'Create an inventory tracking system with barcode scanning, stock alerts, and automated reporting.',
    difficulty: 'Intermediate',
    duration: '40h',
    skillsCount: 5,
    techStack: ['Python', 'Flask', 'SQL', 'Barcode API', 'PDF Reports'],
    learnersCount: 2240,
    imageUrl: 'https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Warehouse SKU directory with location aisle tracking and stock thresholds',
      'Camera-based barcode and QR code scanner for quick stock intake',
      'Automated purchase order generation when inventory falls below minimums',
      'Exportable PDF consignment manifests and supplier shipment logs'
    ],
    learningOutcomes: [
      'Build relational database schemas with SQLite / PostgreSQL',
      'Integrate web-based barcode decoding APIs with camera feeds',
      'Generate dynamic PDF summary reports using Python libraries',
      'Implement audit trail logging for material transfers and shipments'
    ],
    skillsLearned: ['Python', 'Flask', 'SQL', 'Barcode API', 'PDF Reports'],
    prerequisites: 'Familiarity with Python syntax and basic relational database concepts.'
  },
  {
    id: 'proj-production-line-monitor',
    title: 'Production Line Monitor',
    industry: 'Manufacturing',
    category: 'Manufacturing',
    description: 'Build an intelligent production monitoring system with predictive maintenance and quality control.',
    difficulty: 'Advanced',
    duration: '50h',
    skillsCount: 5,
    techStack: ['IoT', 'Python', 'Machine Learning', 'TimescaleDB', 'Grafana'],
    learnersCount: 1870,
    imageUrl: 'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=800&q=80',
    whatYouWillBuild: [
      'Real-time conveyor belt sensor ingestion (temperature, vibration, RPM)',
      'Machine anomaly detection model flagging early bearing wear and tear',
      'Overall Equipment Effectiveness (OEE) metrics dashboard in real-time',
      'Emergency stoppage triggers and incident alert dispatching via SMS'
    ],
    learningOutcomes: [
      'Handle high-volume time-series database writes with TimescaleDB',
      'Deploy scikit-learn anomaly detection models on streaming machine data',
      'Create high-visibility plant supervisor visual control dashboards',
      'Integrate industrial alert webhooks for critical threshold breaches'
    ],
    skillsLearned: ['IoT', 'Python', 'Machine Learning', 'TimescaleDB', 'Grafana'],
    prerequisites: 'Knowledge of Python, basic machine learning, and time-series data.'
  }
];

export const JOB_OPPORTUNITIES: JobOpportunity[] = [
  {
    id: 'job-1',
    title: 'Junior Data Analyst',
    company: 'FinPulse Technologies',
    location: 'Bengaluru, India (Hybrid)',
    type: 'Hybrid',
    salary: '₹7.5 - 9.5 LPA',
    roleMatch: 'Data Analyst Track Graduates',
    postedDate: '2 days ago',
    tags: ['SQL', 'Tableau', 'Excel']
  },
  {
    id: 'job-2',
    title: 'Product UI/UX Designer',
    company: 'RazorLogix Systems',
    location: 'Remote',
    type: 'Remote',
    salary: '₹8 - 12 LPA',
    roleMatch: 'UI/UX Designer Track Graduates',
    postedDate: '1 day ago',
    tags: ['Figma', 'User Research', 'Design Systems']
  },
  {
    id: 'job-3',
    title: 'Security Operations Center (SOC) Analyst',
    company: 'CipherVault CyberSec',
    location: 'Hyderabad, India',
    type: 'Full-time',
    salary: '₹9 - 14 LPA',
    roleMatch: 'Cyber Security Analyst Graduates',
    postedDate: '3 days ago',
    tags: ['SIEM', 'Threat Analysis', 'Network Security']
  },
  {
    id: 'job-4',
    title: 'Full Stack Engineer (React + Node)',
    company: 'NexusCloud Labs',
    location: 'Bengaluru / Pune (Hybrid)',
    type: 'Hybrid',
    salary: '₹10 - 15 LPA',
    roleMatch: 'Full-Stack Developer Graduates',
    postedDate: 'Just now',
    tags: ['React', 'TypeScript', 'Node.js', 'PostgreSQL']
  }
];
