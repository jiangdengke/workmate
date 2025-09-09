# Workmate Backend (Spring Boot / jOOQ)

> Clean, layered backend for **Todos / Notes / Reminders**. This repository contains **only the backend**. The React front‑end lives in a separate repo.

---

## Features

* ✅ **Todos**: create / toggle done / list / delete
* ✅ **Notes**: per‑user create / list / delete
* ✅ **Reminders**: per‑user create / list with status filter (`all|upcoming|due|sent`) / delete
* ⏱️ **Scheduler**: simple reminder dispatcher (logs by default; pluggable for email/SMS/IM)
* 🧰 **DX**: Flyway migrations, jOOQ codegen, Lombok constructor injection
* 🔐 **Auth**: JWT login/register; all data scoped to user

## API Redesign (Auth + Per-User Data)

We introduced a `users` table and moved `user_name` out of `notes`/`reminders` into a proper foreign key `user_id`. `todos` also now belong to a user.

- Auth
  - `POST /api/v1/auth/register` { userName, password, displayName?, email? } -> { token, user }
  - `POST /api/v1/auth/login` { userName, password } -> { token, user }
  - Use header `Authorization: Bearer <token>` for all requests below

- Todos
  - `POST /api/v1/todos` { title }
  - `GET /api/v1/todos?done=true|false&page=0&size=20`
  - `PATCH /api/v1/todos/{id}/done` { done }
  - `DELETE /api/v1/todos/{id}`

- Notes
  - `POST /api/v1/notes` { text }
  - `GET /api/v1/notes?page=0&size=20`
  - `DELETE /api/v1/notes/{id}`

- Reminders
  - `POST /api/v1/reminders` { text, atTime }
  - `GET /api/v1/reminders?status=all|upcoming|due|sent&page=0&size=20`
  - `DELETE /api/v1/reminders/{id}`

Config
- Add `JWT_SECRET` in `.env` (or set `app.jwt.secret`) for token signing
- DB schema lives at `src/main/resources/db/db.sql` (jOOQ codegen reads it)

## Tech Stack

* **JDK 21**, **Gradle (Kotlin DSL)**
* **Spring Boot 3** (Web, Validation, Scheduling)
* **jOOQ 3.19** (official Gradle plugin; generate from MySQL schema)
* **Flyway** for DB migrations
* **MySQL 8**


## Prerequisites

* JDK **21**
* MySQL **8** (local or remote)
* Gradle Wrapper (`./gradlew`)

## Environment

Create an `.env` at repo root (used by jOOQ codegen & Spring Boot):

```
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=workmate
DB_USER=root
DB_PASSWORD=root
```

## Quick Start

```bash
# 1) Generate jOOQ sources (runs Flyway first)
./gradlew clean generateJooq

# 2) Run the application
./gradlew bootRun
# → http://localhost:8080
```

## License

Choose and add a LICENSE file at the repo root (e.g. **MIT** or **Apache‑2.0**). See notes in the main discussion for how to generate quickly via GitHub UI or CLI.
