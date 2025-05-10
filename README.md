# Travel Temptation – Backend

This is the backend of **Travel Temptation**, a full-stack platform designed to connect travelers with verified travel agencies and help users securely plan and book their dream trips. The backend is built using Java Spring Boot and follows a layered architecture with a RESTful API.

## Features

- User registration and authentication (JWT-secured)
- Roles for regular users and travel agencies
- Agencies can list trips and excursions
- Users can browse, book, and manage trips
- Real-time updates via WebSockets
- CI/CD pipeline (GitLab + GitRunner)
- 80%+ test coverage of business logic
- Dockerized deployment
- Secure architecture aligned with OWASP guidelines

## Architecture

- Java 17 / Spring Boot
- PostgreSQL + JPA (ORM)
- RESTful API
- WebSocket integration
- Follows SOLID principles and SRP per component
- C1/C2/C3 architecture model applied

## Testing Strategy

- Unit tests for services (business logic)
- Integration tests for controllers
- End-to-end testing with Cypress (in frontend)
- Code quality via SonarQube in CI/CD pipeline

## Frontend Integration

This backend is consumed by a React-based frontend application:
👉 [Travel Temptation – Frontend](https://github.com/elena-min/travel-temptation-frontend)

To see the full functionality in action (including authentication, trip browsing, booking), please clone and run both the frontend and backend projects locally.


## Getting Started

1. **Clone the repo:**

```bash
git clone https://github.com/elena-min/travel-temptation-backend.git
cd travel-temptation-backend
