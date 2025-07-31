# User Management API

A Spring Boot application for user management with PostgreSQL database.

## Features

- Create, read, update, and delete users
- Search users by ID, username, or email
- Input validation
- Exception handling
- Comprehensive test coverage

## Technologies

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL (with Docker)
- JUnit 5 & Mockito for testing
- Maven for dependency management

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/usermanagement/api/
│   │       ├── controller/       # REST controllers
│   │       ├── dto/              # Data Transfer Objects
│   │       ├── exception/        # Custom exceptions and handlers
│   │       ├── model/            # Entity classes
│   │       ├── repository/       # Data access layer
│   │       ├── service/          # Business logic
│   │       └── UserManagementApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/
    │   └── com/usermanagement/api/
    │       ├── controller/       # Controller tests
    │       ├── integration/      # Integration tests
    │       ├── repository/       # Repository tests
    │       └── service/          # Service tests
    └── resources/
        └── application-test.properties
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven

### Running the Application

1. Clone the repository

2. Start the PostgreSQL database using Docker Compose:

```bash
docker-compose up -d
```

3. Build and run the application:

```bash
./mvnw spring-boot:run
```

The application will be available at http://localhost:8080

### Running Tests

To run the tests:

```bash
./mvnw test
```

## API Endpoints

### Users

- `POST /api/users` - Create a new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `GET /api/users/email/{email}` - Get user by email
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Sample Requests

### Create User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "username": "johndoe",
    "password": "password123"
  }'
```

### Get User by ID

```bash
curl -X GET http://localhost:8080/api/users/1
```

### Update User

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John Updated",
    "lastName": "Doe Updated",
    "email": "john.updated@example.com"
  }'
```

### Delete User

```bash
curl -X DELETE http://localhost:8080/api/users/1
```