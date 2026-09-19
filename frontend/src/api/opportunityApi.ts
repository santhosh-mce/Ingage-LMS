import axios from 'axios';
import { INITIAL_OPPORTUNITIES } from '../data/opportunitiesData';

export type OpportunityType = 'Job' | 'Internship' | 'Freelance' | 'Apprenticeship';
export type WorkMode = 'Remote' | 'Hybrid' | 'On-site';
export type ApplicationStatus =
  | 'Applied'
  | 'Under Review'
  | 'Shortlisted'
  | 'Interview Scheduled'
  | 'Rejected'
  | 'Accepted';

export interface OpportunityItem {
  id: string;
  title: string;
  company: string;
  companyLogo?: string;
  location: string;
  type: OpportunityType;
  workMode: WorkMode;
  salary: string;
  experienceLevel: string;
  category: string;
  matchScore: number;
  requiredSkills: { name: string; matched?: boolean }[];
  description: string;
  aboutCompany: string;
  responsibilities: string[];
  qualifications: string[];
  benefits: string[];
  deadline?: string;
  postedDate?: string;
  roleTrackId?: string;
}

export interface ApplicationItem {
  id: string;
  opportunityId: string;
  appliedAt: string;
  status: ApplicationStatus;
  opportunityTitle: string;
  companyName: string;
  companyLogo?: string;
  type: OpportunityType;
  location: string;
  salary: string;
}

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ingage_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Seed data enriched with all 4 opportunity types (Job, Internship, Freelance, Apprenticeship)
export const DEFAULT_OPPORTUNITIES: OpportunityItem[] = [
  {
    id: 'opp-1',
    title: 'Data Analyst Intern',
    company: 'DataCorp Analytics',
    companyLogo: 'https://images.unsplash.com/photo-1549923746-c502d488b3ea?q=80&w=120&auto=format&fit=crop',
    location: 'Remote',
    type: 'Internship',
    workMode: 'Remote',
    salary: '₹25,000 - ₹35,000 / month',
    experienceLevel: 'Entry Level',
    category: 'Data & Analytics',
    matchScore: 92,
    postedDate: '2 days ago',
    deadline: 'In 2 weeks',
    requiredSkills: [
      { name: 'SQL', matched: true },
      { name: 'Excel', matched: true },
      { name: 'Python', matched: true },
      { name: 'Power BI', matched: false }
    ],
    description: 'DataCorp Analytics is seeking a highly motivated Data Analyst Intern to join our business intelligence unit. You will work directly with our engineering and product teams to transform raw event data into actionable operational insights.',
    aboutCompany: 'DataCorp Analytics is a leading data intelligence firm supporting Fortune 500 enterprises with real-time reporting, customer analytics, and ETL pipeline management.',
    responsibilities: [
      'Extract and transform transactional records using PostgreSQL and MySQL queries',
      'Maintain weekly business performance dashboards in Excel and automated Python scripts',
      'Collaborate with product managers to define tracking KPIs for user retention',
      'Assist senior analysts in synthesizing A/B test results into executive summaries'
    ],
    qualifications: [
      'Proficiency in SQL querying (joins, aggregations, CTEs, window functions)',
      'Working knowledge of Python for data manipulation (pandas, numpy)',
      'Advanced spreadsheet modeling skills in MS Excel or Google Sheets',
      'Strong communication and data storytelling aptitude'
    ],
    benefits: [
      'Mentorship from Senior Data Principals',
      'Certificate of Internship Completion & Letter of Recommendation',
      'Full-time PPO conversion based on performance',
      'Flexible remote work hours'
    ],
    roleTrackId: 'data-analyst'
  },
  {
    id: 'opp-2',
    title: 'Junior Full Stack Developer',
    company: 'Tech Solutions Inc.',
    companyLogo: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?q=80&w=120&auto=format&fit=crop',
    location: 'Bangalore, India',
    type: 'Job',
    workMode: 'Hybrid',
    salary: '₹7 - ₹9 LPA',
    experienceLevel: '0 - 2 Years',
    category: 'Software Engineering',
    matchScore: 88,
    postedDate: '3 days ago',
    deadline: 'In 3 weeks',
    requiredSkills: [
      { name: 'React', matched: true },
      { name: 'TypeScript', matched: true },
      { name: 'Node.js', matched: true },
      { name: 'Tailwind CSS', matched: true }
    ],
    description: 'Tech Solutions Inc. is seeking a talented Junior Full Stack Developer to build modern web applications and customer-facing dashboards using React, TypeScript, and Node.js.',
    aboutCompany: 'Tech Solutions Inc. powers cloud software solutions for thousands of global SaaS companies with cutting-edge telemetry and developer tooling.',
    responsibilities: [
      'Develop dynamic, responsive user interfaces using React and modern CSS frameworks',
      'Integrate RESTful microservices and PostgreSQL databases with high reliability',
      'Write automated unit and integration tests to ensure robust deployment pipeline',
      'Participate in code reviews and collaborate closely with UX designers'
    ],
    qualifications: [
      'Solid command of modern JavaScript (ES6+), TypeScript, and React hooks',
      'Understanding of client-server architecture, HTTP protocols, and REST API design',
      'Familiarity with Git workflow, CI/CD pipelines, and cloud deployment basics',
      'Passionate about clean code and delightful user experiences'
    ],
    benefits: [
      'Comprehensive Health & Wellness Insurance',
      'Annual Learning & Certification Allowance (₹50,000/yr)',
      'Hybrid office model with modern campus in Indiranagar, Bangalore',
      'Competitive equity stock options'
    ],
    roleTrackId: 'full-stack-developer'
  },
  {
    id: 'opp-3',
    title: 'React Native Mobile App Specialist',
    company: 'FinFlow Technologies',
    companyLogo: 'https://images.unsplash.com/photo-1551836022-d5d88e9218df?q=80&w=120&auto=format&fit=crop',
    location: 'Remote',
    type: 'Freelance',
    workMode: 'Remote',
    salary: '₹60,000 - ₹90,000 / project',
    experienceLevel: 'Intermediate',
    category: 'Mobile Development',
    matchScore: 82,
    postedDate: 'Just now',
    deadline: 'In 1 week',
    requiredSkills: [
      { name: 'React Native', matched: true },
      { name: 'Redux Toolkit', matched: true },
      { name: 'iOS/Android', matched: false },
      { name: 'REST APIs', matched: true }
    ],
    description: 'FinFlow is looking for a freelance mobile app engineer to implement feature updates for our cross-platform personal finance mobile app built on React Native.',
    aboutCompany: 'FinFlow Technologies builds intuitive micro-savings and automated budget tracking apps used by over 200,000 young professionals across India.',
    responsibilities: [
      'Implement newly designed onboarding screens and banking transaction widgets',
      'Optimize component rendering speed and memory usage on low-spec Android devices',
      'Integrate secure biometric authentication and notification push listeners'
    ],
    qualifications: [
      'Prior published apps on Google Play Store or Apple App Store',
      'Proficiency in React Native and state management with Redux Toolkit',
      'Strong eye for smooth animations and fluid mobile UI interactions'
    ],
    benefits: [
      '100% remote asynchronous workflow with flexible hours',
      'Milestone-based prompt weekly payouts',
      'Long-term contract extension possibilities'
    ],
    roleTrackId: 'mobile-developer'
  },
  {
    id: 'opp-4',
    title: 'Cloud & DevOps Apprentice',
    company: 'InfraScale Cloud Systems',
    companyLogo: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?q=80&w=120&auto=format&fit=crop',
    location: 'Hyderabad, India',
    type: 'Apprenticeship',
    workMode: 'On-site',
    salary: '₹20,000 / month + Full Tuition',
    experienceLevel: 'Fresher / Trainee',
    category: 'Cloud & DevOps',
    matchScore: 75,
    postedDate: '5 days ago',
    deadline: 'In 4 weeks',
    requiredSkills: [
      { name: 'Linux', matched: true },
      { name: 'Docker', matched: false },
      { name: 'AWS Basics', matched: false },
      { name: 'Networking', matched: true }
    ],
    description: 'Kickstart your cloud engineering career through our structured 12-month Apprenticeship program. You will receive certified AWS training while assisting our site reliability engineering squad.',
    aboutCompany: 'InfraScale delivers multi-cloud infrastructure automation, Kubernetes orchestration, and 24/7 reliability engineering for enterprise clients.',
    responsibilities: [
      'Learn and shadow senior SREs in managing Docker container clusters on AWS',
      'Automate system monitoring scripts and configure Prometheus / Grafana alerts',
      'Participate in disaster recovery drills and incident response walkthroughs'
    ],
    qualifications: [
      'Basic knowledge of Linux terminal commands and shell scripting',
      'Foundational understanding of TCP/IP, DNS, and HTTP networking concepts',
      'Eagerness to learn cloud architectures and achieve AWS certifications'
    ],
    benefits: [
      'Sponsored AWS Certified Solutions Architect exam fee',
      'Dedicated industry mentor throughout the 12-month program',
      'Guaranteed interview for full-time Cloud Engineer role upon graduation'
    ],
    roleTrackId: 'devops-engineer'
  },
  {
    id: 'opp-5',
    title: 'UI/UX Design Intern',
    company: 'PixelCraft Creative Studio',
    companyLogo: 'https://images.unsplash.com/photo-1572021335469-31706a17aaef?q=80&w=120&auto=format&fit=crop',
    location: 'Mumbai, India',
    type: 'Internship',
    workMode: 'Hybrid',
    salary: '₹20,000 - ₹28,000 / month',
    experienceLevel: 'Entry Level',
    category: 'Design & Creative',
    matchScore: 84,
    postedDate: '1 week ago',
    deadline: 'In 10 days',
    requiredSkills: [
      { name: 'Figma', matched: true },
      { name: 'Wireframing', matched: true },
      { name: 'User Research', matched: true },
      { name: 'Design Systems', matched: false }
    ],
    description: 'PixelCraft is looking for an imaginative UI/UX design intern to collaborate on enterprise SaaS design systems and consumer mobile experiences.',
    aboutCompany: 'PixelCraft is an award-winning digital experience agency that crafts brand identities, UI design systems, and web apps for hyper-growth startups.',
    responsibilities: [
      'Create high-fidelity wireframes, interactive prototypes, and vector icons in Figma',
      'Conduct usability interviews and synthesize user feedback into iterative mockups',
      'Collaborate with front-end developers to ensure pixel-perfect design implementation'
    ],
    qualifications: [
      'A portfolio showing 2+ UX case studies or interactive Figma prototypes',
      'Understanding of visual hierarchy, accessibility (WCAG), and responsive typography',
      'Great collaborative attitude and openness to constructive critique'
    ],
    benefits: [
      'Work on global client projects across FinTech, HealthTech, and EdTech',
      'Direct 1-on-1 feedback sessions from Design Directors',
      'Fast-track PPO offer'
    ],
    roleTrackId: 'ui-ux-designer'
  },
  {
    id: 'opp-6',
    title: 'Frontend Web Developer (React)',
    company: 'Apex Digital Labs',
    companyLogo: 'https://images.unsplash.com/photo-1568602471122-7832951cc4c5?q=80&w=120&auto=format&fit=crop',
    location: 'Pune, India',
    type: 'Job',
    workMode: 'Hybrid',
    salary: '₹8 - ₹12 LPA',
    experienceLevel: '1 - 3 Years',
    category: 'Software Engineering',
    matchScore: 90,
    postedDate: '4 days ago',
    deadline: 'In 2 weeks',
    requiredSkills: [
      { name: 'React', matched: true },
      { name: 'TypeScript', matched: true },
      { name: 'Tailwind CSS', matched: true },
      { name: 'Redux Toolkit', matched: true }
    ],
    description: 'Apex Digital Labs is expanding our core engineering team. We are looking for an ambitious React Frontend Developer to architect high-performance web experiences.',
    aboutCompany: 'Apex Digital Labs provides enterprise digital transformation solutions, powering customer portals and e-commerce engines for international brands.',
    responsibilities: [
      'Build scalable, modular React components using clean architecture patterns',
      'Optimize web vitals (LCP, FID, CLS) across desktop and mobile devices',
      'Collaborate with backend engineers to integrate GraphQL and REST APIs'
    ],
    qualifications: [
      'Proven expertise in React 18+, TypeScript, and Tailwind CSS',
      'Experience managing complex global state with Redux Toolkit or Zustand',
      'Knowledge of accessibility and cross-browser compatibility standards'
    ],
    benefits: [
      'Health insurance covering employee, spouse, and parents',
      'Flexible work hours and generous paid time off',
      'Annual technology allowance for home office setup'
    ],
    roleTrackId: 'frontend-developer'
  }
];

// Helper to get cached items or fallback
const STORAGE_KEY_APPLICATIONS = 'ingage_learner_applications';
const STORAGE_KEY_SAVED = 'ingage_saved_opportunities';

export function getSavedOpportunityIds(): string[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY_SAVED);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function saveOpportunityId(id: string): string[] {
  try {
    const current = getSavedOpportunityIds();
    if (!current.includes(id)) {
      const updated = [...current, id];
      localStorage.setItem(STORAGE_KEY_SAVED, JSON.stringify(updated));
      return updated;
    }
    return current;
  } catch {
    return [id];
  }
}

export function removeSavedOpportunityId(id: string): string[] {
  try {
    const current = getSavedOpportunityIds();
    const updated = current.filter((item) => item !== id);
    localStorage.setItem(STORAGE_KEY_SAVED, JSON.stringify(updated));
    return updated;
  } catch {
    return [];
  }
}

export function getStoredApplications(): ApplicationItem[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY_APPLICATIONS);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function storeApplication(app: ApplicationItem): ApplicationItem[] {
  try {
    const current = getStoredApplications();
    // Check duplicate
    const exists = current.some((a) => a.opportunityId === app.opportunityId);
    if (!exists) {
      const updated = [app, ...current];
      localStorage.setItem(STORAGE_KEY_APPLICATIONS, JSON.stringify(updated));
      return updated;
    }
    return current;
  } catch {
    return [app];
  }
}

/**
 * Fetch opportunities:
 * Tries GET /opportunities or GET /api/opportunities.
 * If empty or fails, returns rich DEFAULT_OPPORTUNITIES.
 */
export async function getOpportunitiesApi(): Promise<OpportunityItem[]> {
  try {
    const response = await apiClient.get<any[]>('/opportunities');
    if (response.data && Array.isArray(response.data) && response.data.length > 0) {
      // Map backend fields to frontend OpportunityItem
      return response.data.map((item: any, idx: number) => ({
        id: String(item.id || `opp-be-${idx}`),
        title: item.title || 'Career Opportunity',
        company: item.company || 'Partner Company',
        companyLogo: item.companyLogo || DEFAULT_OPPORTUNITIES[idx % DEFAULT_OPPORTUNITIES.length].companyLogo,
        location: item.location || 'Remote',
        type: (item.type as OpportunityType) || 'Job',
        workMode: (item.workMode as WorkMode) || (item.location?.toLowerCase().includes('remote') ? 'Remote' : 'Hybrid'),
        salary: item.salary || 'Competitive',
        experienceLevel: item.experienceLevel || 'Entry to Mid',
        category: item.category || 'Technology',
        matchScore: item.matchScore || 80,
        requiredSkills: item.requiredSkills || [{ name: 'Problem Solving', matched: true }],
        description: item.description || 'Exciting opportunity to build your career with our growing team.',
        aboutCompany: item.aboutCompany || 'Leading technology enterprise.',
        responsibilities: item.responsibilities || ['Contribute to agile team goals'],
        qualifications: item.qualifications || ['Relevant educational background or equivalent experience'],
        benefits: item.benefits || ['Competitive pay', 'Growth opportunities'],
        postedDate: item.postedDate || 'Recent',
        deadline: item.deadline || 'Open until filled'
      }));
    }
    return DEFAULT_OPPORTUNITIES;
  } catch {
    // Graceful fallback to default enriched opportunities
    return DEFAULT_OPPORTUNITIES;
  }
}

/**
 * Submit an application for an opportunity.
 * First tries POST /applications or POST /opportunities/{id}/apply.
 * Falls back to local storage record to guarantee persistence.
 */
export async function applyToOpportunityApi(
  opportunity: OpportunityItem,
  notes?: string
): Promise<ApplicationItem> {
  const newApp: ApplicationItem = {
    id: `app-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
    opportunityId: opportunity.id,
    appliedAt: new Date().toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    }),
    status: 'Under Review',
    opportunityTitle: opportunity.title,
    companyName: opportunity.company,
    companyLogo: opportunity.companyLogo,
    type: opportunity.type,
    location: opportunity.location,
    salary: opportunity.salary
  };

  try {
    // Attempt backend POST call
    await apiClient.post('/applications', {
      opportunityId: opportunity.id,
      notes: notes || '',
    });
  } catch {
    // Backend endpoint might not be wired yet; client-side persists in storage
  }

  storeApplication(newApp);
  return newApp;
}
