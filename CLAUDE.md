# Database Manager

Full-stack skeleton: Angular 22 (Material) + Spring Boot 3.5 + PostgreSQL 18.

**Development workflow: run backend and frontend locally. Docker is for PostgreSQL only during development.** The full `docker compose up -d` is just for quick demos. When implementing features, always run the backend and frontend outside Docker for hot-reload and faster iteration.

## Running locally

```bash
# Database only via Docker
docker compose up -d postgres

# Backend (needs JDK 26 + Maven, or use Maven wrapper)
cd backend
export JAVA_HOME=~/jdk-26
./mvnw spring-boot:run
# Runs on http://localhost:8080

# Frontend (needs Node 26)
cd frontend
npm start
# Runs on http://localhost:4200, proxies /api to localhost:8080
```

For quick demo, `docker compose up -d` starts everything (nginx on :80, backend on :8080, postgres on :5432).

## Project structure

Backend uses **package-by-feature**, not package-by-layer:

```
backend/src/main/java/com/example/databasemanager/
├── DatabaseManagerApplication.java
├── config/
│   └── CorsConfig.java
└── task/
    ├── entity/Task.java
    ├── dto/TaskDto.java
    ├── repository/TaskRepository.java
    ├── mapper/TaskMapper.java
    └── controller/TaskController.java
```

New entities follow same pattern: `<name>/entity`, `<name>/dto`, `<name>/repository`, `<name>/mapper`, `<name>/service`, `<name>/controller`.

## Liquibase changelogs

- Location: `db/changelogs/`
- Master: `db/db.changelog-master.xml`
- File format: `YYYY-MM-dd-<whats-done>.xml`
- ChangeSet ID format: `YYYY-MM-dd-<action-description>`
- Author: your name or handle (e.g. `szombatibalazs`)

## Backend conventions

- Java 26 runtime, compiler targets `--release 25` (Spring Boot ASM compatibility)
- Lombok 1.18.40 with explicit annotation processor path
- MapStruct 1.6.3 for entity/DTO conversion (`componentModel = "spring"`)
- Checkstyle (Google-style) bound to `mvn validate`
- PMD (code smell detection) bound to `mvn verify`
- Spotless (Palantir Java Format) — run manually: `mvn spotless:apply`
- SpotBugs not added — incompatible with Java 26 bytecode (class file v70)
- Spring Data JPA repositories; no manual SQL

## Frontend conventions

- Angular 22 standalone components with signals
- Angular Material for UI
- Prettier for formatting (`npm run format`)
- ESLint with Angular rules (`npm run lint`)
- Zoneless change detection (no zone.js)
- nginx serves production build in Docker, proxies `/api/` to backend
