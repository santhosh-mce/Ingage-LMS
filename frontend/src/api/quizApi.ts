import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('ingage_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface QuizQuestion {
  id: number;
  lessonId: number;
  questionText: string;
  options: string[];
  displayOrder: number;
}

export interface QuizSubmitRequest {
  questionId: number;
  selectedOptionIndex: number;
}

export interface QuizSubmitResponse {
  correct: boolean;
  explanation: string;
  correctOptionIndex?: number;
  quizCompleted: boolean;
  totalQuestions: number;
  answeredQuestions: number;
  message?: string;
}

export const getLessonQuiz = async (courseId: number, lessonId: number): Promise<QuizQuestion[]> => {
  const response = await api.get<QuizQuestion[]>(`/courses/${courseId}/lessons/${lessonId}/quiz`);
  return response.data;
};

export const submitQuizAnswer = async (
  courseId: number,
  lessonId: number,
  payload: QuizSubmitRequest
): Promise<QuizSubmitResponse> => {
  const response = await api.post<QuizSubmitResponse>(
    `/courses/${courseId}/lessons/${lessonId}/quiz/submit`,
    payload
  );
  return response.data;
};
