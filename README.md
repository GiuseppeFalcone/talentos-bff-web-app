# TalentOS BFF Web App

Backend for Frontend (BFF) service for the TalentOS project, providing aggregated endpoints and view-specific data transformations for the web frontend application.

## Description

This Spring Boot application serves as the Backend for Frontend layer for the TalentOS ecosystem. It acts as an intermediary between the frontend web application and multiple backend microservices (User API, Curriculum API, and Domain API), providing:

- **View-specific endpoints**: Aggregated data tailored for specific frontend views (Your CV, Your Employees)
- **Authentication & Authorization**: JWT-based authentication with access and refresh token management
- **API Gateway functionality**: Proxy endpoints for direct backend service communication
- **Caching**: Redis-based caching for improved performance
- **API Documentation**: Interactive Swagger UI documentation

## Features

- 🔐 **Authentication Service**: Login, token refresh, and password reset functionality
- 📄 **Your CV View**: Aggregated curriculum and user data for CV management
- 👥 **Your Employee View**: Employee listing and management with pagination
- 🚀 **Proxy Endpoints**: Direct pass-through to User, Curriculum, and Domain APIs
- 📚 **OpenAPI Documentation**: Automatically generated API documentation via SpringDoc
- ⚡ **Redis Caching**: Performance optimization through distributed caching
- 🔒 **JWT Security**: Secure authentication with access and refresh tokens

## Technology Stack

- **Java 25**
- **Spring Boot 3.5.7**
- **Spring Web** - RESTful web services
- **Spring Boot Validation** - Request validation
- **Spring Data Redis** - Caching layer
- **MySQL Connector** - Database connectivity
- **Lombok** - Boilerplate code reduction
- **MapStruct** - Object mapping
- **JWT (jjwt)** - JSON Web Token implementation
- **SpringDoc OpenAPI** - API documentation
- **Maven** - Build and dependency management

## Prerequisites

Before running this application, ensure you have:

- **Java 25** or higher installed
- **Maven 3.6+** installed
- **Redis Server** running (default: localhost:6379)
- **MySQL Database** accessible
- Access to the following backend services:
  - User API (default: localhost:8081)
  - Domain API (default: localhost:8082)
  - Curriculum API (default: localhost:8083)

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/GiuseppeFalcone/talentos-bff-web-app.git
   cd talentos-bff-web-app
   ```

2. **Configure environment variables**
   
   Copy the example environment file and configure your settings:
   ```bash
   cp src/main/resources/.env.example .env
   ```
   
   Set the following environment variables:
   - `SPRING_PROFILES_ACTIVE` - Active profile (dev, test, or prod)
   - `PORT` - Application port number
   - `JWT_ACCESS_KEY` - Secret key for access token generation
   - `JWT_REFRESH_KEY` - Secret key for refresh token generation

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

## Configuration

The application uses Spring profiles for environment-specific configuration. Configuration files are located in `src/main/resources/`:

- `application.properties` - Main configuration
- `application-dev.properties` - Development environment
- `application-test.properties` - Test environment
- `application-prod.properties` - Production environment

### Key Configuration Properties

**Server Configuration:**
```properties
server.port=${PORT}
spring.application.name=bffwebapp
```

**Redis Configuration:**
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.redis.time-to-live=60m
```

**JWT Configuration:**
```properties
security.jwt.access-expiration-seconds=1800
security.jwt.refresh-expiration-minutes=5000
```

**Backend Service URLs:**
- User API: `http://localhost:8081/api/users`
- Curriculum API: `http://localhost:8083/api/curriculums`
- Domain API: `http://localhost:8082/api/domains`

## Running the Application

### Using Maven

```bash
# Development mode
export SPRING_PROFILES_ACTIVE=dev
export PORT=8080
export JWT_ACCESS_KEY=your_access_key
export JWT_REFRESH_KEY=your_refresh_key

./mvnw spring-boot:run
```

### Using Java

```bash
./mvnw clean package
java -jar target/bffwebapp-0.0.1-SNAPSHOT.jar
```

The application will start on the configured port (default: 8080).

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/api/bff-web-app/docs/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/bff-web-app/docs

## API Endpoints

### Authentication
- `POST /api/bff-web-app/auth/login` - User login
- `POST /api/bff-web-app/auth/refresh` - Refresh access token
- `POST /api/bff-web-app/auth/reset` - Reset password

### Your CV View
- `GET /api/bff-web-app/views/your-cv` - Get curriculum details
- `POST /api/bff-web-app/views/your-cv` - Create new curriculum
- `PUT /api/bff-web-app/views/your-cv` - Update curriculum data

### Your Employee View
- `GET /api/bff-web-app/views/your-employee` - Get paginated employee list

### Proxy Endpoints
- `/api/bff-web-app/users/**` - User API proxy
- `/api/bff-web-app/curriculums/**` - Curriculum API proxy
- `/api/bff-web-app/domains/**` - Domain API proxy

## Testing

The application includes a Postman collection for API testing:

```bash
# Import the collection
bff-api-text.postman_collection.json
```

Run tests using Maven:
```bash
./mvnw test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/certimetergroup/talentos/bffwebapp/
│   │   ├── controller/           # REST controllers
│   │   │   ├── view/             # View-specific controllers
│   │   │   └── ...               # Proxy controllers
│   │   ├── service/              # Business logic
│   │   │   ├── rest/             # Backend API client services
│   │   │   └── views/            # View aggregation services
│   │   ├── dto/                  # Data transfer objects
│   │   ├── mapper/               # MapStruct mappers
│   │   ├── context/              # Request context
│   │   ├── restclient/           # REST client configurations
│   │   ├── errorhandler/         # Error handling
│   │   └── utility/              # Utility classes
│   └── resources/
│       ├── application.properties
│       ├── application-{profile}.properties
│       └── .env.example
└── test/                         # Test files
```

## Development

### Code Style
- Uses Lombok for reducing boilerplate code
- MapStruct for type-safe object mapping
- Spring Boot validation for request validation
- RESTful API design principles

### Building
```bash
./mvnw clean compile
```

### Packaging
```bash
./mvnw clean package
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is part of the TalentOS ecosystem developed by Certimeter Group.

## Authors

- Giuseppe Falcone - Initial development

## Support

For issues, questions, or contributions, please contact the development team or open an issue in the repository.
