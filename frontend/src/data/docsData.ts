export interface HandoffDocItem {
  id: string;
  title: string;
  description: string;
  category: 'design' | 'developer';
  icon: string;
  route: string;
  badge?: string;
}

export const DESIGN_HANDOFF_DOCS: HandoffDocItem[] = [
  {
    id: 'design-tokens',
    title: 'Color Palette & Design Tokens',
    description: 'Ingage Lime (#78a800, #84cc16), neutral slates, typography scales (Inter & Plus Jakarta Sans), and radii rules.',
    category: 'design',
    icon: 'Palette',
    route: '/design-handoff'
  },
  {
    id: 'typography-system',
    title: 'Typography & Layout Rhythm',
    description: 'Major Second scale (1.125), line-height standards (1.5-1.7), container padding math and optical alignments.',
    category: 'design',
    icon: 'FileText',
    route: '/design-handoff'
  },
  {
    id: 'component-library',
    title: 'Core UI Component Anatomy',
    description: 'Job-role cards, gated sequential step badges, status pills, module accordions, and interactive video player.',
    category: 'design',
    icon: 'Grid',
    route: '/design-handoff'
  },
  {
    id: 'responsive-breakpoints',
    title: 'Responsive Grid & Breakpoints',
    description: 'Mobile (320px-480px), Tablet (768px-1024px), Desktop (1280px-1536px) fluid grid layouts with 44px min touch targets.',
    category: 'design',
    icon: 'Laptop',
    route: '/design-handoff'
  },
  {
    id: 'accessibility-specs',
    title: 'WCAG 2.1 AA Accessibility Guidelines',
    description: '4.5:1 text contrast ratios, visible keyboard focus rings, screen reader aria-labels, and semantic HTML milestones.',
    category: 'design',
    icon: 'Shield',
    route: '/design-handoff'
  },
  {
    id: 'motion-interactions',
    title: 'Motion & Micro-interactions',
    description: 'Framer Motion spring physics for search autocomplete, drawer slides, module unlock celebratory states.',
    category: 'design',
    icon: 'Sparkles',
    route: '/design-handoff'
  }
];

export const DEVELOPER_HANDOFF_DOCS: HandoffDocItem[] = [
  {
    id: 'business-rules-logic',
    title: 'Business Rules & Logic',
    description: 'Sequential gating criteria, 70% passing threshold, prerequisite enforcement, and certificate issuance logic.',
    category: 'developer',
    icon: 'FileText',
    route: '/business-rules-logic',
    badge: 'Core Engine'
  },
  {
    id: 'feature-flags-config',
    title: 'Feature Flags & Configuration Guide',
    description: '20+ feature flags catalog, ENABLE_PROJECT_TRACK, gradual rollouts, kill switches, and environment configurations.',
    category: 'developer',
    icon: 'ToggleLeft',
    route: '/feature-flags-config',
    badge: 'Operations'
  },
  {
    id: 'error-handling-edge-cases',
    title: 'Error Handling & Edge Case Scenarios',
    description: '30+ edge cases, 408/429/500 API recovery, quiz boundary (69% vs 70%), multi-tab synchronization, and error boundaries.',
    category: 'developer',
    icon: 'Bug',
    route: '/error-handling-edge-cases',
    badge: 'Resilience'
  },
  {
    id: 'performance-optimization',
    title: 'Performance & Optimization Guidelines',
    description: 'LCP/FID/CLS targets, code splitting, HLS adaptive video bitrate streaming, Redis caching, and memoization.',
    category: 'developer',
    icon: 'Zap',
    route: '/performance-optimization',
    badge: '<3s Target'
  },
  {
    id: 'security-access-control',
    title: 'Security & Access Control Rules',
    description: 'JWT 15-min access tokens, OAuth 2.0 social login, RBAC permission matrix (5 roles), bcrypt cost 12, and GDPR.',
    category: 'developer',
    icon: 'Shield',
    route: '/security-access-control',
    badge: 'Enterprise'
  },
  {
    id: 'qa-checklist-acceptance',
    title: 'QA Checklist & Acceptance Criteria',
    description: '80%+ unit test coverage, E2E Playwright tests, Gherkin BDD templates, P0-P3 bug matrix, and 16-point checklist.',
    category: 'developer',
    icon: 'CheckSquare',
    route: '/qa-checklist-acceptance',
    badge: 'Production-Ready'
  },
  {
    id: 'screen-to-route-mapping',
    title: 'Screen-to-Route Mapping',
    description: 'All 43 application screens mapped to exact client URLs, parameters, auth protection levels, and role permissions.',
    category: 'developer',
    icon: 'Grid',
    route: '/screen-to-route-mapping',
    badge: 'Architecture'
  },
  {
    id: 'state-data-dependency',
    title: 'State & Data Dependency Matrix',
    description: 'Zustand state slices, reactive dependencies, optimistic updates, and cache invalidation strategies.',
    category: 'developer',
    icon: 'Database',
    route: '/state-data-dependency',
    badge: 'Data Flow'
  }
];
