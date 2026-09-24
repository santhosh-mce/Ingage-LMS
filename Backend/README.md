# Ingage LMS Backend

Spring Boot REST API for the Ingage learning and career platform.

## Structure

```text
src/main/java/com/lms/Backend/
├── BackendApplication.java
├── config/
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── common/
│   ├── controller/
│   │   └── HealthController.java
│   └── exception/
│       └── GlobalExceptionHandler.java
├── auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── service/
│   │   └── AuthService.java
│   └── dto/
│       ├── RegisterRequest.java
│       ├── LoginRequest.java
│       └── AuthResponse.java
├── user/
│   ├── controller/
│   │   └── UserController.java
│   ├── repository/
│   │   └── UserRepository.java
│   └── entity/
│       ├── User.java
│       └── UserRole.java
├── career/
│   └── controller/
│       └── CareerController.java
├── learning/
│   └── controller/LearningController.java
├── project/
│   └── controller/ProjectController.java
├── opportunity/
│   └── controller/OpportunityController.java
├── application/
│   └── controller/ApplicationController.java
└── notification/
    └── controller/NotificationController.java
```

Each feature should grow using the same layers:

```text
feature/
├── controller/      HTTP endpoints and request validation
├── service/         business rules and transactions
├── repository/      Spring Data JPA interfaces
├── entity/          database models
├── dto/             request and response contracts
└── mapper/          entity/DTO conversion when needed
```

## API base URL

The application context path is `/api`.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/health` | Service health check |
| POST | `/api/auth/register` | Create a learner account |
| POST | `/api/auth/login` | Verify learner credentials |
| GET | `/api/users/me` | Protected authenticated-user endpoint |
| GET | `/api/careers` | Career catalog slugs |
| GET | `/api/projects` | Project catalog placeholder |
| GET | `/api/opportunities` | Opportunity catalog placeholder |

## Configuration

Set these environment variables for local or deployed environments:

```text
DB_URL=jdbc:postgresql://localhost:5432/lms_db
DB_USERNAME=postgres
DB_PASSWORD=your-password
SERVER_PORT=8080
FRONTEND_URLS=http://localhost:3000,http://localhost:5173
FRONTEND_OAUTH_SUCCESS_URL=http://localhost:3000/oauth/callback
JWT_SECRET=replace-with-a-random-secret-at-least-32-characters
JWT_EXPIRATION=86400000
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
LINKEDIN_CLIENT_ID=your-linkedin-client-id
LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret
```

The database password and JWT secret are intentionally not stored in source control. Set `JWT_SECRET` in the process environment before starting the backend. Hibernate currently uses `ddl-auto=update` for development; use migrations before production deployment. New accounts use the `STUDENT` role and passwords are stored as BCrypt hashes.

OAuth callback URLs:

```text
http://localhost:8080/api/login/oauth2/code/google
http://localhost:8080/api/login/oauth2/code/linkedin
```

Start OAuth only after setting the provider credentials. The OAuth success handler stores the application JWT in an HttpOnly cookie and redirects to the frontend callback; it does not put the JWT in the URL.

## Run

```powershell
$env:JWT_SECRET = "replace-with-a-random-secret-at-least-32-characters"
.\mvnw.cmd spring-boot:run
```

Run tests with:

```powershell
.\mvnw.cmd test
```