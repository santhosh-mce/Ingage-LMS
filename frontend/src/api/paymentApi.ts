import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
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

export interface PaymentOrderResponse {
  orderId: number;
  orderNumber: string;
  razorpayOrderId: string;
  amount: number;
  amountInPaise: number;
  currency: string;
  keyId: string;
  courseTitle: string;
  courseId: number;
  free?: boolean;
  message?: string;
}

export interface PaymentVerifyRequest {
  razorpay_payment_id: string;
  razorpay_order_id: string;
  razorpay_signature: string;
  courseId?: number;
}

export interface PaymentVerifyResponse {
  success: boolean;
  message: string;
  orderNumber: string;
  paymentNumber: string;
  courseId: number;
  courseTitle?: string;
}

export interface UserPaymentRecord {
  id: number;
  paymentNumber: string;
  orderNumber: string;
  razorpayPaymentId: string;
  courseId: number;
  courseName: string;
  amount: number;
  discount: number;
  finalAmount: number;
  currency: string;
  paymentStatus: 'PAID' | 'PENDING' | 'FAILED' | 'REFUNDED';
  paymentDate: string;
}

export interface UserEnrollmentRecord {
  id: number;
  courseId: number;
  courseTitle: string;
  courseSlug?: string;
  thumbnail?: string;
  category: string;
  level: string;
  duration: string;
  instructor: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  progressPercentage: number;
  enrolledAt: string;
  lastAccessedAt?: string;
}

export type EnrollmentItem = UserEnrollmentRecord;

export interface CourseContentDetail {
  id: number;
  title: string;
  slug?: string;
  description: string;
  category: string;
  level: string;
  duration: string;
  instructor?: string;
  price: number;
  finalPrice?: number;
  isEnrolled: boolean;
  sections: Array<{
    id: number;
    title: string;
    description?: string;
    displayOrder: number;
    lessons: Array<{
      id: number;
      title: string;
      description?: string;
      lessonType: string;
      contentUrl?: string | null;
      duration?: string;
      freePreview: boolean;
      locked: boolean;
      required: boolean;
      displayOrder: number;
    }>;
  }>;
}

// 1. Create Razorpay Payment Order (or free course enrollment)
export const createPaymentOrder = async (
  courseId: number,
  couponCode?: string
): Promise<PaymentOrderResponse> => {
  const res = await apiClient.post<PaymentOrderResponse>('/payments/create-order', {
    courseId,
    couponCode: couponCode ? couponCode.trim() : undefined,
  });
  return res.data;
};

// 2. Verify Razorpay Payment Signature
export const verifyPayment = async (
  payload: PaymentVerifyRequest
): Promise<PaymentVerifyResponse> => {
  const res = await apiClient.post<PaymentVerifyResponse>('/payments/verify', payload);
  return res.data;
};

// 3. Validate Discount Coupon
export const validateCoupon = async (
  couponCode: string,
  amount: number
): Promise<{
  valid: boolean;
  couponCode?: string;
  discountType?: string;
  discountValue?: number;
  discountAmount?: number;
  finalAmount?: number;
  message?: string;
}> => {
  const res = await apiClient.post('/discounts/validate', {
    couponCode,
    amount,
  });
  return res.data;
};

// 4. Get Logged-in User's Payments
export const getMyPayments = async (): Promise<UserPaymentRecord[]> => {
  const res = await apiClient.get<UserPaymentRecord[]>('/payments/my-payments');
  return res.data;
};

// 5. Get Logged-in User's Active Enrollments
export const getMyEnrollments = async (): Promise<UserEnrollmentRecord[]> => {
  const res = await apiClient.get<UserEnrollmentRecord[]>('/learning/my-enrollments');
  return res.data;
};

// 6. Get Course Curriculum & Content (Enforces secure access)
export const getCourseContent = async (courseId: number | string): Promise<CourseContentDetail> => {
  const res = await apiClient.get<CourseContentDetail>(`/courses/${courseId}/content`);
  return res.data;
};
