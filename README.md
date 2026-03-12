# BFF Web App

> A Spring Boot Backend-for-Frontend service for TalentOS web clients

## Overview

**BFF Web App** is a Spring Boot microservice that acts as the frontend-facing integration layer in the TalentOS ecosystem.

It exposes a unified API surface for UI clients and orchestrates calls to backend domain services such as User API, Curriculum API, and Domain API. The service applies authorization rules, handles JWT token lifecycle operations, and composes frontend-oriented view payloads.

## Key Features

- **Frontend-Oriented API Gateway**: Single entry point for web clients under a coherent `/api/bff-web-app` namespace
- **Authentication Flows**: Login, refresh token, and password reset endpoints for frontend auth workflows
- **JWT Middleware**: Request-level token validation and request context population
- **Role-Aware Authorization**: Access control for employee, manager, admin, and superadmin use cases
- **Service Orchestration**: Integrates User, Curriculum, and Domain microservices behind consistent response contracts
- **View Composition Endpoints**: Dedicated endpoints for `your-cv` and `your-employees` frontend pages
- **Redis Caching**: Cache support for frequently accessed domain, user, and curriculum data
- **Global Error Handling**: Standardized exception mapping with common response envelope
- **OpenAPI Documentation**: Swagger UI and OpenAPI endpoint for interactive API exploration
- **Profile-Based Configuration**: Environment-specific property files for dev/test/prod

## Technology Stack

| Component                 | Technology                     | Version |
| ------------------------- | ------------------------------ | ------- |
| **Framework**             | Spring Boot                    | 3.5.7   |
| **Language**              | Java                           | 25      |
| **Validation**            | Jakarta Bean Validation        | 3.5.7   |
| **Mapping**               | MapStruct                      | 1.6.3   |
| **Boilerplate Reduction** | Lombok                         | 1.18.42 |
| **Authentication**        | JJWT                           | 0.13.0  |
| **Caching**               | Spring Cache + Redis           | 3.5.7   |
| **API Documentation**     | SpringDoc OpenAPI              | 2.8.13  |
| **Database Driver**       | MySQL Connector/J (runtime)    | managed |
| **Build Tool**            | Maven Wrapper + Maven Compiler | 3.14.1  |

## Project Structure

```text
src/
├── main/
│   ├── java/com/certimetergroup/talentos/bffwebapp/
│   │   ├── BffwebappApplication.java               # Spring Boot entry point
│   │   ├── config/
│   │   │   ├── cache/RedisCacheConfig.java         # Redis cache manager and cache TTL setup
│   │   │   ├── cors/CorsConfig.java                # CORS configuration
│   │   │   ├── swagger/OpenApiConfig.java          # OpenAPI metadata configuration
│   │   │   └── web/RestTemplateConfig.java         # RestTemplate and HTTP client configuration
│   │   ├── context/RequestContext.java             # Request-scoped auth/user context
│   │   ├── controller/
│   │   │   ├── UserController.java                 # User endpoints proxied via BFF
│   │   │   ├── CurriculumController.java           # Curriculum endpoints proxied via BFF
│   │   │   ├── DomainController.java               # Domain endpoints proxied via BFF
│   │   │   ├── ExceptionController.java            # Global exception handling
│   │   │   └── view/                               # Frontend view-specific endpoints
│   │   ├── filter/JwtAuthenticationMiddleware.java # JWT validation filter
│   │   ├── restclient/                             # External API client adapters
│   │   ├── service/
│   │   │   ├── AuthorizationService.java           # Role-based authorization checks
│   │   │   ├── JwtService.java                     # Token generation/validation logic
│   │   │   ├── rest/                               # User/Curriculum/Domain API integration services
│   │   │   └── views/                              # View composition business logic
│   │   ├── mapper/                                 # Aggregation and mapping helpers
│   │   └── dto/                                    # Paged and view DTOs
│   └── resources/
│       ├── application.properties                  # Shared/base properties
│       ├── application-dev.properties              # Development profile
│       ├── application-prod.properties             # Production profile
│       └── application-test.properties             # Test profile
└── test/
	 └── java/com/certimetergroup/talentos/bffwebapp/
		  └── BffwebappApplicationTests.java          # Spring context test
```

## Getting Started

### Prerequisites

- **Java 25** or higher
- **Maven 3.6+** (or Maven Wrapper)
- **Redis** running locally or reachable from the app (`localhost:6379` by default)
- Access to internal artifact repository for `com.certimetergroup.talentos:commons`
- User API, Domain API, and Curriculum API available on expected base URLs (or reconfigured)

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd bff-web-app
   ```

2. **Set up required environment variables**

   ```bash
   export SPRING_PROFILES_ACTIVE=dev
   export PORT=8080
   export JWT_ACCESS_KEY="replace-with-secure-access-key"
   export JWT_REFRESH_KEY="replace-with-secure-refresh-key"
   ```

3. **Build the project**

   ```bash
   ./mvnw clean package
   ```

4. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

   Or run the packaged JAR:

   ```bash
   java -jar target/bffwebapp-0.0.1-SNAPSHOT.jar
   ```

The service starts on the configured `PORT` (for example, `8080`).

## API Usage

### Base URL

```text
http://localhost:8080/api/bff-web-app
```

### Response Contract

All endpoints return a standardized envelope:

- `responseEnum`: service outcome/status semantic
- `payload`: endpoint-specific data (or null)

### Example Endpoints

**Authentication**

```http
POST /api/bff-web-app/auth/login
POST /api/bff-web-app/auth/refresh
POST /api/bff-web-app/auth/reset
```

**Users**

```http
GET    /api/bff-web-app/users?page=1&pageSize=10&searchString=john
GET    /api/bff-web-app/users/{userId}
POST   /api/bff-web-app/users
PUT    /api/bff-web-app/users/{userId}
PATCH  /api/bff-web-app/users/{userId}
PATCH  /api/bff-web-app/users/password
DELETE /api/bff-web-app/users/{userId}
```

**Curriculums**

```http
GET    /api/bff-web-app/curriculums?page=1&pageSize=5
GET    /api/bff-web-app/curriculums/{curriculumId}
POST   /api/bff-web-app/curriculums
DELETE /api/bff-web-app/curriculums/{curriculumId}
```

**Domains**

```http
GET    /api/bff-web-app/domains?page=1&pageSize=5&domainName=cloud
GET    /api/bff-web-app/domains/{domainId}?domainOptionIds=18,19
POST   /api/bff-web-app/domains
PUT    /api/bff-web-app/domains/{domainId}
DELETE /api/bff-web-app/domains/{domainId}
```

**View Endpoints**

```http
GET  /api/bff-web-app/views/your-cv?curriculumId={id}
POST /api/bff-web-app/views/your-cv
PUT  /api/bff-web-app/views/your-cv?curriculumId={id}

GET  /api/bff-web-app/views/your-employees?page=1&pageSize=20
```

### API Documentation

Interactive API documentation is available at:

- **Swagger UI**: `http://localhost:8080/api/bff-web-app/docs/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/bff-web-app/docs`

## Configuration

### Environment Profiles

**Development** (`application-dev.properties`):

- Activates the `dev` profile

**Production** (`application-prod.properties`):

- Activates the `prod` profile

**Test** (`application-test.properties`):

- Activates the `test` profile

### Key Configuration Properties

```properties
# Application
spring.application.name=bffwebapp
server.port=${PORT}
spring.profiles.active=${SPRING_PROFILES_ACTIVE}

# JWT
security.jwt.issuer=talentos-bff-web-app
security.jwt.access.key=${JWT_ACCESS_KEY}
security.jwt.refresh.key=${JWT_REFRESH_KEY}
security.jwt.access-expiration-seconds=1800
security.jwt.refresh-expiration-minutes=5000

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=60m

# OpenAPI
springdoc.api-docs.path=/api/bff-web-app/docs
springdoc.swagger-ui.path=/api/bff-web-app/docs/swagger-ui.html

# CORS
cors.enable=true
allowed-cors-origin=http://localhost:4200
```

### Downstream Service Configuration

The BFF integrates with the following backend APIs via configurable endpoint properties:

- `user-api.endpoint.*` (default base URL: `http://localhost:8081/api/users`)
- `domain-api.endpoint.*` (default base URL: `http://localhost:8082`)
- `curriculum-api.endpoint.*` (default base URL: `http://localhost:8083`)

## Security and Authorization

- `JwtAuthenticationMiddleware` validates access tokens for protected routes
- Auth and docs paths are excluded from JWT middleware:
  - `/api/bff-web-app/auth/**`
  - `/api/bff-web-app/docs/**`
- Authorization rules are enforced in `AuthorizationService` based on user role and ownership/management relationships

Role behavior summary:

- **EMPLOYEE**: restricted to own profile/curriculum scope
- **MANAGER**: can access own scope and managed employee scope
- **ADMIN/SUPERADMIN**: full access for protected operations (including domain and user management)

## Error Handling

Global exception handling is provided via `ExceptionController` (`@RestControllerAdvice`).

Handled categories include:

- Business exceptions (`FailureException`)
- Validation errors (`MethodArgumentNotValidException`, `BindException`, `ConstraintViolationException`)
- Generic fallback (`Exception`)

Validation failures return `BAD_REQUEST` with a payload map of `field/path -> message`.

## Caching

Redis-backed caching is configured with per-cache TTL policies.

Examples:

- Domain-related caches: up to 30 days
- User/Curriculum-related caches: up to 24 hours
- Default cache TTL: 60 minutes

## Testing

Run tests with:

```bash
./mvnw test
```

The repository includes a Postman collection:

- `bff-api-text.postman_collection.json`

## Project Statistics

- **Language**: Java 25
- **Framework**: Spring Boot 3.5.7
- **Build System**: Maven (Wrapper included)
- **Caching**: Redis
- **API Documentation**: OpenAPI 3 / Swagger UI
- **Architecture Role**: Backend-for-Frontend orchestration service

## Dependencies Management

Dependencies are managed via Maven in `pom.xml`.

Core dependencies include:

- Spring Boot starters: Web, Validation, Data Redis, Cache, Test
- JJWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- MapStruct + annotation processor
- Lombok + Lombok-MapStruct binding
- SpringDoc OpenAPI
- Shared TalentOS commons module

Check dependency resolution/updates with:

```bash
./mvnw dependency:resolve
./mvnw versions:display-dependency-updates
```

## Development Guidelines

### Code Structure

- **Controllers**: Define BFF endpoint contracts for UI-facing operations
- **Rest Services/Clients**: Encapsulate communication with downstream microservices
- **View Services**: Compose cross-service payloads for frontend pages
- **Authorization Service**: Centralize role and ownership checks
- **Filter/Context**: Validate JWTs and propagate authenticated request context

### Best Practices

- Keep orchestration logic in services, not controllers
- Preserve the shared response envelope contract across endpoints
- Validate all incoming path/query/body inputs
- Ensure role-based checks remain centralized in authorization service methods
- Update this README whenever endpoints, auth behavior, or config keys change

## Contributing

When contributing to this project:

1. Follow the existing package structure and naming conventions.
2. Keep authorization checks explicit for new protected routes.
3. Keep orchestration logic deterministic and rollback-safe where multiple services are involved.
4. Add or update tests when changing request validation, auth logic, or integration flows.
5. Update this README when introducing new endpoints or configuration changes.

## License

MIT License. See [LICENSE](LICENSE) for details.

---

**Developed by Giuseppe Falcone**
