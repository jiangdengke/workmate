# Workmate Backend (Spring Boot / jOOQ)

> Clean, layered backend for **Todos / Notes / Reminders**. This repository contains **only the backend**. The React front‑end lives in a separate repo.

---

## Features

* ✅ **Todos**: create / toggle done / list / delete
* ✅ **Notes**: per‑user create / list / delete
* ✅ **Reminders**: per‑user create / list with status filter (`all|upcoming|due|sent`) / delete
* ⏱️ **Scheduler**: simple reminder dispatcher (logs by default; pluggable for email/SMS/IM)
* 🧰 **DX**: Flyway migrations, jOOQ codegen, Lombok constructor injection

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
