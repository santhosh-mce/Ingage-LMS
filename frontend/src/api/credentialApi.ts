import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('ingage_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface CredentialModuleDto {
  id?: number;
  title: string;
  description: string;
  duration: string;
  orderIndex?: number;
}

export interface CredentialCourseDto {
  id: number;
  title: string;
  slug: string;
  provider: string;
  category: string;
  level: string;
  duration: string;
  description: string;
  shortDescription?: string;
  thumbnail?: string;
  credentialName?: string;
  credentialType?: string;
  credentialUrl?: string;
  price?: number;
  discount?: number;
  free: boolean;
  published: boolean;
  featured: boolean;
  rating?: number;
  learnersCount?: number;
  careerSlug?: string;
  learningOutcomes?: string[];
  prerequisites?: string[];
  modules?: CredentialModuleDto[];
  createdAt?: string;
  updatedAt?: string;

  // Contextual user status
  enrolled?: boolean;
  progressPercentage?: number;
  userStatus?: 'NOT_ENROLLED' | 'IN_PROGRESS' | 'COMPLETED' | 'CREDENTIAL_EARNED' | 'CREDENTIAL_PENDING';
  completedModulesCount?: number;
  credentialId?: string;
}

export interface UserCredentialDto {
  enrollmentId: number;
  courseId: number;
  courseTitle: string;
  courseSlug: string;
  courseThumbnail?: string;
  category: string;
  level: string;
  provider: string;
  credentialName: string;
  credentialType: string;
  credentialUrl?: string;
  status: 'IN_PROGRESS' | 'COMPLETED' | 'CREDENTIAL_EARNED' | 'CREDENTIAL_PENDING';
  progressPercentage: number;
  completedModulesCount: number;
  totalModulesCount: number;
  credentialId?: string;
  enrolledAt: string;
  completedAt?: string;
}

export interface CategoryCountItem {
  category: string;
  count: number;
}

export async function getCredentialCourses(params?: {
  category?: string;
  level?: string;
  search?: string;
  sort?: string;
}): Promise<CredentialCourseDto[]> {
  const res = await api.get('/credential-courses', { params });
  return res.data;
}

export async function getCredentialCourseBySlug(slug: string): Promise<CredentialCourseDto> {
  const res = await api.get(`/credential-courses/${slug}`);
  return res.data;
}

export async function getCredentialCategories(): Promise<CategoryCountItem[]> {
  const res = await api.get('/credential-courses/categories');
  return res.data;
}

export async function getFeaturedCredentialCourses(): Promise<CredentialCourseDto[]> {
  const res = await api.get('/credential-courses/featured');
  return res.data;
}

export async function searchCredentialCourses(q: string): Promise<CredentialCourseDto[]> {
  const res = await api.get('/credential-courses/search', { params: { q } });
  return res.data;
}

export async function getCredentialCoursesByCareer(careerSlug: string): Promise<CredentialCourseDto[]> {
  const res = await api.get(`/credential-courses/career/${careerSlug}`);
  return res.data;
}

export async function enrollInCredentialCourse(slug: string): Promise<CredentialCourseDto> {
  const res = await api.post(`/credential-courses/${slug}/enroll`);
  return res.data;
}

export async function updateCredentialCourseProgress(slug: string, completedModules?: number): Promise<CredentialCourseDto> {
  const res = await api.post(`/credential-courses/${slug}/progress`, { completedModules });
  return res.data;
}

export async function getMyCredentials(): Promise<UserCredentialDto[]> {
  const res = await api.get('/my/credentials');
  return res.data;
}

// Admin API
export async function getAdminCredentialCourses(): Promise<CredentialCourseDto[]> {
  const res = await api.get('/admin/credential-courses');
  return res.data;
}

export async function createAdminCredentialCourse(payload: Partial<CredentialCourseDto>): Promise<CredentialCourseDto> {
  const res = await api.post('/admin/credential-courses', payload);
  return res.data;
}

export async function updateAdminCredentialCourse(id: number, payload: Partial<CredentialCourseDto>): Promise<CredentialCourseDto> {
  const res = await api.put(`/admin/credential-courses/${id}`, payload);
  return res.data;
}

export async function deleteAdminCredentialCourse(id: number): Promise<{ message: string; id: string }> {
  const res = await api.delete(`/admin/credential-courses/${id}`);
  return res.data;
}

export async function togglePublishCredentialCourse(id: number): Promise<CredentialCourseDto> {
  const res = await api.patch(`/admin/credential-courses/${id}/publish`);
  return res.data;
}
