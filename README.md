# Expression Evaluation API

A Spring Boot-based REST API for evaluating mathematical expressions and storing the results in a PostgreSQL database.

## Features
- Evaluate complex mathematical expressions involving `+`, `-`, `*`, `/`, `^` (power), `%` (modulo), and parentheses `()`.
- Store evaluation history (including errors and successful results).
- Search previous evaluations by their result value.
- API versioning (`/api/v1/...`).
- Interactive API documentation with Swagger/OpenAPI.
- Production-ready monitoring via Spring Boot Actuator.
- Fully containerized with Docker and Docker Compose.

---

## Prerequisites
Before running the application, ensure you have the following installed:
- **Docker** and **Docker Compose** (recommended for easy setup).
- **Java 21** or higher.
- **Maven 3.9+** (if running locally without Docker).
- **PostgreSQL 15+** (if running locally without Docker).

---

## Getting Started

### Method 1: Running with Docker (Recommended)
This is the simplest way to get the API and the database up and running quickly.

1.  **Clone the repository** (if you haven't already).
2.  **Open a terminal** in the project root directory.
3.  **Run Docker Compose**:
    ```bash
    docker-compose up --build
    ```
4.  The API will be available at `http://localhost:8080`.
5.  The PostgreSQL database will be started automatically and initialized.

### Method 2: Running Locally with Maven
If you prefer to run the application directly on your machine:

1.  **Ensure PostgreSQL is running**:
    - Create a database named `expressiondb`.
    - Ensure a user `postgres` with password `postgres` exists (or update `src/main/resources/application.properties` with your credentials).
2.  **Build the project**:
    ```bash
    mvn clean install
    ```
3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

---

## API Documentation

### Swagger UI
Once the application is running, you can explore the API and test endpoints via Swagger UI:
- **URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Postman Collection
A Postman collection is included in the project root: `Expression_Eval_API.postman_collection.json`. You can import this into Postman to quickly test the endpoints.

---

## API Endpoints

### 1. Evaluate Expression
- **Endpoint**: `POST /api/v1/expressions/calculate`
- **Body**:
  ```json
  {
    "expression": "(10 + 2) * 5"
  }
  ```
- **Response**: Returns the calculated result and metadata.

### 2. Find by Result
- **Endpoint**: `GET /api/v1/expressions/find-by-result?value=60`
- **Response**: Returns a list of all expressions that evaluated to the given value.

---

## Monitoring and Health
The application uses Spring Boot Actuator for monitoring:
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Metrics**: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)

---

## Technical Details
- **Framework**: Spring Boot 3.5.10
- **Language**: Java 21
- **Database**: PostgreSQL
- **Persistence**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Bean Validation
- **Documentation**: SpringDoc OpenAPI
