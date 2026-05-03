# 🏥 Veyra — Nursing Home Management Backend

**Veyra** is a backend system designed to manage daily operations of nursing homes or assisted living facilities — including residents, staff, activities, and health tracking.  
Built with **Java 25**, **Spring Boot**, **MySQL**, and **Swagger** for REST API documentation.


## 🩺 Overview

**Veyra** provides a clean and maintainable backend for managing:
- Resident profiles & medical histories, etc
- Activities and schedules
- Attendance tracking
- Authentication JWT
- Integration with frontends or mobile apps through RESTful endpoints

The project follows **Domain-Driven Design (DDD)** principles, separating business logic into bounded contexts like:
`NursingHome`, `Residents`, `Activities`, `Staff`.

---

## ⚙️ Tech Stack

| Component | Technology |
|------------|--------|
| **Language** | Java 25 |
| **Framework** | Spring Boot (Web, Data JPA, Validation, Security optional) |
| **Database** | MySQL 8.x |
| **Documentation** | Swagger / OpenAPI (springdoc-openapi) |
| **Build Tool** | Maven |
| **Migrations** | Flyway |
| **Containerization (optional)** | Docker, docker-compose |

---

## 🌟 Main Features

- CRUD operations for residents, staff, and activities
- Automatic linking of activities to resident history when attended
- Authentication & role-based access control
- Swagger UI for exploring all API endpoints
- Health and monitoring endpoints
- Clean domain-driven architecture for scalability

---

## 🚀 Project Setup

### 🧩 Prerequisites

- **JDK 25** installed (`JAVA_HOME` configured)
- **Maven** installed
- **MySQL** database available

---
