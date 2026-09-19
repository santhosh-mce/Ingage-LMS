import axios from 'axios';

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface RequestLoginOtpRequest {
  email: string;
}

export interface VerifyLoginOtpRequest {
  email: string;
  otp: string;
}

export interface AuthResponse {
  message: string;
  token?: string | null;
  userId?: string;
  name?: string;
  email?: string;
  role?: string;
  otp?: string;
}

export interface CurrentUserResponse {
  message: string;
  userId: string;
  name: string;
  email: string;
  role: string;
  profileImage?: string;
  avatar?: string;
  avatarUrl?: string;
}

interface ApiErrorResponse {
  error?: string;
  message?: string;
}

const authClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

authClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ingage_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export async function registerUser(payload: RegisterRequest): Promise<AuthResponse> {
  const response = await authClient.post<AuthResponse>('/auth/register', payload);
  return response.data;
}

export async function loginUser(payload: LoginRequest): Promise<AuthResponse> {
  const response = await authClient.post<AuthResponse>('/auth/login', payload);
  return response.data;
}

export async function requestLoginOtp(email: string): Promise<{ message: string; email: string; otp?: string }> {
  const response = await authClient.post<{ message: string; email: string; otp?: string }>('/auth/login/request-otp', { email });
  return response.data;
}

export async function verifyLoginOtp(payload: VerifyLoginOtpRequest): Promise<AuthResponse> {
  const response = await authClient.post<AuthResponse>('/auth/login/verify-otp', payload);
  return response.data;
}

export async function getCurrentUser(): Promise<CurrentUserResponse> {
  const response = await authClient.get<CurrentUserResponse>('/users/me');
  return response.data;
}

export async function logoutUser(): Promise<void> {
  await authClient.post('/auth/logout');
}

export interface UpdateProfileRequest {
  name: string;
  phone?: string;
}

export async function updateUserProfileApi(payload: UpdateProfileRequest): Promise<CurrentUserResponse> {
  const response = await authClient.put<CurrentUserResponse>('/users/me', payload);
  return response.data;
}

export interface ProfileImageResponse {
  success: boolean;
  message: string;
  profileImage: string;
  avatarUrl?: string;
  user?: CurrentUserResponse;
}

export async function uploadProfileImageApi(file: File): Promise<ProfileImageResponse> {
  const formData = new FormData();
  formData.append('image', file);
  formData.append('file', file);
  const response = await authClient.post<ProfileImageResponse>('/users/profile/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
}

export async function removeProfileImageApi(): Promise<{ success: boolean; message: string }> {
  const response = await authClient.delete<{ success: boolean; message: string }>('/users/profile/image');
  return response.data;
}

export function getAuthErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error || error.response?.data?.message || fallback;
  }
  return fallback;
}

/**
 * Resolves any avatar / profile image path to a fully qualified, accessible URL.
 * Handles relative paths, missing /api context prefix, and localhost port mapping.
 */
export function getAccessibleImageUrl(path?: string | null): string {
  if (!path || typeof path !== 'string' || !path.trim()) return '';
  const clean = path.trim();

  // If already absolute with protocol
  if (clean.startsWith('http://') || clean.startsWith('https://') || clean.startsWith('data:') || clean.startsWith('blob:')) {
    try {
      const url = new URL(clean);
      // Fix cases where backend port 8000 URL was created with /uploads instead of /api/uploads
      if ((url.port === '8000' || url.hostname === 'localhost') && url.pathname.startsWith('/uploads/')) {
        url.pathname = '/api' + url.pathname;
        return url.toString();
      }
    } catch {
      // ignore
    }
    return clean;
  }

  // Base API configuration (e.g., http://localhost:8000/api)
  const apiBase = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api';
  const backendOrigin = apiBase.replace(/\/api\/?$/, '');

  if (clean.startsWith('/api/uploads/')) {
    return `${backendOrigin}${clean}`;
  }
  if (clean.startsWith('/uploads/')) {
    return `${backendOrigin}/api${clean}`;
  }
  if (clean.startsWith('/api/')) {
    return `${backendOrigin}${clean}`;
  }
  if (clean.startsWith('/')) {
    return `${backendOrigin}/api${clean}`;
  }

  // Pure filename: e.g. user-07fc075e-8d46b1fe7eef443f8ea6a1ab1fc7ceaf.jpg
  return `${backendOrigin}/api/uploads/profile-images/${clean}`;
}
