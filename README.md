# RideShare Backend

A mini ride-sharing backend application built with Spring Boot and MongoDB, featuring JWT authentication, input validation, and clean architecture.

## Features

- **User Authentication**: Register and login with JWT token-based authentication
- **Role-Based Access Control**: Separate endpoints for passengers (ROLE_USER) and drivers (ROLE_DRIVER)
- **Ride Management**: Create, accept, and complete rides
- **Input Validation**: Jakarta Bean Validation with custom error messages
- **Global Exception Handling**: Centralized error handling with meaningful responses

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data MongoDB
- Lombok
- Maven

## Project Structure

```
src/main/java/com/rideshare/
├── config/          # Security and JWT configuration
├── controller/      # REST API controllers
├── dto/             # Data Transfer Objects
├── exception/       # Custom exceptions and global handler
├── model/           # Entity classes
├── repository/      # MongoDB repositories
├── service/         # Business logic
└── util/            # Utility classes (JWT)
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB running on localhost:27017

## Setup & Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd rideshare-backend
   ```

2. **Start MongoDB**
   ```bash
   # Using Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest

   # Or start your local MongoDB service
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The server will start at `http://localhost:8080`

## API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Passenger Endpoints (ROLE_USER)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/rides` | Create a new ride request |
| GET | `/api/v1/user/rides` | Get all rides for current user |
| POST | `/api/v1/rides/{rideId}/complete` | Complete a ride |

### Driver Endpoints (ROLE_DRIVER)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/driver/rides/requests` | Get all pending ride requests |
| POST | `/api/v1/driver/rides/{rideId}/accept` | Accept a ride |
| POST | `/api/v1/rides/{rideId}/complete` | Complete a ride |

## API Usage Examples

### 1. Register a User (Passenger)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_passenger",
    "password": "password123",
    "role": "ROLE_USER"
  }'
```

### 2. Register a Driver

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jane_driver",
    "password": "password123",
    "role": "ROLE_DRIVER"
  }'
```

### 3. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_passenger",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "username": "john_passenger",
  "role": "ROLE_USER",
  "message": "Login successful"
}
```

### 4. Create a Ride (Passenger)

```bash
curl -X POST http://localhost:8080/api/v1/rides \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{
    "pickupLocation": "123 Main Street",
    "dropLocation": "456 Oak Avenue"
  }'
```

### 5. View Pending Rides (Driver)

```bash
curl -X GET http://localhost:8080/api/v1/driver/rides/requests \
  -H "Authorization: Bearer <driver_jwt_token>"
```

### 6. Accept a Ride (Driver)

```bash
curl -X POST http://localhost:8080/api/v1/driver/rides/{rideId}/accept \
  -H "Authorization: Bearer <driver_jwt_token>"
```

### 7. Complete a Ride

```bash
curl -X POST http://localhost:8080/api/v1/rides/{rideId}/complete \
  -H "Authorization: Bearer <jwt_token>"
```

## Request/Response DTOs

### RegisterRequest
```json
{
  "username": "string (3-50 chars, required)",
  "password": "string (6-100 chars, required)",
  "role": "ROLE_USER | ROLE_DRIVER (required)"
}
```

### LoginRequest
```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

### RideRequest
```json
{
  "pickupLocation": "string (3-200 chars, required)",
  "dropLocation": "string (3-200 chars, required)"
}
```

## Ride Status Flow

```
REQUESTED → ACCEPTED → COMPLETED
```

- **REQUESTED**: Initial status when passenger creates a ride
- **ACCEPTED**: Driver has accepted the ride
- **COMPLETED**: Ride has been completed (by passenger or driver)

## Error Handling

The API returns consistent error responses:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descriptive error message",
  "details": ["Validation error 1", "Validation error 2"],
  "path": "/api/endpoint",
  "timestamp": "2024-01-01T12:00:00"
}
```

## Configuration

Key configuration in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `spring.data.mongodb.uri` | MongoDB connection URI | mongodb://localhost:27017/rideshare |
| `jwt.secret` | JWT signing secret | (configured) |
| `jwt.expiration` | Token expiration in ms | 86400000 (24 hours) |

## Security

- Passwords are encrypted using BCrypt
- JWT tokens contain username, role, issuedAt, and expiry
- Tokens must be sent in the `Authorization` header as `Bearer <token>`
- Role-based access control protects endpoints

## Author

Piyush Bansal
