# WSFL (Winter Series Fantasy League) Backend

A Spring Boot application that manages fantasy running leagues, including drafts, race results, and playoff scoring.

## Overview

WSFL is a fantasy sports platform for running leagues. Team owners draft runners for their teams and compete based on their runners' performances in races throughout the season.

## Key Features

- League management with configurable team sizes and seasons
- Snake-style draft system
- Race result tracking and scoring
- Playoff system
- Team standings and statistics
- User authentication and authorization

## Getting Started

### Prerequisites
- Java 23
- PostgreSQL 15+
- Maven 3.9+

### Setup
1. Clone the repository
2. Create a PostgreSQL database named `wsfl-api`
3. Update `application.properties` with your database credentials if needed
4. Run `mvn spring-boot:run`

The application will start on port 9000 and initialize with sample data.

### API Documentation

The API documentation is available through Swagger UI without requiring authentication:
- Swagger UI: http://localhost:9000/swagger-ui.html
- OpenAPI JSON: http://localhost:9000/api-docs

## Use Cases

### 1. Running a League Draft

The draft process allows teams to select runners for their roster:

1. Create a new draft: