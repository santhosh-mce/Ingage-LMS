import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
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

// Dashboard & Analytics
export async function getDashboardOverview() {
  const res = await api.get('/admin/dashboard');
  return res.data;
}

export async function getDashboardAnalytics() {
  const res = await api.get('/admin/analytics');
  return res.data;
}

// User Management
export async function getAdminUsers(search?: string) {
  const params: Record<string, string> = {};
  if (search && search.trim()) params.search = search.trim();
  const res = await api.get('/admin/users', { params });
  return res.data;
}

export async function getAdminUserDetails(id: string) {
  const res = await api.get(`/admin/users/${id}`);
  return res.data;
}

export async function updateAdminUserStatus(id: string, active: boolean) {
  const res = await api.put(`/admin/users/${id}/status`, { active });
  return res.data;
}

export async function updateAdminUserRole(id: string, role: string) {
  const res = await api.put(`/admin/users/${id}/role`, { role });
  return res.data;
}

export async function deleteOrDeactivateAdminUser(id: string) {
  const res = await api.delete(`/admin/users/${id}`);
  return res.data;
}

// Course Management
export async function getAdminCourses(search?: string, status?: string) {
  const params: Record<string, string> = {};
  if (search && search.trim()) params.search = search.trim();
  if (status && status !== 'ALL') params.status = status;
  const res = await api.get('/admin/courses', { params });
  return res.data;
}

export async function getAdminCourseById(id: number | string) {
  const res = await api.get(`/admin/courses/${id}`);
  return res.data;
}

export async function createAdminCourse(payload: Record<string, any>) {
  const res = await api.post('/admin/courses', payload);
  return res.data;
}

export async function updateAdminCourse(id: number | string, payload: Record<string, any>) {
  const res = await api.put(`/admin/courses/${id}`, payload);
  return res.data;
}

export async function publishAdminCourse(id: number | string) {
  const res = await api.post(`/admin/courses/${id}/publish`);
  return res.data;
}

export async function unpublishAdminCourse(id: number | string) {
  const res = await api.post(`/admin/courses/${id}/unpublish`);
  return res.data;
}

export async function archiveAdminCourse(id: number | string) {
  const res = await api.post(`/admin/courses/${id}/archive`);
  return res.data;
}

export async function deleteAdminCourse(id: number | string) {
  const res = await api.delete(`/admin/courses/${id}`);
  return res.data;
}

// Sections & Lessons Builder
export async function addCourseSection(courseId: number | string, payload: { title: string; description?: string }) {
  const res = await api.post(`/admin/courses/${courseId}/sections`, payload);
  return res.data;
}

export async function updateCourseSection(sectionId: number | string, payload: { title: string; description?: string }) {
  const res = await api.put(`/admin/sections/${sectionId}`, payload);
  return res.data;
}

export async function deleteCourseSection(sectionId: number | string) {
  const res = await api.delete(`/admin/sections/${sectionId}`);
  return res.data;
}

export async function addCourseLesson(sectionId: number | string, payload: Record<string, any>) {
  const res = await api.post(`/admin/sections/${sectionId}/lessons`, payload);
  return res.data;
}

export async function updateCourseLesson(lessonId: number | string, payload: Record<string, any>) {
  const res = await api.put(`/admin/lessons/${lessonId}`, payload);
  return res.data;
}

export async function deleteCourseLesson(lessonId: number | string) {
  const res = await api.delete(`/admin/lessons/${lessonId}`);
  return res.data;
}

export async function getCourseAnalytics(courseId: number | string) {
  const res = await api.get(`/admin/courses/${courseId}/analytics`);
  return res.data;
}

export async function getCourseStudents(courseId: number | string) {
  const res = await api.get(`/admin/courses/${courseId}/students`);
  return res.data;
}

export async function getCourseCategories() {
  const res = await api.get('/admin/categories');
  return res.data;
}

export async function createCourseCategory(payload: { name: string; slug?: string; description?: string }) {
  const res = await api.post('/admin/categories', payload);
  return res.data;
}

// Media Upload
export async function uploadMediaFile(file: File, category: 'video' | 'thumbnail' | 'document') {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('category', category);

  const res = await api.post('/admin/media/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return res.data;
}

// Payments & Orders
export async function getAdminPayments() {
  const res = await api.get('/admin/payments');
  return res.data;
}

export async function getAdminOrders() {
  const res = await api.get('/admin/orders');
  return res.data;
}

// Discounts
export async function getAdminDiscounts() {
  const res = await api.get('/admin/discounts');
  return res.data;
}

export async function createAdminDiscount(payload: Record<string, any>) {
  const res = await api.post('/admin/discounts', payload);
  return res.data;
}

export async function updateAdminDiscount(id: number | string, payload: Record<string, any>) {
  const res = await api.put(`/admin/discounts/${id}`, payload);
  return res.data;
}

export async function toggleAdminDiscountStatus(id: number | string, active: boolean) {
  const res = await api.put(`/admin/discounts/${id}/status`, { active });
  return res.data;
}

export async function deleteAdminDiscount(id: number | string) {
  const res = await api.delete(`/admin/discounts/${id}`);
  return res.data;
}

export async function getDiscountAnalytics() {
  const res = await api.get('/admin/discounts/analytics');
  return res.data;
}

export async function validateCouponCode(couponCode: string, amount: number) {
  const res = await api.post('/discounts/validate', { couponCode, amount });
  return res.data;
}

// Course Progress
export async function getAdminProgress(search?: string, courseId?: number | string, status?: string) {
  const params: Record<string, string> = {};
  if (search && search.trim()) params.search = search.trim();
  if (courseId) params.courseId = courseId.toString();
  if (status && status !== 'ALL') params.status = status;
  const res = await api.get('/admin/progress', { params });
  return res.data;
}

export async function markLessonComplete(lessonId: number | string) {
  const res = await api.post('/progress/complete-lesson', { lessonId });
  return res.data;
}

// Certificates
export async function getAdminCertificates() {
  const res = await api.get('/admin/certificates');
  return res.data;
}

export function getCertificateDownloadUrl(certificateIdOrCode: number | string, isPublic = false) {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api';
  if (isPublic) {
    return `${baseUrl}/certificates/verify/${certificateIdOrCode}/download`;
  }
  return `${baseUrl}/admin/certificates/${certificateIdOrCode}/download`;
}

export async function verifyPublicCertificate(verificationCode: string) {
  const res = await api.get(`/certificates/verify/${verificationCode}`);
  return res.data;
}

// Careers Management
export async function getAdminCareers() {
  const res = await api.get('/admin/careers');
  return res.data;
}

export async function createAdminCareer(payload: Record<string, any>) {
  const res = await api.post('/admin/careers', payload);
  return res.data;
}

export async function updateAdminCareer(id: number | string, payload: Record<string, any>) {
  const res = await api.put(`/admin/careers/${id}`, payload);
  return res.data;
}

export async function deleteAdminCareer(id: number | string) {
  const res = await api.delete(`/admin/careers/${id}`);
  return res.data;
}

export async function assignCourseToCareer(careerId: number | string, courseId: number | string) {
  const res = await api.post(`/admin/careers/${careerId}/assign-course/${courseId}`);
  return res.data;
}

export async function removeCourseFromCareer(careerId: number | string, courseId: number | string) {
  const res = await api.delete(`/admin/careers/${careerId}/remove-course/${courseId}`);
  return res.data;
}

export async function toggleCareerPublish(id: number | string, published: boolean) {
  const res = await api.put(`/admin/careers/${id}/publish`, { published });
  return res.data;
}

export async function toggleCareerStatus(id: number | string, active: boolean) {
  const res = await api.put(`/admin/careers/${id}/status`, { active });
  return res.data;
}

// Project Management
export async function getAdminProjects() {
  const res = await api.get('/admin/projects');
  return res.data;
}

export async function createAdminProject(payload: Record<string, any>) {
  const res = await api.post('/admin/projects', payload);
  return res.data;
}

export async function updateAdminProject(id: number | string, payload: Record<string, any>) {
  const res = await api.put(`/admin/projects/${id}`, payload);
  return res.data;
}

export async function toggleProjectStatus(id: number | string, active: boolean) {
  const res = await api.put(`/admin/projects/${id}/status`, { active });
  return res.data;
}

export async function toggleProjectPublish(id: number | string, published: boolean) {
  const res = await api.put(`/admin/projects/${id}/publish`, { published });
  return res.data;
}

export async function deleteAdminProject(id: number | string) {
  const res = await api.delete(`/admin/projects/${id}`);
  return res.data;
}

// Activity Logs
export async function getAdminActivityLogs() {
  const res = await api.get('/admin/activity');
  return res.data;
}
