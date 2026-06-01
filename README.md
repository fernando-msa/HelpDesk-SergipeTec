# HelpDesk-SergipeTec

> Technical support ticket management system built with Jakarta EE 10, JWT authentication, and JPA/Hibernate persistence.

![Java 25](https://img.shields.io/badge/Java-25_LTS-007396?logo=openjdk&logoColor=white)
![Maven 3.9.15](https://img.shields.io/badge/Maven-3.9.15-C71A36?logo=apachemaven&logoColor=white)
![Jakarta EE 10](https://img.shields.io/badge/Jakarta_EE-10.0.0-1E90FF?logo=jakartaee&logoColor=white)
![CI](https://github.com/fernando-msa/HelpDesk-SergipeTec/actions/workflows/ci.yml/badge.svg?branch=main)
![License MIT](https://img.shields.io/badge/License-MIT-4C1?logo=opensourceinitiative&logoColor=white)

---

## Features

- **JWT Authentication** -- HMAC-SHA256 signed tokens with configurable expiration
- **Ticket Lifecycle** -- Create, update status (Open / In Progress / Closed), and close tickets
- **Notification System** -- Automatic notifications for ticket events
- **RESTful API** -- JSON-based endpoints with input validation
- **Responsive UI** -- HTML5/CSS3/JavaScript frontend
- **PBKDF2 Password Hashing** -- Secure credential storage with constant-time comparison

## Tech Stack

| Component        | Version / Details                          |
|------------------|--------------------------------------------|
| **Java**         | 25 LTS (Eclipse Adoptium)                  |
| **Maven**        | 3.9.15                                     |
| **Jakarta EE**   | 10.0.0 (JAX-RS, JPA, EJB, CDI)            |
| **Hibernate ORM**| 6.2.7.Final                                |
| **PostgreSQL**   | JDBC 42.7.11                               |
| **JWT**          | JJWT 0.11.5 (HMAC-SHA256)                  |
| **JUnit**        | 5.10.0 (Jupiter)                           |
| **H2 Database**  | 2.2.220 (test scope)                       |

## Architecture

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────────┐     ┌────────────┐
│   Frontend   │────>│   JAX-RS API     │────>│   JPA/Hibernate  │────>│ PostgreSQL │
│  HTML/CSS/JS │<────│   (Resources)    │<────│   (Entities)     │<────│            │
└──────────────┘     └──────────────────┘     └──────────────────┘     └────────────┘
                            │
                     ┌──────┴──────┐
                     │  JwtFilter  │
                     │  (Auth)     │
                     └─────────────┘
```

**Request flow:** Client sends HTTP request -> `JwtFilter` validates the Bearer token -> JAX-RS Resource handles business logic -> JPA EntityManager persists/queries entities -> JSON response returned to client.

## Getting Started

### Prerequisites

- JDK 25+ (Eclipse Adoptium recommended)
- Maven 3.9+
- PostgreSQL 15+ (or use the demo server for local testing without a database)

### Installation

```bash
git clone https://github.com/fernando-msa/HelpDesk-SergipeTec.git
cd HelpDesk-SergipeTec
```

### Environment Configuration

Create a `.env` file (see `.env.example`):

```env
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://localhost:5432/helpdeskdb
DB_USER=helpdesk_user
DB_PASSWORD=your_password_here
JWT_SECRET=$(openssl rand -base64 48)
```

### Build and Run

```bash
# Build
mvn clean package

# Deploy WAR to Jakarta EE server (Payara, WildFly, etc.)
cp target/helpdesk-app-0.1.0.war $PAYARA_HOME/domains/domain1/autodeploy/
```

### Quick Demo (No Database Required)

```bash
node demo-server.js
# Open http://localhost:3000
```

## API Reference

All endpoints (except login) require `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint           | Body                                      | Response         |
|--------|--------------------|-------------------------------------------|------------------|
| POST   | `/api/auth/login`  | `{"username": "admin", "password": "..."}` | `{"token": "..."}` |

### Tickets

| Method | Endpoint                    | Body                                          | Description              |
|--------|-----------------------------|-----------------------------------------------|--------------------------|
| GET    | `/api/tickets`              | --                                            | List all tickets         |
| GET    | `/api/tickets/{id}`         | --                                            | Get ticket by ID         |
| POST   | `/api/tickets`              | `{"title": "...", "description": "..."}`       | Create new ticket        |
| PUT    | `/api/tickets/{id}/status`  | `{"status": "IN_PROGRESS"}`                   | Update ticket status     |
| POST   | `/api/tickets/{id}/close`   | --                                            | Close ticket             |

### Notifications

| Method | Endpoint                       | Description            |
|--------|--------------------------------|------------------------|
| GET    | `/api/notifications`           | List all notifications |
| POST   | `/api/notifications/{id}/read` | Mark as read           |

### Status Values

`OPEN` | `IN_PROGRESS` | `CLOSED`

## Testing

```bash
# Run all tests (uses H2 in-memory database, no PostgreSQL needed)
mvn clean test
```

Tests use an H2 in-memory database via a dedicated `persistence.xml` in `src/test/resources`. No external database setup is required.

**Test coverage:**

| Test Class             | Scope                                       |
|------------------------|---------------------------------------------|
| `JwtUtilTest`          | Token generation, parsing, expiration, tampering |
| `TicketJpaTest`        | Entity CRUD, status transitions, timestamps |
| `NotificationJpaTest`  | Entity CRUD, mark-as-read, type variants    |

## Project Structure

```
backend/src/main/java/com/helpdesk/
├── api/
│   ├── AuthResource.java           # POST /api/auth/login
│   ├── TicketResource.java         # Ticket CRUD endpoints
│   ├── NotificationResource.java   # Notification endpoints
│   └── GlobalExceptionMapper.java  # Centralized error handling
├── model/
│   ├── Ticket.java                 # JPA entity (tickets table)
│   ├── Notification.java           # JPA entity (notifications table)
│   └── TicketStatus.java           # Enum: OPEN, IN_PROGRESS, CLOSED
└── security/
    ├── JwtUtil.java                # JWT generation and parsing
    ├── JwtFilter.java              # Request filter for authentication
    └── CorsFilter.java             # CORS configuration

frontend/
├── index.html                      # Dashboard
├── login.html                      # Login page
├── app.js                          # Frontend logic
└── styles.css                      # Responsive styles
```

## Security

- **JWT HMAC-SHA256** -- Tokens signed with a secret key; the application fails to start if `JWT_SECRET` is not configured
- **PBKDF2 Password Hashing** -- Passwords hashed with PBKDF2WithHmacSHA256 (65536 iterations, 256-bit key) with constant-time comparison
- **CORS** -- Configurable origin via `CORS_ORIGIN` environment variable
- **Input Validation** -- `@Column` constraints on entities, API-level validation on required fields
- **Global Exception Handling** -- `ExceptionMapper` prevents stack trace leakage in production
- **Environment-Based Configuration** -- All secrets managed via environment variables, no hardcoded credentials in source

## License

MIT License -- see [LICENSE](LICENSE) for details.

## Author

**Fernando S. De Santana Junior**
[GitHub](https://github.com/fernando-msa) | [LinkedIn](https://www.linkedin.com/in/fernando-msa/)
