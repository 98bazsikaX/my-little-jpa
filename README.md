# Database Manager

Full-stack skeleton: **Angular 22** (Material) + **Spring Boot 3.5** (JPA) + **PostgreSQL 18**.

> Mostly vibe coded with Claude Code.

## Quick start

```bash
docker compose up -d
```

- Frontend: [http://localhost](http://localhost) (nginx + Angular production build)
- Backend API: [http://localhost:8080/api/tasks](http://localhost:8080/api/tasks)
- PostgreSQL: `localhost:5432`, database `dbmanager`

Default admin: `admin` / `1234`

## Development workflow

Run backend and frontend **locally** for hot-reload. Docker is for PostgreSQL only.

```bash
# Database
docker compose up -d postgres

# Backend (JDK 22 + Maven required)
cd backend
export JAVA_HOME=~/jdk-22
mvn spring-boot:run              # http://localhost:8080

# Frontend (Node 26 required)
cd frontend
npm start                         # http://localhost:4200
```

## Project structure

Backend **package-by-feature**:

```
backend/src/main/java/com/example/databasemanager/
├── DatabaseManagerApplication.java
├── common/
│   ├── AbstractModel.java            # @MappedSuperclass: id, created, updated
│   ├── filter/                       # annotation-driven filter framework
│   │   ├── AbstractFilter.java       # base: reflection → Specification<T>
│   │   ├── FilterField.java          # @annotation: marks filter fields
│   │   ├── FilterType.java           # enum: LIKE, EQUALS, DATE_RANGE
│   │   └── DateRange.java            # record(from, to)
│   └── exception/                    # global error handling
│       ├── GlobalExceptionHandler.java # @ControllerAdvice
│       ├── DuplicateResourceException.java  # 409 Conflict
│       └── ErrorResponse.java        # {status, message, timestamp}
├── config/
│   ├── CorsConfig.java
│   ├── FilterConfig.java             # JwtFilter + QueryMethodFilter registration
│   └── QueryMethodFilter.java        # rewrites QUERY /api/users → POST /search
├── security/
│   ├── JwtUtil.java                  # HMAC-SHA JWT generation/validation
│   └── JwtFilter.java                # OncePerRequestFilter: Bearer token guard
├── task/
│   ├── entity/Task.java
│   ├── dto/TaskDto.java
│   ├── repository/TaskRepository.java
│   ├── mapper/TaskMapper.java
│   └── controller/TaskController.java
├── user/
│   ├── entity/User.java              # extends AbstractModel
│   ├── dto/
│   │   ├── UserDto.java              # response (no password)
│   │   ├── CreateUserRequest.java    # @Valid, includes password
│   │   └── UserFilter.java           # extends AbstractFilter<User>
│   ├── repository/UserRepository.java # JpaRepository + JpaSpecificationExecutor
│   ├── mapper/UserMapper.java
│   ├── service/UserService.java      # interface
│   ├── service/UserServiceImpl.java  # BCrypt, uniqueness check, pagination
│   └── controller/UserController.java
└── auth/
    ├── controller/AuthController.java  # POST /api/auth/login
    ├── dto/LoginRequest.java, LoginResponse.java
    └── service/AuthService.java, AuthServiceImpl.java
```

Frontend mirrors same **feature-based** pattern:

```
frontend/src/app/
├── app.ts, app.html, app.css         # nav shell: toolbar, router-outlet
├── app.config.ts                     # providers: router, HTTP, interceptor
├── app.routes.ts                     # / → /tasks, /tasks, /users, /login
├── task/
│   ├── task.ts                       # Task interface
│   ├── task.service.ts               # HttpClient, /api/tasks
│   └── task.component.ts             # standalone, signals, loading/empty/data
├── user/
│   ├── user.ts                       # User interface
│   ├── user.service.ts               # HttpClient, pagination params
│   ├── user.component.ts             # MatTable + MatPaginator + MatSort
│   └── user-dialog.component.ts      # MatDialog create-user form
└── auth/
    ├── auth.service.ts               # login/logout, JWT token, localStorage
    ├── auth.guard.ts                  # CanActivateFn, redirect → /login
    ├── auth.interceptor.ts           # HttpInterceptorFn, Bearer header, 401 → /login
    └── login/
        └── login.component.ts        # standalone, Material form, snackbar errors
```

## Test structure

```
backend/src/test/java/com/example/databasemanager/
├── unit/                             # no Spring context, Mockito
│   ├── security/JwtUtilTest.java     # 7 tests
│   ├── user/service/UserServiceImplTest.java  # 6 tests
│   └── auth/service/AuthServiceImplTest.java  # 4 tests
└── integration/                      # Spring context, H2
    ├── user/
    │   ├── UserRepositoryTest.java   # 8 tests, @DataJpaTest
    │   └── UserControllerTest.java   # 5 tests, @SpringBootTest + MockMvc
    └── auth/
        └── AuthControllerTest.java   # 3 tests, @SpringBootTest + MockMvc

frontend/src/app/
├── auth/
│   ├── auth.service.spec.ts          # 6 tests, TestBed + HttpTestingController
│   ├── auth.guard.spec.ts            # 2 tests
│   └── auth.interceptor.spec.ts      # 4 tests
└── task/
    └── task.service.spec.ts          # 2 tests
```

Run: backend `mvn test` (33 tests), frontend `npm test` (14 tests, Vitest).

## API endpoints

| Method  | Path                 | Auth      | Description              |
|---------|----------------------|-----------|--------------------------|
| GET     | /api/tasks           | JWT       | List all tasks           |
| GET     | /api/users           | JWT       | Paginated user list (`?page=0&size=10&sort=userName,asc`) |
| POST    | /api/users/search    | JWT       | Filter users (JSON body, paginated) |
| POST    | /api/users           | JWT       | Create user (409 on duplicate) |
| DELETE  | /api/users/{id}      | JWT       | Delete user (404 if missing)  |
| POST    | /api/auth/login      | Public    | Login, returns JWT       |
| QUERY   | /api/users           | JWT       | RFC 10008, forwarded to POST /search |

### Filtering (POST /api/users/search)

Request body (`UserFilter` extends `AbstractFilter<User>`):

```json
{
  "userName": "admin",           // LIKE %value% (case-insensitive)
  "email": "@gmail",
  "firstName": "John",
  "lastName": "Doe",
  "created": {"from": "2026-01-01", "to": "2026-12-31"},
  "lastLogin": {"from": "2026-06-01"},
  "updated": {"to": "2026-07-01"}
}
```

All fields optional. Empty body returns all users. Supports pagination (`?page=0&size=10&sort=userName,asc`).

Adding a filter for new entity: extend `AbstractFilter<T>`, annotate fields with `@FilterField`.

## Authentication

JWT-based. Header: `Authorization: Bearer <token>`. Token from `POST /api/auth/login` with `{"userName":"...","password":"..."}`. `JwtFilter` guards `/api/*` (except `/api/auth/login`). Frontend `auth.interceptor.ts` attaches token, `auth.guard.ts` redirects to `/login`. Logout clears token, redirects to `/login`.

## Error handling

Backend: `@ControllerAdvice` (`GlobalExceptionHandler`) maps exceptions to HTTP status codes:

| Exception | Status | When |
|---|---|---|
| `DuplicateResourceException` | 409 Conflict | Duplicate username/email |
| `EntityNotFoundException` | 404 Not Found | User/Task not found |
| `MethodArgumentNotValidException` | 400 Bad Request | Validation failed (field-level) |
| `DataIntegrityViolationException` | 409 Conflict | DB constraint violation (concurrent insert) |
| `Exception` | 500 (logged) | Unexpected errors |

All error responses: `{"status": N, "message": "...", "timestamp": "..."}`.

Frontend: HTTP errors surfaced via `MatSnackBar` (toast notification) with backend error message.

## Tech stack

| Layer        | Technology                                          |
|--------------|-----------------------------------------------------|
| Frontend     | Angular 22, Angular Material, nginx                 |
| Backend      | Spring Boot 3.5.3, Spring Data JPA, JPA Specifications |
| Database     | PostgreSQL 18                                       |
| Migrations   | Liquibase (XML)                                     |
| Object map   | MapStruct 1.6.3, Lombok @Builder                    |
| Auth         | JJWT 0.12.6 (HMAC-SHA), BCrypt (spring-security-crypto) |
| Tests (BE)   | JUnit 5, Mockito, AssertJ, H2, MockMvc              |
| Tests (FE)   | Vitest, jsdom, Angular TestBed                      |
| Runtime      | JDK 22, Node 26                                     |

## Code quality

| Tool       | Purpose              | Command                    |
|------------|----------------------|----------------------------|
| Checkstyle | Style linting        | `mvn validate`             |
| PMD        | Code smell detection | `mvn verify`               |
| Spotless   | Java formatting      | `mvn spotless:apply`       |
| Prettier   | Frontend formatting  | `npm run format`           |
| ESLint     | Frontend linting     | `npm run lint`             |

## Liquibase conventions

- Changelogs in `db/changelogs/`, master at `db/db.changelog-master.xml`
- File format: `YYYY-MM-dd-<description>.xml`
- ChangeSet ID: `YYYY-MM-dd-<action>`
- Author: your name or handle
