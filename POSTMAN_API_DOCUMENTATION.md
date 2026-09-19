# Ingage LMS API — Complete Postman & REST API Documentation

This document provides a comprehensive, production-accurate reference for all REST endpoints currently implemented in the **Ingage LMS Spring Boot backend**.

All endpoints are organized in the Postman collection at [`postman/Ingage-LMS-API.postman_collection.json`](file:///d:/Ingage%20project/postman/Ingage-LMS-API.postman_collection.json) with environment variables configured in [`postman/Ingage-LMS-Environment.postman_environment.json`](file:///d:/Ingage%20project/postman/Ingage-LMS-Environment.postman_environment.json).

---

## 1. Environments & Base URLs

The application uses servlet context path `/api`. Every endpoint is referenced using `{{baseUrl}}`.

| Environment | Base URL Variable (`{{baseUrl}}`) | Frontend Target |
| :--- | :--- | :--- |
| **Local Development** | `http://localhost:8080/api` | `http://localhost:3000` |
| **Render Production** | `https://ingage-lms.onrender.com/api` | `https://ingage-lms.vercel.app` |

---

## 2. Master API Endpoint Inventory

| # | HTTP Method | Final Endpoint Path | Security / Auth | Required Role | Purpose / Feature |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `POST` | `/auth/register` | Public | - | Register a new student account with name, email, password |
| 2 | `POST` | `/auth/login` | Public | - | Authenticate user credentials and return 24-hr JWT |
| 3 | `POST` | `/auth/login/request-otp` | Public | - | Generate 6-digit numeric login verification code |
| 4 | `POST` | `/auth/login/verify-otp` | Public | - | Validate 6-digit OTP code and return JWT access token |
| 5 | `GET` | `/auth/me` | Bearer Token | Authenticated | Inspect current authentication context identity |
| 6 | `POST` | `/auth/logout` | Public | - | Clear HTTP-only `AUTH_TOKEN` cookie |
| 7 | `GET` | `/oauth2/authorization/google` | Browser Flow | - | Browser redirect to Google OAuth 2.0 consent screen |
| 8 | `GET` | `/login/oauth2/code/google` | Google Callback | - | Spring Security Google OAuth callback & JWT redirect |
| 9 | `GET` | `/oauth2/authorization/linkedin` | Browser Flow | - | Browser redirect to LinkedIn OAuth authorization screen |
| 10 | `GET` | `/login/oauth2/code/linkedin` | LinkedIn Callback | - | Spring Security LinkedIn OAuth callback & JWT redirect |
| 11 | `GET` | `/users/profile` | Bearer Token | Authenticated | Fetch authenticated user's profile with accessible avatar |
| 12 | `GET` | `/users/me` | Bearer Token | Authenticated | Alias endpoint for `/users/profile` used by frontend Redux |
| 13 | `POST` | `/users/profile/image` | Bearer Token | Authenticated | Upload user profile image (`multipart/form-data`) |
| 14 | `DELETE` | `/users/profile/image` | Bearer Token | Authenticated | Remove profile image from disk and clear DB field |
| 15 | `GET` | `/courses` | Public | - | List published courses with optional `?search=` filter |
| 16 | `GET` | `/courses/{id}` | Public | - | Fetch single course overview by numeric ID |
| 17 | `GET` | `/courses/{id}/content` | Bearer Token / Opt | - | Get full curriculum (sections & lessons) with preview metadata |
| 18 | `GET` | `/courses/{id}/enrollment-status` | Bearer Token / Opt | - | Check if current user is actively enrolled in course |
| 19 | `GET` | `/courses/{id}/learn` | Bearer Token | Enrolled / Admin | Retrieve authorized player environment and lesson states |
| 20 | `GET` | `/courses/{courseId}/lessons/{lessonId}/video` | Bearer Token / Opt | Enrolled / Preview | HTTP Range stream protected lesson video (`video/mp4`) |
| 21 | `GET` | `/learning/my-enrollments` | Bearer Token | Authenticated | Get all course enrollments and progress for authenticated user |
| 22 | `GET` | `/learning/lessons/{lessonId}/video` | Bearer Token / Opt | Enrolled / Preview | Direct video streaming endpoint with byte-range support |
| 23 | `POST` | `/progress/complete-lesson` | Bearer Token | Authenticated | Mark lesson complete and recalculate course progress % |
| 24 | `POST` | `/orders/create` | Bearer Token | Authenticated | Create Razorpay order with optional coupon code |
| 25 | `POST` | `/payments/create-order` | Bearer Token | Authenticated | Alias endpoint for `/orders/create` |
| 26 | `POST` | `/payments/verify` | Bearer Token | Authenticated | Verify Razorpay HMAC signature and activate enrollment |
| 27 | `GET` | `/payments/my-payments` | Bearer Token | Authenticated | List all payments made by authenticated user |
| 28 | `POST` | `/discounts/validate` | Public / Opt | - | Validate coupon code and calculate discounted totals |
| 29 | `POST` | `/webhooks/razorpay` | Razorpay Webhook | - | Receive asynchronous payment capture notifications |
| 30 | `GET` | `/career/job-roles` | Public | - | List active job role tracks |
| 31 | `GET` | `/career/job-roles/{slug}` | Public | - | Get single job role by slug (e.g. `data-analyst`) |
| 32 | `GET` | `/careers` | Public | - | Paginated and filtered career paths catalog |
| 33 | `GET` | `/careers/search` | Public | - | Keyword search across careers and skills |
| 34 | `GET` | `/careers/slug/{slug}` | Public / Opt | - | Get full career roadmap and linked courses by slug |
| 35 | `GET` | `/careers/{id}` | Public / Opt | - | Get career roadmap by numeric ID |
| 36 | `GET` | `/careers/categories` | Public | - | Get list of distinct career categories |
| 37 | `GET` | `/careers/{id}/stats` | Public | - | Get student enrollment and completion stats for a career |
| 38 | `GET` | `/explore` | Public | - | Combined catalog of featured careers and capstone projects |
| 39 | `GET` | `/projects` | Public | - | List active projects with optional `?industry=` filter |
| 40 | `GET` | `/projects/{slug}` | Public | - | Get comprehensive project details by slug |
| 41 | `GET` | `/opportunities` | Bearer Token | Authenticated | List career and internship opportunities |
| 42 | `GET` | `/certificates/verify/{verificationCode}` | Public | - | Verify issued student certificate publicly |
| 43 | `GET` | `/certificates/verify/{verificationCode}/download` | Public | - | Download verified certificate as PDF |
| 44 | `GET` | `/health` | Public | - | Backend health and liveness probe |
| 45 | `GET` | `/uploads/{category}/{filename}` | Public | - | Serve public uploaded assets (videos blocked here) |
| 46 | `GET` | `/admin/dashboard` | Bearer Token | `ROLE_ADMIN` | Overview metrics (users, courses, revenue, enrollments) |
| 47 | `GET` | `/admin/analytics` | Bearer Token | `ROLE_ADMIN` | Platform analytics and revenue charts |
| 48 | `GET` | `/admin/users` | Bearer Token | `ROLE_ADMIN` | List all platform users with filter |
| 49 | `GET` | `/admin/users/{id}` | Bearer Token | `ROLE_ADMIN` | Get detailed user profile and transaction history |
| 50 | `PUT` | `/admin/users/{id}/status` | Bearer Token | `ROLE_ADMIN` | Suspend or activate user account |
| 51 | `PUT` | `/admin/users/{id}/role` | Bearer Token | `ROLE_ADMIN` | Modify user role (`STUDENT`, `INSTRUCTOR`, `ADMIN`) |
| 52 | `DELETE` | `/admin/users/{id}` | Bearer Token | `ROLE_ADMIN` | Delete or deactivate user record |
| 53 | `GET` | `/admin/courses` | Bearer Token | `ROLE_ADMIN` | List all courses with status filter (`ALL`, `DRAFT`, etc.) |
| 54 | `GET` | `/admin/courses/{id}` | Bearer Token | `ROLE_ADMIN` | Get complete course details with curriculum tree |
| 55 | `POST` | `/admin/courses` | Bearer Token | `ROLE_ADMIN` | Create new course in DRAFT status |
| 56 | `PUT` | `/admin/courses/{id}` | Bearer Token | `ROLE_ADMIN` | Update existing course details |
| 57 | `POST` | `/admin/courses/{id}/publish` | Bearer Token | `ROLE_ADMIN` | Publish course to public catalog |
| 58 | `POST` | `/admin/courses/{id}/unpublish` | Bearer Token | `ROLE_ADMIN` | Revert published course to DRAFT |
| 59 | `POST` | `/admin/courses/{id}/archive` | Bearer Token | `ROLE_ADMIN` | Archive course |
| 60 | `DELETE` | `/admin/courses/{id}` | Bearer Token | `ROLE_ADMIN` | Delete course |
| 61 | `POST` | `/admin/courses/{id}/sections` | Bearer Token | `ROLE_ADMIN` | Add section module to course |
| 62 | `PUT` | `/admin/sections/{id}` | Bearer Token | `ROLE_ADMIN` | Update section title or display order |
| 63 | `DELETE` | `/admin/sections/{id}` | Bearer Token | `ROLE_ADMIN` | Delete section and all nested lessons |
| 64 | `POST` | `/admin/sections/{id}/lessons` | Bearer Token | `ROLE_ADMIN` | Add lesson with video URL and preview settings |
| 65 | `PUT` | `/admin/lessons/{id}` | Bearer Token | `ROLE_ADMIN` | Update lesson metadata |
| 66 | `DELETE` | `/admin/lessons/{id}` | Bearer Token | `ROLE_ADMIN` | Delete lesson |
| 67 | `GET` | `/admin/courses/{id}/analytics` | Bearer Token | `ROLE_ADMIN` | Fetch performance analytics for a course |
| 68 | `GET` | `/admin/courses/{id}/students` | Bearer Token | `ROLE_ADMIN` | List enrolled students and progress for a course |
| 69 | `GET` | `/admin/categories` | Bearer Token | `ROLE_ADMIN` | List all course categories |
| 70 | `POST` | `/admin/categories` | Bearer Token | `ROLE_ADMIN` | Create course category |
| 71 | `GET` | `/admin/payments` | Bearer Token | `ROLE_ADMIN` | Platform payment transactions ledger |
| 72 | `GET` | `/admin/orders` | Bearer Token | `ROLE_ADMIN` | Platform Razorpay orders audit table |
| 73 | `GET` | `/admin/discounts` | Bearer Token | `ROLE_ADMIN` | List all promotional coupon codes |
| 74 | `POST` | `/admin/discounts` | Bearer Token | `ROLE_ADMIN` | Create new promotional discount code |
| 75 | `PUT` | `/admin/discounts/{id}` | Bearer Token | `ROLE_ADMIN` | Update discount percentage or limits |
| 76 | `PUT` | `/admin/discounts/{id}/status` | Bearer Token | `ROLE_ADMIN` | Toggle discount code active status |
| 77 | `DELETE` | `/admin/discounts/{id}` | Bearer Token | `ROLE_ADMIN` | Delete discount code |
| 78 | `GET` | `/admin/discounts/analytics` | Bearer Token | `ROLE_ADMIN` | Discount redemption analytics |
| 79 | `GET` | `/admin/progress` | Bearer Token | `ROLE_ADMIN` | Monitor platform-wide student progress records |
| 80 | `GET` | `/admin/certificates` | Bearer Token | `ROLE_ADMIN` | Audit all issued completion certificates |
| 81 | `GET` | `/admin/certificates/{id}/download` | Bearer Token | `ROLE_ADMIN` | Download PDF certificate by database ID |
| 82 | `POST` | `/admin/certificates/issue` | Bearer Token | `ROLE_ADMIN` | Manually issue certificate to a student |
| 83 | `GET` | `/admin/careers` | Bearer Token | `ROLE_ADMIN` | List all career paths (including unpublished) |
| 84 | `POST` | `/admin/careers` | Bearer Token | `ROLE_ADMIN` | Create new career pathway |
| 85 | `PUT` | `/admin/careers/{id}` | Bearer Token | `ROLE_ADMIN` | Update career pathway |
| 86 | `PUT` | `/admin/careers/{id}/publish` | Bearer Token | `ROLE_ADMIN` | Toggle career published status |
| 87 | `PUT` | `/admin/careers/{id}/status` | Bearer Token | `ROLE_ADMIN` | Toggle career active status |
| 88 | `DELETE` | `/admin/careers/{id}` | Bearer Token | `ROLE_ADMIN` | Delete career path |
| 89 | `POST` | `/admin/careers/{id}/assign-course/{courseId}` | Bearer Token | `ROLE_ADMIN` | Link course to career roadmap |
| 90 | `DELETE` | `/admin/careers/{id}/remove-course/{courseId}` | Bearer Token | `ROLE_ADMIN` | Unlink course from career roadmap |
| 91 | `GET` | `/admin/projects` | Bearer Token | `ROLE_ADMIN` | List all capstone projects |
| 92 | `POST` | `/admin/projects` | Bearer Token | `ROLE_ADMIN` | Create new capstone project |
| 93 | `PUT` | `/admin/projects/{id}` | Bearer Token | `ROLE_ADMIN` | Update project metadata |
| 94 | `PUT` | `/admin/projects/{id}/status` | Bearer Token | `ROLE_ADMIN` | Toggle project active status |
| 95 | `PUT` | `/admin/projects/{id}/publish` | Bearer Token | `ROLE_ADMIN` | Toggle project published status |
| 96 | `DELETE` | `/admin/projects/{id}` | Bearer Token | `ROLE_ADMIN` | Delete project |
| 97 | `GET` | `/admin/activity` | Bearer Token | `ROLE_ADMIN` | View audit trail of admin mutations |
| 98 | `POST` | `/admin/media/upload` | Bearer Token | `ROLE_ADMIN` | Admin asset upload (`multipart/form-data`) |

---

## 3. Authentication & Social OAuth 2.0 Flow

### Normal Email & Password Authentication
```
[User Registration]
POST /api/auth/register
Body: { "name": "Alex Mercer", "email": "alex@example.com", "password": "Password123!" }
Response: 201 Created -> { "token": "JWT...", "userId": "...", "role": "STUDENT" }

[User Login]
POST /api/auth/login
Body: { "email": "alex@example.com", "password": "Password123!" }
Response: 200 OK -> { "token": "JWT...", "userId": "...", "role": "STUDENT" }
* Note: Automatically populates {{token}} in Postman environment.
```

### Social OAuth 2.0 Architecture (Google & LinkedIn)
The platform uses backend-orchestrated OAuth 2.0 with Spring Security:

```text
Browser / Frontend               Spring Boot (Render)                  Google / LinkedIn
       │                                  │                                    │
       │── 1. Click "Sign in with Google" │                                    │
       │   GET /api/oauth2/authorization/google                                │
       │─────────────────────────────────>│                                    │
       │                                  │── 2. Redirect to Google Auth ─────>│
       │<─ 3. Browser follows 302 ────────│                                    │
       │                                                                       │
       │── 4. User signs in and consents on Google ───────────────────────────>│
       │                                                                       │
       │                                  │<── 5. Google redirects with code ──│
       │                                  │    GET /api/login/oauth2/code/google?code=...
       │                                  │                                    │
       │                                  │── 6. Backend exchanges code for ID │
       │                                  │   Finds/creates User in PostgreSQL │
       │                                  │   Generates JWT (24-hr expiry)     │
       │                                  │                                    │
       │<─ 7. Redirect to Frontend ───────│                                    │
       │   Location: https://ingage-lms.vercel.app/oauth/callback?token=JWT_TOKEN
       │                                                                       │
       │── 8. Frontend reads token, stores in localStorage ('ingage_token')   │
       │── 9. Calls GET /api/users/me to load profile into Redux store         │
       │── 10. Navigates to student dashboard (or /admin for ADMIN)            │
```

**Key Security Detail**: Google Cloud OAuth Console's **Authorized Redirect URI** must be:
- **Production**: `https://ingage-lms.onrender.com/api/login/oauth2/code/google`
- **Development**: `http://localhost:8080/api/login/oauth2/code/google`

---

## 4. User Profile & Image Upload Specification

### Profile Retrieval
- `GET /api/users/profile` (or `GET /api/users/me`)
- Header: `Authorization: Bearer {{token}}`
- Returns:
  ```json
  {
    "success": true,
    "message": "Authenticated user profile",
    "userId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
    "name": "Alex Mercer",
    "email": "alex.mercer@example.com",
    "role": "STUDENT",
    "profileImage": "/api/uploads/profile-images/user-d290f1ee-avatar.jpg",
    "avatar": "/api/uploads/profile-images/user-d290f1ee-avatar.jpg",
    "avatarUrl": "/api/uploads/profile-images/user-d290f1ee-avatar.jpg"
  }
  ```

### Profile Image Upload
- `POST /api/users/profile/image`
- Content-Type: `multipart/form-data`
- Form Parameters:
  - `image` (or `file`): Binary image file
- Validation & Security Rules:
  - Max file size: **5 MB**
  - Allowed MIME types: `image/jpeg`, `image/jpg`, `image/png`, `image/webp`
  - Path traversal checks: Blocks `..`, `/`, `\` in file names
  - Safe replacement: New image is saved and database record updated in PostgreSQL *before* deleting previous uploaded avatar
  - Social avatars: Google/LinkedIn external avatars (`https://lh3.googleusercontent.com/...`) are preserved and never deleted from disk

---

## 5. Payment & Enrollment Workflow (Razorpay)

Course enrollment requires successful payment processing:

```text
1. Select Course & Validate Coupon (Optional):
   POST /api/discounts/validate
   Body: { "couponCode": "WELCOME50", "amount": 4999.0 }

2. Create Razorpay Checkout Order:
   POST /api/orders/create
   Body: { "courseId": 1, "couponCode": "WELCOME50" }
   Response: {
     "orderId": "order_OG918xK91",
     "amount": 249950,
     "currency": "INR",
     "keyId": "rzp_test_..."
   }

3. User completes payment in Razorpay modal.

4. Backend Signature Verification & Enrollment Activation:
   POST /api/payments/verify
   Body: {
     "razorpay_payment_id": "pay_OG920aL02",
     "razorpay_order_id": "order_OG918xK91",
     "razorpay_signature": "e5b8d..."
   }
   Backend Action:
   - Verifies HMAC-SHA256 signature against razorpay.key.secret
   - Checks if user is already enrolled (idempotent)
   - Creates Enrollment record with status 'ACTIVE'
   - Records Payment entity with status 'SUCCESS'
   Response: {
     "success": true,
     "message": "Payment verified and enrollment completed",
     "enrollmentId": 12,
     "courseId": 1
   }

5. Verify Enrollment:
   GET /api/courses/1/enrollment-status
   Response: { "isEnrolled": true, "status": "ACTIVE", "progressPercentage": 0 }
```

---

## 6. Protected Video Streaming & Security

All video content is protected against unauthorized direct downloads:

1. **Static Handler Protection**:
   `GET /api/uploads/video/...` and `GET /api/uploads/videos/...` return HTTP 403 Forbidden to prevent direct bypass.
2. **Authorized Streaming Route**:
   `GET /api/courses/{courseId}/lessons/{lessonId}/video` or `GET /api/learning/lessons/{lessonId}/video`
3. **Authorization Check**:
   - If `lesson.isFreePreview() == true` or `course.getPrice() == 0`: Public access allowed.
   - Otherwise: Request MUST have valid JWT (`Authorization: Bearer {{token}}`). The backend verifies that the user is an **ADMIN** or has an **active enrollment** in the parent course.
4. **HTTP Partial Content Streaming**:
   - Supports HTTP `Range: bytes=start-end` requests.
   - Returns HTTP `206 Partial Content` with `Content-Type: video/mp4`, `Content-Range`, and `no-store, no-cache` security headers.

---

## 7. Educator Functions Note

In Ingage LMS, educators and course creators manage curriculum through the **Admin Course Management APIs** (`/api/admin/courses/**`):
- Course creation, updates, and publishing
- Section & Lesson CRUD
- Enrolled student rosters & course analytics
- All administrative routes require JWT with `ROLE_ADMIN`.

---

## 8. Importing Into Postman

1. Open Postman.
2. Click **Import** (top-left).
3. Select both files:
   - `postman/Ingage-LMS-API.postman_collection.json`
   - `postman/Ingage-LMS-Environment.postman_environment.json`
4. Select the **Ingage LMS Environment** in the environment dropdown.
5. Execute `1. Authentication -> Login` to automatically populate the `{{token}}` variable and start testing.
