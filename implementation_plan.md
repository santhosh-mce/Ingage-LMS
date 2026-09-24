# Dynamic Course Purchase & Enrollment Verification on Career Path Pages

Implement real-time backend enrollment, payment, and order verification for recommended courses on Career Path detail pages (`/roles/{slug}`), supporting all career paths and courses dynamically.

## User Review Required

> [!IMPORTANT]
> - **Security Single Source of Truth**: The client will never determine or supply enrollment, access, or payment status. The Spring Boot backend checks the authenticated JWT user against PostgreSQL `enrollments`, `orders`, and `payments` tables.
> - **No Hardcoding**: Works dynamically for all 20+ careers and all recommended courses configured in PostgreSQL.
> - **Direct Checkout on Career Page**: Users can click "Buy Now" directly on any recommended course card in `/roles/{slug}`, complete the Razorpay checkout, verify payment on the backend, and have the card immediately transition to "Start Learning".

---

## Proposed Changes

### 1. Backend Payment & Enrollment Verification Layer

#### [MODIFY] [CareerCourseDto.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/career/dto/CareerCourseDto.java)
- Extend `CareerCourseDto` to include:
  - `enrollmentStatus`: `"NOT_LOGGED_IN" | "NOT_ENROLLED" | "ENROLLED" | "COMPLETED"`
  - `paymentStatus`: `"PAID" | "PENDING" | "FAILED" | null`
  - `progress`: `Integer` (0 to 100)
  - `courseAccess`: `boolean` (true if active enrollment or free)
  - `completed`: `boolean` (true if completed)
  - Ensure compatibility with both existing property names (`courseTitle`, `courseThumbnail`, `coursePrice`) and standard names (`title`, `imageUrl`, `price`).

#### [NEW] [CourseEnrollmentStatusResponse.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/course/dto/CourseEnrollmentStatusResponse.java)
- Response DTO for dedicated status endpoint:
  ```java
  public record CourseEnrollmentStatusResponse(
      Long courseId,
      String enrollmentStatus,
      String paymentStatus,
      Integer progress,
      boolean courseAccess,
      boolean completed
  ) {}
  ```

#### [MODIFY] [OrderRepository.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/payment/repository/OrderRepository.java)
- Add query:
  `Optional<Order> findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(UUID userId, Long courseId);`

#### [MODIFY] [PaymentRepository.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/payment/repository/PaymentRepository.java)
- Add queries:
  `Optional<Payment> findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(UUID userId, Long courseId);`
  `Optional<Payment> findFirstByUserIdAndCourseIdAndPaymentStatusOrderByCreatedAtDesc(UUID userId, Long courseId, PaymentStatus status);`

#### [MODIFY] [CareerService.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/career/service/CareerService.java)
- Inject `EnrollmentRepository`, `OrderRepository`, `PaymentRepository`, and `UserRepository`.
- Add helper `resolveCourseStatus(User user, Course course)`:
  - If `user == null`: return `NOT_LOGGED_IN`, `paymentStatus = null`, `courseAccess = false`.
  - Check `enrollmentRepository.findByUserIdAndCourseId(userId, courseId)`:
    - If active: `enrollmentStatus = "ENROLLED"`, `paymentStatus = "PAID"`, `courseAccess = true`, `progress = enrollment.getProgressPercentage()`.
    - If completed: `enrollmentStatus = "COMPLETED"`, `paymentStatus = "PAID"`, `courseAccess = true`, `completed = true`.
  - If no enrollment, check latest `Order` / `Payment`:
    - If order `PENDING`: `paymentStatus = "PENDING"`, `enrollmentStatus = "NOT_ENROLLED"`.
    - If order/payment `FAILED`: `paymentStatus = "FAILED"`, `enrollmentStatus = "NOT_ENROLLED"`.
    - Default: `enrollmentStatus = "NOT_ENROLLED"`, `paymentStatus = null`, `courseAccess = false`.
- Update `mapToCareerDetailResponse(Career career, User user)` to populate enriched `CareerCourseDto`s with real user statuses.

#### [MODIFY] [CareersController.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/career/controller/CareersController.java)
- Update `GET /api/careers/slug/{slug}` and `GET /api/careers/{id}` to accept `Principal principal` (or read `SecurityContextHolder`), lookup authenticated user (if present), and pass to `careerService`.

#### [MODIFY] [CourseService.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/course/service/CourseService.java) & [CourseController.java](file:///d:/Ingage%20project/Backend/src/main/java/com/lms/Backend/course/controller/CourseController.java)
- Add `GET /api/courses/{id}/enrollment-status` returning `CourseEnrollmentStatusResponse`.
- Add `GET /api/courses/{id}/learn`:
  - Validates authentication + active enrollment.
  - Returns `403 Forbidden` if un-enrolled in a paid course.

---

### 2. Frontend Career Page & API Layer

#### [MODIFY] [careerApi.ts](file:///d:/Ingage%20project/frontend/src/api/careerApi.ts)
- Update `CareerCourseDto` with the status fields: `enrollmentStatus`, `paymentStatus`, `progress`, `courseAccess`, `completed`.
- Add `getCourseEnrollmentStatus(courseId: number): Promise<CourseEnrollmentStatusResponse>`.

#### [MODIFY] [CareerDetailPage.tsx](file:///d:/Ingage%20project/frontend/src/pages/CareerDetailPage.tsx)
- Redesign "Recommended Courses" section:
  - Show course image, category, title, level, price (₹).
  - Dynamic button according to backend status:
    - `NOT_LOGGED_IN`: **[Sign Up to Continue]** &rarr; triggers `onOpenAuth('signup', ...)`.
    - `LOGGED_IN + NOT_ENROLLED`: **[Buy Now]** &rarr; initiates Razorpay order creation + checkout modal right on the page.
    - `PAYMENT_PENDING`: **[Payment Pending]** &rarr; status notification.
    - `PAYMENT_FAILED`: **[Buy Again]** &rarr; retries Razorpay payment.
    - `ENROLLED`: **[Start Learning]** &rarr; navigates to `/courses/:id`. Shows progress indicator.
    - `COMPLETED`: **[View Certificate]** / **[Completed]** &rarr; navigates to `/profile/certificates`.
  - Seamless post-payment refresh: after Razorpay verification succeeds, refetches `getCareerBySlug(roleId)` from backend so button immediately transforms to `[Start Learning]`.
  - Display loading skeleton during status checks to prevent flickering "Buy Now" for enrolled users.

---

## Verification Plan

### Automated Verification
1. Run backend compilation: `.\mvnw.cmd test-compile`.
2. Run frontend compilation: `npm run build`.
3. Execute end-to-end multi-user test script (`scratch/test_career_enrollment_flow.ps1`):
   - **User A**:
     - Check `/api/careers/slug/data-analyst` as anonymous user &rarr; courses show `enrollmentStatus = "NOT_LOGGED_IN"`.
     - Login as User A.
     - Check `/api/careers/slug/data-analyst` &rarr; courses show `enrollmentStatus = "NOT_ENROLLED"` and `courseAccess = false`.
     - Purchase "Data Analytics with SQL & Tableau" via `/api/payments/create-order` and `/api/payments/verify`.
     - Re-check `/api/careers/slug/data-analyst` &rarr; course now shows `enrollmentStatus = "ENROLLED"`, `paymentStatus = "PAID"`, and `courseAccess = true`.
     - Check other recommended course ("Python Backend Development") &rarr; still `NOT_ENROLLED`.
     - Test direct `/api/courses/{id}/learn` &rarr; 200 OK for enrolled course, 403 Forbidden for un-enrolled course.
   - **User B**:
     - Login as User B.
     - Check `/api/careers/slug/data-analyst` &rarr; User A's enrolled course still shows `NOT_ENROLLED` and `courseAccess = false` for User B (multi-user isolation verified).
