# Database Manager

Full-stack skeleton: Angular 22 (Material) + Spring Boot 3.5 + PostgreSQL 18.

**Language: agents always respond in English. Internal reasoning may use any language (e.g. Chinese).**

**Development workflow: run backend and frontend locally. Docker is for PostgreSQL only during development.** The full `docker compose up -d` is just for quick demos. When implementing features, always run the backend and frontend outside Docker for hot-reload and faster iteration.

## Running locally

```bash
# Database only via Docker
docker compose up -d postgres

# Backend (needs JDK 22 + Maven)
cd backend
export JAVA_HOME=~/jdk-22
mvn spring-boot:run
# Runs on http://localhost:8080

# Frontend (needs Node 26)
cd frontend
npm start
# Runs on http://localhost:4200, proxies /api to localhost:8080
```

For quick demo, `docker compose up -d` starts everything (nginx on :80, backend on :8080, postgres on :5432). Default admin: `admin` / `1234`.

## Project structure

Backend uses **package-by-feature**, not package-by-layer:

```
backend/src/main/java/com/example/databasemanager/
├── DatabaseManagerApplication.java
├── common/
│   ├── AbstractModel.java            # @MappedSuperclass: id, created, updated
│   ├── filter/                       # generic JSON-based filter framework
│   │   ├── FilterOperation.java       # enum: LIKE, EQUALS, DATE_RANGE
│   │   ├── FilterCriterion.java       # single criterion: operation, field, value
│   │   ├── FilterRequest.java         # JSON wrapper: {"filters": [...]}
│   │   ├── FilterSpecificationBuilder.java  # builds JPA Specification<T> from criteria
│   │   └── InvalidFilterException.java      # 400 when field name or value type is wrong
│   │   └── DateRange.java            # record(from, to)
│   └── exception/                    # global error handling
│       ├── GlobalExceptionHandler.java # @ControllerAdvice
│       ├── DuplicateResourceException.java  # throw for duplicate username/email
│       └── ErrorResponse.java        # record(status, message, timestamp)
├── config/
│   ├── CorsConfig.java
│   ├── FilterConfig.java             # JwtFilter + QueryMethodFilter registration (order: 0 query, 1 jwt)
│   └── QueryMethodFilter.java        # rewrites QUERY /api/users → POST /api/users/search
├── security/
│   ├── JwtUtil.java                  # HMAC-SHA JWT generation/validation
│   └── JwtFilter.java                # OncePerRequestFilter: Bearer token guard, public: /api/auth/login
├── task/
│   ├── entity/Task.java
│   ├── dto/TaskDto.java              # @Builder
│   ├── repository/TaskRepository.java
│   ├── mapper/TaskMapper.java
│   └── controller/TaskController.java
├── user/
│   ├── entity/User.java              # extends AbstractModel, @Table(name = "\"users\"")
│   ├── dto/
│   │   ├── UserDto.java              # @Builder, response (no password)
│   │   ├── CreateUserRequest.java    # @Builder, @Valid, includes password
│   ├── repository/UserRepository.java # JpaRepository + JpaSpecificationExecutor
│   ├── mapper/UserMapper.java
│   ├── service/UserService.java      # interface: getAllUsers, queryUsers, createUser, deleteUser
│   ├── service/UserServiceImpl.java  # BCrypt, uniqueness check, pagination, filtering
│   └── controller/UserController.java # GET, POST, DELETE, POST /search
└── auth/
    ├── controller/AuthController.java  # POST /api/auth/login (public)
    ├── dto/LoginRequest.java           # @Builder, @Valid
    ├── dto/LoginResponse.java          # success, message, token (nullable, @JsonInclude NON_NULL)
    └── service/AuthService.java, AuthServiceImpl.java  # BCrypt verify + JWT generation
```

Test structure mirrors the same split:

```
backend/src/test/java/com/example/databasemanager/
├── unit/                             # no Spring context, Mockito
│   ├── security/JwtUtilTest.java
│   ├── user/service/UserServiceImplTest.java
│   └── auth/service/AuthServiceImplTest.java
└── integration/                      # Spring context, H2 in-memory DB
    ├── user/
    │   ├── UserRepositoryTest.java   # @DataJpaTest
    │   └── UserControllerTest.java   # @SpringBootTest + MockMvc
    └── auth/
        └── AuthControllerTest.java   # @SpringBootTest + MockMvc
```

Frontend mirrors same **package-by-feature** pattern:

```
frontend/src/app/
├── app.ts, app.html, app.css         # nav shell: toolbar, router-outlet
├── app.config.ts                     # providers: router, HTTP with interceptors
├── app.routes.ts                     # / → /tasks (guarded), /users (guarded), /login (public)
├── task/
│   ├── task.ts                       # Task interface
│   ├── task.service.ts               # HttpClient, /api/tasks
│   └── task.component.ts             # standalone, signals, loading/empty/data, snackbar errors
├── user/
│   ├── user.ts                       # User interface
│   ├── user.service.ts               # HttpClient, pagination params (page, size, sort, order)
│   ├── user.component.ts             # MatTable + MatPaginator + MatSort, server-side paging
│   └── user-dialog.component.ts      # MatDialog create-user form, inline error display
└── auth/
    ├── auth.service.ts               # login/logout, JWT management, expiry check
    ├── auth.guard.ts                  # CanActivateFn, redirect → /login
    ├── auth.interceptor.ts           # HttpInterceptorFn, Bearer header, 401 → /login
    └── login/
        └── login.component.ts        # standalone, Material form, snackbar errors
```

New entities follow same pattern: `<name>/entity`, `<name>/dto`, `<name>/repository`, `<name>/mapper`, `<name>/service`, `<name>/controller`.

## Liquibase changelogs

- Location: `db/changelogs/`
- Master: `db/db.changelog-master.xml`
- File format: `YYYY-MM-dd-<whats-done>.xml`
- ChangeSet ID format: `YYYY-MM-dd-<action-description>`
- Author: your name or handle (e.g. `szombatibalazs`)

## Filtering

Generic JSON-based filter format. Clients send a `FilterRequest` with a list of `FilterCriterion` objects. `FilterSpecificationBuilder` validates field names against the entity class hierarchy and builds a JPA `Specification<T>` with AND semantics.

**Request format:**

```json
{
  "filters": [
    {"operation": "LIKE", "field": "userName", "value": "john"},
    {"operation": "EQUALS", "field": "email", "value": "x@example.com"},
    {"operation": "DATE_RANGE", "field": "created", "value": {"from": 1672531200000, "to": 1704067199999}}
  ]
}
```

Operations: `LIKE` (case-insensitive partial match, escapes `\`, `%`, `_`), `EQUALS` (exact equality), `DATE_RANGE` (epoch-millis from/to, UTC start-of-day inclusive). All fields optional; empty/null filters returns all records.

Field names must match entity attribute names (including inherited fields from `AbstractModel`: `id`, `created`, `updated`). Invalid field names throw `InvalidFilterException` → HTTP 400.

**Using filters in a controller:**

```java
@PostMapping("/search")
public Page<TaskDto> searchTasks(
    @RequestBody(required = false) FilterRequest filterRequest,
    @PageableDefault(size = 10, sort = "title") Pageable pageable) {
    FilterRequest fr = filterRequest != null ? filterRequest : new FilterRequest();
    return taskService.queryTasks(fr, pageable);
}
```

Service calls `FilterSpecificationBuilder.build(Entity.class, criteria)` and passes the result to `repository.findAll(spec, pageable)`.

## Backend conventions

- Java 22 runtime, compiler targets `--release 22`
- Lombok 1.18.40 must be used everywhere: `@Getter`, `@Setter`, `@Builder` on DTOs, `@NoArgsConstructor` on entities and Jackson-deserialized DTOs, `@RequiredArgsConstructor` on Spring beans. Never write manual getters/setters/constructors that Lombok can generate.
- Javadoc is mandatory on all public classes and public methods. Use `@param` and `@return` tags. Keep it concise — one sentence for classes, one for methods. No Javadoc on private methods.
- MapStruct 1.6.3 for entity/DTO conversion (`componentModel = "spring"`)
- Checkstyle (Google-style) bound to `mvn validate`
- PMD (code smell detection) bound to `mvn verify`
- Spotless (Palantir Java Format) — run manually: `mvn spotless:apply`
- SpotBugs not added — incompatible with Java 26 bytecode (class file v70)
- Spring Data JPA repositories; no manual SQL
- Service layer uses **interface + implementation** pattern (`UserService` / `UserServiceImpl`)
- Controllers inject service interfaces, never repositories or mappers directly
- List endpoints use Spring Data pagination (`Pageable` + `Page<T>`, default size=10, sort=userName ASC)
- Shared entity fields (`id`, `created`, `updated`) go in `AbstractModel` (`@MappedSuperclass`)
- JWT auth: `JwtUtil` generates/validates HMAC-SHA tokens, `JwtFilter` guards `/api/*`, public paths: `/api/auth/login`
- BCrypt password encoding via `spring-security-crypto` (not full Spring Security)
- Filter endpoints: `POST /api/<entity>/search` with `@RequestBody FilterRequest`. The `FilterSpecificationBuilder` builds a JPA `Specification<T>` from the criteria list. No per-entity filter DTO needed.
- `QueryMethodFilter` rewrites RFC 10008 `QUERY /api/users` → `POST /api/users/search` transparently
- Error handling: throw `DuplicateResourceException` for uniqueness violations → `@ControllerAdvice` maps to 409. `EntityNotFoundException` → 404. `DataIntegrityViolationException` → 409 fallback. All errors return `{"status","message","timestamp"}` JSON
- Never catch and log expected errors — let `@ControllerAdvice` handle them
- Tests use Gson autowired in `@SpringBootTest`, single `new Gson()` in Mockito-only tests
- ByteBuddy javaagent required in surefire `<argLine>` for Mockito on JDK 22 Windows
- No Maven wrapper (mvnw) — use system `mvn` with `JAVA_HOME=~/jdk-22`
- Dates/datetimes in API DTOs use Unix epoch milliseconds (`long`/`Long`). Entities use `LocalDateTime` internally. `DateTimeMapper` handles conversion in MapStruct mappers. Date filters accept epoch millis for date-only values (UTC start-of-day).

## Frontend conventions

- Angular 22 standalone components with signals
- Angular Material for UI
- Prettier for formatting (`npm run format`)
- ESLint with Angular rules (`npm run lint`)
- Vitest for unit testing (`npm test`)
- Zoneless change detection (no zone.js)
- JSDoc is mandatory on all exported classes, functions, and interfaces. Use `@param` and `@returns` tags. Keep it concise. Interfaces get a single-line description; public methods get `@param`/`@returns`.
- Feature-based directory structure (`task/`, `user/`, `auth/`) mirroring backend package-by-feature
- `inject()` for dependency injection in components; constructor injection in services
- Three-state UI: loading spinner, empty message, data list (@if/@else/@for)
- HTTP interceptor (`auth.interceptor.ts`) attaches JWT Bearer token to `/api` requests, redirects to `/login` on 401
- Route guard (`auth.guard.ts`) prevents unauthenticated access
- JWT token stored in localStorage, expiry checked at bootstrap via `isTokenExpired()`
- Server-side pagination: `MatPaginator` + `MatSort` with `page/size/sort` query params
- Create/edit forms use `MatDialog` overlay; list views use `MatTable`
- All backend fetch errors shown via `MatSnackBar` (toast) with backend error message
- Logout clears token AND navigates to `/login`
- nginx serves production build in Docker, proxies `/api/` to backend
