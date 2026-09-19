import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

export interface CareerExploreItem {
  id: number;
  title: string;
  slug: string;
  category: string;
  trending?: boolean;
  icon?: string;
}

export interface ProjectExploreItem {
  id: number;
  title: string;
  slug: string;
  category: string;
  industry: string;
}

export interface ExploreData {
  careers: CareerExploreItem[];
  projects: ProjectExploreItem[];
}

export async function getExploreData(): Promise<ExploreData> {
  const res = await api.get<ExploreData>('/explore');
  return res.data;
}

export async function getPublicProjects(industry?: string) {
  const params: Record<string, string> = {};
  if (industry && industry !== 'All') params.industry = industry;
  const res = await api.get('/projects', { params });
  return res.data;
}

export async function getPublicProjectBySlug(slug: string) {
  const res = await api.get(`/projects/${slug}`);
  return res.data;
}
