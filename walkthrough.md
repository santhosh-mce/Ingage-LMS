# Explore Dropdown: Dynamic PostgreSQL Integration Walkthrough

The LMS Header **Explore dropdown** has been successfully converted from hardcoded frontend arrays into a dynamic, backend-driven architecture powered by **Spring Boot** and **PostgreSQL**.

---

## 1. Architecture & Summary of Changes

```
┌────────────────────────────────────────────────────────┐
│               React Frontend Header                    │
│    (Dynamic Explore Dropdown with Real-Time Fetch)     │
└───────────────┬────────────────────────┬───────────────┘
                │                        │
       GET /api/explore            GET /api/projects
                │                        │
┌───────────────▼────────────────────────▼───────────────┐
│               Spring Boot Backend (REST)               │
│  ExploreController.java       ProjectController.java   │
│  AdminCareerController.java   AdminProjectController   │
└───────────────┬────────────────────────┬───────────────┘
                │                        │
┌───────────────▼────────────────────────▼───────────────┐
│                 PostgreSQL Database                    │
│     careers table                projects table        │
│   (active=true, published=true) (active=true, published=true)
└────────────────────────────────────────────────────────┘
```

---

## 2. Changes Made

### Backend (Spring Boot & PostgreSQL)
1. **Career Entity & Migration:**
   - Added `published` column (`boolean default true`) with `@Index` on `Career.java`.
   - Annotated `@OneToMany` lazy collections (`skills`, `responsibilities`, `roadmaps`, `projects`, `careerCourses`, `opportunities`) with `@JsonIgnore` to prevent Jackson serialization issues.
   - Added `findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscTitleAsc()` to `CareerRepository.java`.
   - Updated `AdminCareerService.java` with `@Transactional(readOnly = true)` and toggle methods.
   - Added `PUT /api/admin/careers/{id}/publish` and `PUT /api/admin/careers/{id}/status` in `AdminCareerController.java`.

2. **Project Entity & Module:**
   - Created standalone `Project` entity (`com.lms.Backend.project.entity.Project`) with fields: `id`, `title`, `slug`, `industry`, `category`, `description`, `difficulty`, `duration`, `skillsCount`, `learnersCount`, `imageUrl`, `prerequisites`, `techStack`, `whatYouWillBuild`, `learningOutcomes`, `skillsLearned`, `active`, `published`, `displayOrder`.
   - Created `ProjectRepository` with active/published queries.
   - Created `ProjectDto` and `ProjectService` with transactional entity-to-DTO conversion to avoid Hibernate lazy-initialization errors.
   - Created `ProjectDataInitializer` to auto-seed default tracks on startup.
   - Updated `ProjectController` (`GET /api/projects` and `GET /api/projects/{slug}`).
   - Created `AdminProjectController` (`GET`, `POST`, `PUT`, `DELETE`, `PUT /{id}/publish`, `PUT /{id}/status`).

3. **Explore Aggregator Endpoint:**
   - Created `ExploreResponse` DTO (`careers: List<CareerExploreDto>`, `projects: List<ProjectExploreDto>`).
   - Created `ExploreController` exposing `GET /api/explore` (publicly accessible via `SecurityConfig.java`).

### Frontend (React & TypeScript)
1. **API Layer:**
   - Created `frontend/src/api/exploreApi.ts` with `getExploreData()`, `getPublicProjects()`, and `getPublicProjectBySlug()`.
   - Updated `frontend/src/api/adminApi.ts` with Admin Project CRUD and Career/Project toggle methods.
2. **Explore Dropdown (`Header.tsx`):**
   - Replaced all hardcoded imports from `mockData.ts` with dynamic API calls to `getExploreData()`.
   - Added loading skeleton state, error fallback, empty states, and dynamic search filter.
   - Navigates to `/roles/:slug` for Job Roles and `/projects/:slug` (or `/projects`) for Projects.
3. **Admin Projects Management (`AdminProjectsPage.tsx`):**
   - Created full management dashboard with search, industry filter, Active / Inactive toggle, Published / Unpublished toggle, modal form for create/edit, and delete confirmation.
   - Registered `/admin/projects` route in `App.tsx` and added navigation link in `AdminSidebar.tsx`.
4. **Admin Careers (`AdminCareersPage.tsx`):**
   - Added distinct toggle buttons for **Published / Unpublished** and **Active / Inactive**.

---

## 3. Automated Verification Results

### Public Endpoints:
```bash
# Public Explore endpoint
curl -s http://localhost:8080/api/explore
# Response: HTTP 200 OK with 20 active/published careers and 12 active/published projects.

# Public Projects list
curl -s http://localhost:8080/api/projects
# Response: HTTP 200 OK with 12 projects.

# Public Project details by slug
curl -s http://localhost:8080/api/projects/patient-health-tracker
# Response: HTTP 200 OK with project details.
```

### Full Lifecycle Integration Test:
```
--- TEST 1: Create New Career 'AI Engineer' ---
Created Career ID: 21, Title: AI Engineer, Slug: ai-engineer
Is AI Engineer in Explore dropdown? True
Is AI Engineer in Explore dropdown after unpublishing? False
Deleted temporary Career

--- TEST 2: Create New Project 'AI Resume Builder' ---
Created Project ID: 13, Title: AI Resume Builder, Slug: ai-resume-builder
Is AI Resume Builder in Explore dropdown? True
Is AI Resume Builder in Explore dropdown after unpublishing? False
Deleted temporary Project
Is AI Resume Builder in Explore dropdown after delete? False
```

### Frontend Build:
```bash
npm run build
# Result: built in 9.74s with 0 TypeScript/Vite errors.
```

---

## 4. Manual Verification Steps for User

1. **Verify Explore Dropdown:**
   - Open `http://localhost:3000`.
   - Click/hover on **Explore** in the header.
   - Notice both columns: **Job-Ready Training** and **Project-Based Learning**.
   - Type in the search box (e.g., "Data" or "Health") to verify live filtering.
   - Click on "Data Analyst" -> redirects to `/roles/data-analyst`.
   - Click on "Patient Health Tracker" -> redirects to `/projects/patient-health-tracker`.

2. **Verify Admin Career Toggle:**
   - Go to `http://localhost:3000/admin/careers` (log in as `admin@ingage.com` / `Admin@123`).
   - Click the "Published" toggle on "Data Analyst" to unpublish it.
   - Refresh or open the Explore dropdown -> "Data Analyst" is gone.
   - Toggle "Published" back on -> "Data Analyst" reappears immediately.

3. **Verify Admin Project Controls:**
   - Go to `http://localhost:3000/admin/projects`.
   - Click "+ Add Project", fill in details and click "Create Project".
   - Open Explore dropdown -> New project appears instantly under "Project-Based Learning".
   - Unpublish or delete the project -> Disappears instantly.
