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

## Development workflow

Run backend and frontend **locally** for hot-reload. Docker is for PostgreSQL only.

```bash
# Database
docker compose up -d postgres

# Backend (JDK 26 + Maven required)
cd backend
export JAVA_HOME=~/jdk-26
./mvnw spring-boot:run         # http://localhost:8080

# Frontend (Node 26 required)
cd frontend
npm start                       # http://localhost:4200
```

## Project structure

Backend uses **package-by-feature**:

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

## Tech stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Frontend    | Angular 22, Angular Material, nginx |
| Backend     | Spring Boot 3.5.3, Spring Data JPA  |
| Database    | PostgreSQL 18                       |
| Migrations  | Liquibase (XML)                     |
| Object map | MapStruct 1.6.3                     |
| Runtime     | JDK 26, Node 26                     |

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
