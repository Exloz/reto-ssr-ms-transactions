# ms-transactions Service

This is the ms-transactions microservice for managing Cuenta and Movimientos entities using reactive programming.

## Setup

1. Start PostgreSQL and Kafka using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## API Documentation

Swagger UI is available at: http://localhost:8081/swagger-ui.html

## Endpoints

- **GET /cuentas** - Get all cuentas
- **GET /cuentas/{cuentaId}** - Get cuenta by ID
- **POST /cuentas** - Create a new cuenta
- **PUT /cuentas/{cuentaId}** - Update cuenta
- **DELETE /cuentas/{cuentaId}** - Delete cuenta

- **GET /movimientos** - Get all movimientos
- **POST /movimientos** - Create a new movimiento
- **GET /movimientos/reportes?cuentaId={id}&startDate={date}&endDate={date}** - Get movimientos report

## Profiles

- **dev**: Uses PostgreSQL database with R2DBC
- **test**: Uses H2 in-memory database for testing

## Architecture

This service uses Clean Architecture with reactive programming:
- Domain layer with entities and interfaces
- Use case layer with reactive business logic
- Infrastructure layer with R2DBC repositories
- Presentation layer with WebFlux controllers