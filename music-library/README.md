# 🎵 Music Library API

A comprehensive RESTful API for managing a music library built with **Spring Boot 3.5.7** and **MySQL**. This Java application provides full CRUD operations for artists, albums, and genres, with advanced features including pagination, search functionality, album cover images, comprehensive testing, and a Postman sample data import with 50 artists and over 100 albums.

## 📋 Table of Contents

- [Project Overview](#-project-overview)
  - [Key Capabilities](#key-capabilities)
- [Development Approach](#-development-approach)
- [Technology Stack](#️-technology-stack)
- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [API Endpoints](#-api-endpoints)
- [Data Models](#-data-models)
- [DTOs (Data Transfer Objects)](#-dtos-data-transfer-objects)
- [Project Structure](#️-project-structure)
- [Configuration](#-configuration)
  - [Test Configuration](#test-configuration-application-testyaml)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Error Handling](#-error-handling)
- [Support](#-support)
- [License](#-license)

---

## 🎵 Project Overview

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Educational-yellow.svg)](LICENSE)

The Music Library API is a portfolio-quality Spring Boot application that demonstrates modern backend development practices. It provides a complete solution for managing a music catalog with:

- **Artists** - Musicians and bands with biographical information
- **Albums** - Music releases with detailed metadata (release dates, cover art, track counts, catalog numbers)
- **Genres** - Musical categories with many-to-many relationships to albums
- **Relationship Queries** - Find albums by artist or genre
- **Database Management** - Reset functionality for development/testing

### Key Capabilities

✅ Full CRUD operations for all entities  
✅ Pagination and sorting on all list endpoints  
✅ Input validation with detailed error messages  
✅ Automatic timestamp tracking (createdAt, updatedAt)  
✅ OpenAPI 3.0 specification with interactive Swagger UI  
✅ Comprehensive test suite (unit, integration, repository tests)  
✅ Rich music library with 50 artists and 100+ albums  
✅ Album cover image support  
✅ Environment-based configuration  

### Tools & Technologies

**Postman**  
Comprehensive collection with 150+ API requests for testing all endpoints and populating the database with realistic music data. Includes organized folders for Artists, Albums, Genres, and relationship queries.

**Node.js Scripts**  
Custom data processing scripts for downloading album cover images from the iTunes API and preparing JSON data for database population.

---

## 🤖 Development Approach

This project was developed using **AI-assisted development practices**, a modern approach that combines human expertise with AI tools to accelerate development while maintaining high code quality and architectural integrity.

### Developer Responsibilities

The developer was responsible for all critical aspects of the application, including:

**Architecture & Design**
- Database schema design with complex many-to-many relationships
- Entity structure and relationship mapping
- RESTful API design patterns
- DTO pattern implementation

**Problem-Solving & Debugging**
- Created PUT methods for Album, Artist, and Genre entities
- Fixed test scripts (corrected validation tests for albums)
- Debugged functionality in library.html
- Resolved artist-album association issues in the database
- Implemented data integrity solutions using programmatic variable patterns in Postman

**Feature Development & Enhancement**
- Added clear search button functionality to library.html
- Integrated album cover images with iTunes API
- Implemented pagination and sorting across all endpoints

**Integration & Testing**
- Integrated multiple technologies: Spring Boot, MySQL, Postman, Node.js, ngrok
- Created and validated comprehensive Postman collection with 150+ API requests
- Performed collection runs to ensure data integrity
- Validated all CRUD operations and relationship queries

### AI Tool Usage

AI tools including Claude Sonnet 4.5, GPT-5, DeepSeek R1, Gemini 3 Pro, and Llama 4 Maverick were used as development accelerators for:
- Generating boilerplate code
- Discussing implementation patterns for common Spring Boot features
- Providing syntax assistance and code completion
- Generating initial test structures
- HTML/CSS/JS creation
- Creating debugging scripts
- Code review, method improvements, code comments
- Data validation and error handling

### Why This Approach Works

Modern software development increasingly leverages AI tools as productivity multipliers. This approach mirrors real-world professional development where:
- Developers focus on architecture, design decisions, and problem-solving
- AI tools handle repetitive coding tasks and boilerplate generation
- Human expertise guides the overall direction and ensures quality
- Debugging and integration remain fundamentally human skills

The result is a **production-quality application** that demonstrates both technical competency and the ability to effectively leverage modern development tools — a skill increasingly valued in professional software engineering.


### AWS Cloud Deployment

The application has been successfully deployed to **Amazon Web Services (AWS)** using a modern containerized architecture with managed services. This production deployment demonstrates enterprise-grade cloud infrastructure skills and DevOps practices.

**Live Production API**: [http://project.jcarl.net:8080/api](http://project.jcarl.net:8080/api)  
**Custom Domain**: `project.jcarl.net` (via AWS Route 53)

#### AWS Architecture

The deployment leverages multiple AWS services in a scalable, secure architecture:

- **AWS RDS MySQL**: Managed database service for production data
  - Endpoint: `music-library-db.cv4kawuomqo5.us-west-2.rds.amazonaws.com:3306`
  - Automated backups, monitoring, and maintenance
  - Security group configured for ECS access only

- **AWS ECR**: Private Docker container registry
  - Repository: `913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library`
  - Secure image storage and versioning

- **AWS ECS Fargate**: Serverless container orchestration
  - Cluster: `music-library-cluster1`
  - Service: `music-library-service`
  - No server management required
  - Auto-scaling capabilities
  - Dynamic IP address management

- **AWS Route 53**: DNS management and domain routing
  - Hosted Zone: `jcarl.net` (ID: Z08164103KW73VKOVN6GY)
  - A Record: `project.jcarl.net` → ECS Fargate public IP
  - Automated DNS updates via container startup scripts
  - TTL: 300 seconds

- **AWS CodeBuild**: Automated CI/CD pipeline
  - Builds Docker images from source
  - Pushes to ECR automatically
  - Configured via `buildspec.yml`

- **AWS S3**: Build artifact storage
- **AWS IAM**: Role-based access control and security
  - Task Role: `ECS-Task-Route53-Role` with Route 53 update permissions
  - Policy: `ECS-Route53-Update-Policy` (route53:ChangeResourceRecordSets, route53:GetChange)

#### Solving the Dynamic IP Challenge

**Problem**: AWS ECS Fargate assigns dynamic public IP addresses that change whenever tasks restart, making it difficult to maintain a consistent API endpoint for users and documentation.

**Solution**: Implemented a container-based automated DNS management system that updates Route 53 at startup:

1. **Container Scripts**: Created `/update-route53.sh` and `/startup.sh` scripts embedded in the Docker image
2. **IP Detection**: Uses ECS Task Metadata Endpoint V4 (`$ECS_CONTAINER_METADATA_URI_V4/task`) to retrieve the Fargate task's public IP
3. **DNS Update**: AWS CLI executes `route53 change-resource-record-sets` with UPSERT action to update the A record
4. **Startup Integration**: Docker ENTRYPOINT runs `startup.sh` which calls the Route 53 update script before starting the application
5. **IAM Task Role**: `ECS-Task-Route53-Role` with `ECS-Route53-Update-Policy` grants permissions for:
   - `route53:ChangeResourceRecordSets` on hosted zone `Z08164103KW73VKOVN6GY`
   - `route53:GetChange` on change resources
6. **Required Tools**: Container includes `aws-cli`, `jq`, and `curl` (installed in Alpine Linux base image)
7. **Error Handling**: Graceful degradation - application starts even if DNS update fails

**Technical Implementation**:
```bash
# Container detects its own IP and updates DNS
TASK_METADATA=$(curl -s $ECS_CONTAINER_METADATA_URI_V4/task)
PUBLIC_IP=$(echo $TASK_METADATA | jq -r '.Containers[0].Networks[0].IPv4Addresses[0]')
aws route53 change-resource-record-sets --hosted-zone-id Z08164103KW73VKOVN6GY \
  --change-batch '{"Changes":[{"Action":"UPSERT","ResourceRecordSet":{...}}]}'
```

**Benefits**:
- ✅ Consistent API endpoint regardless of infrastructure changes
- ✅ Self-contained solution - no external Lambda functions required
- ✅ Updates DNS within seconds of container startup
- ✅ Fully automated - no manual intervention required
- ✅ Professional custom domain instead of raw IP addresses
- ✅ Demonstrates container-based automation and AWS IAM integration

This solution showcases real-world DevOps problem-solving: identifying infrastructure limitations and implementing automated solutions using container-native approaches and cloud IAM security.

#### Docker Containerization

The application uses a **multi-stage Docker build** for optimal image size and security:

```dockerfile
# Stage 1: Build with Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with OpenJDK
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Benefits**:
- Smaller final image (only JRE, not full JDK)
- Build dependencies not included in runtime
- Faster deployments and reduced attack surface

#### Deployment Validation

The AWS deployment was thoroughly tested using Postman:
- **182 API requests** executed successfully
- **600 tests** passed (100% pass rate)
- All CRUD operations validated
- Relationship queries verified
- Performance metrics collected

This comprehensive testing ensures production readiness and API reliability.

---

### AWS Cost Management

Understanding and managing AWS costs is crucial for maintaining a cost-effective deployment. This section provides guidance on optimizing your AWS spending while keeping the application available when needed.

#### Cost Breakdown

The Music Library API deployment incurs the following monthly costs:

| Service | Cost | Notes |
|---------|------|-------|
| **RDS MySQL (db.t3.micro)** | ~$13/month | ~$0.017/hour, runs continuously |
| **ECS Fargate** | ~$1.20/day when running | ~$0.05/hour, only when tasks are active |
| **ECR + S3** | ~$0.10/month | Storage for Docker images and artifacts |
| **Data Transfer** | Variable | Minimal for development/demo usage |

**Total Cost Scenarios:**
- **Always Running**: ~$49/month (RDS + ECS 24/7)
- **Daily Use** (ECS stopped overnight): ~$25/month
- **On-Demand** (both stopped): ~$0.10/month (storage only)

#### How to Stop the Deployment

To minimize costs when not actively using the application:

##### Stop ECS Service (Recommended for Daily Savings)

1. **Navigate to ECS Console**:
   - Go to [AWS ECS Console](https://console.aws.amazon.com/ecs/)
   - Select region: **us-west-2**
   - Click on cluster: **music-library-cluster1**

2. **Update Service**:
   - Click on service: **music-library-service**
   - Click **Update** button (top right)
   - Under **Desired tasks**, change from `1` to `0`
   - Click **Update** at the bottom

3. **Verify**:
   - Wait 1-2 minutes for task to stop
   - Service status should show "0 running tasks"
   - **Savings**: ~$1.20/day (~$36/month)

##### Stop RDS Database (For Extended Breaks)

⚠️ **Note**: RDS can only be stopped for 7 days maximum. After 7 days, AWS automatically restarts it.

1. **Navigate to RDS Console**:
   - Go to [AWS RDS Console](https://console.aws.amazon.com/rds/)
   - Select region: **us-west-2**
   - Click on database: **music-library-db**

2. **Stop Database**:
   - Click **Actions** dropdown
   - Select **Stop temporarily**
   - Confirm the action

3. **Verify**:
   - Status changes to "Stopping" then "Stopped"
   - **Savings**: ~$13/month (while stopped)
   - **Important**: Database will auto-restart after 7 days

#### How to Start the Deployment

When you need to use the application again:

##### Start RDS Database (If Stopped)

1. **Navigate to RDS Console**:
   - Go to [AWS RDS Console](https://console.aws.amazon.com/rds/)
   - Select region: **us-west-2**
   - Click on database: **music-library-db**

2. **Start Database**:
   - Click **Actions** dropdown
   - Select **Start**
   - Wait 3-5 minutes for database to become available

3. **Verify**:
   - Status changes to "Starting" then "Available"
   - Endpoint becomes accessible

##### Start ECS Service

1. **Navigate to ECS Console**:
   - Go to [AWS ECS Console](https://console.aws.amazon.com/ecs/)
   - Select region: **us-west-2**
   - Click on cluster: **music-library-cluster1**

2. **Update Service**:
   - Click on service: **music-library-service**
   - Click **Update** button
   - Under **Desired tasks**, change from `0` to `1`
   - Click **Update**

3. **Wait for Deployment**:
   - Task status changes to "PROVISIONING" → "PENDING" → "RUNNING"
   - **Expected time**: 2-3 minutes
   - Health checks must pass before accepting traffic

4. **Verify Application**:
   ```bash
   curl http://54.212.239.199:8080/api
   ```
   - Should return API information
   - Swagger UI should be accessible

#### Best Practices

Choose a cost management strategy based on your usage pattern:

##### For Daily Development
- **Stop ECS overnight** (set desired tasks to 0)
- **Keep RDS running** for quick morning startups
- **Cost**: ~$25/month
- **Startup time**: 2-3 minutes (ECS only)
- **Best for**: Active development, frequent testing

##### For Weekend/Short Breaks (1-7 days)
- **Stop both ECS and RDS**
- **Cost**: ~$0.10/month (storage only)
- **Startup time**: 5-8 minutes (RDS + ECS)
- **Best for**: Weekends, short vacations

##### For Long Breaks (7+ days)
- **Stop ECS** (set desired tasks to 0)
- **Keep RDS running** (it will auto-restart anyway)
- **Cost**: ~$13/month
- **Startup time**: 2-3 minutes when needed
- **Best for**: Extended breaks, infrequent use

##### For Job Hunting/Portfolio Demos
- **Keep both stopped** by default
- **Start on-demand** before interviews/demos
- **Cost**: ~$0.10/month + usage
- **Startup time**: 5-8 minutes
- **Best for**: Showing to employers, portfolio demonstrations

#### AWS CLI Quick Commands

For faster management, use AWS CLI commands:

##### Stop Services
```bash
# Stop ECS service (set desired count to 0)
aws ecs update-service \
  --cluster music-library-cluster1 \
  --service music-library-service \
  --desired-count 0 \
  --region us-west-2

# Stop RDS database
aws rds stop-db-instance \
  --db-instance-identifier music-library-db \
  --region us-west-2
```

##### Start Services
```bash
# Start RDS database
aws rds start-db-instance \
  --db-instance-identifier music-library-db \
  --region us-west-2

# Wait for RDS to be available (optional)
aws rds wait db-instance-available \
  --db-instance-identifier music-library-db \
  --region us-west-2

# Start ECS service (set desired count to 1)
aws ecs update-service \
  --cluster music-library-cluster1 \
  --service music-library-service \
  --desired-count 1 \
  --region us-west-2
```

##### Check Status
```bash
# Check ECS service status
aws ecs describe-services \
  --cluster music-library-cluster1 \
  --services music-library-service \
  --region us-west-2 \
  --query 'services[0].[serviceName,runningCount,desiredCount]' \
  --output table

# Check RDS status
aws rds describe-db-instances \
  --db-instance-identifier music-library-db \
  --region us-west-2 \
  --query 'DBInstances[0].[DBInstanceIdentifier,DBInstanceStatus]' \
  --output table
```

**Prerequisites for CLI commands:**
- Install [AWS CLI](https://aws.amazon.com/cli/)
- Configure credentials: `aws configure`
- Set default region to `us-west-2`

---

## 🛠️ Technology Stack

### Core Framework
- **Spring Boot**: 3.5.7
- **Java**: 17
- **Maven**: Build automation and dependency management

### Database & Persistence
- **MySQL**: 8.0+ (production)
- **H2**: In-memory database (testing)
- **Spring Data JPA**: Data access abstraction
- **Hibernate**: ORM implementation

### API & Documentation
- **Spring Web**: RESTful API framework
- **SpringDoc OpenAPI**: 2.8.14 (Swagger UI)
- **Bean Validation (JSR-380)**: Input validation

### Testing
- **JUnit 5**: Testing framework
- **Spring Boot Test**: Integration testing support
- **Mockito**: 5.2.0 (mocking framework with inline support)
- **Testcontainers**: Real MySQL containers for integration tests
- **JaCoCo**: 0.8.12 (code coverage reporting)

### Containerization & Deployment
- **Docker**: Multi-stage containerization with Maven and OpenJDK
- **AWS RDS**: Managed MySQL database service
- **AWS ECR**: Elastic Container Registry for Docker images
- **AWS ECS Fargate**: Serverless container orchestration
- **AWS Route 53**: DNS management with automated updates
- **AWS Lambda**: Serverless functions for infrastructure automation
- **AWS EventBridge**: Event-driven task monitoring
- **AWS CodeBuild**: CI/CD pipeline for automated builds
- **AWS S3**: Storage for build artifacts
- **AWS IAM**: Identity and access management

### Utilities
- **Lombok**: Boilerplate code reduction
- **Jackson**: JSON serialization/deserialization

---

## ✨ Features

### API Features
- **RESTful Design**: Standard HTTP methods (GET, POST, PUT, DELETE)
- **Pagination**: All list endpoints support `page`, `size`, and `sort` parameters
- **Relationship Queries**: Get albums by artist or genre
- **Input Validation**: Comprehensive validation with detailed error messages
- **Standardized Errors**: Consistent `ApiError` responses with timestamps and details
- **Automatic Timestamps**: `createdAt` and `updatedAt` tracked automatically
- **Database Reset**: Development endpoint to reset database state

### Technical Features
- **DTO Pattern**: Separate request/response objects for clean API contracts
- **Global Exception Handling**: Centralized error handling with `@ControllerAdvice`
- **Bidirectional Relationships**: Properly managed JPA relationships
- **Lazy Loading**: Optimized database queries with lazy fetching
- **Cascade Operations**: Automatic relationship management
- **Environment Configuration**: YAML-based config with environment variables
- **Comprehensive Testing**: Unit, integration, and repository tests
- **Code Coverage**: JaCoCo reports for test coverage metrics

---

## 📋 Prerequisites

- **Java 17** or higher installed ([Download](https://www.oracle.com/java/technologies/downloads/#java17))
- **MySQL 8.0+** running locally or accessible remotely ([Download](https://dev.mysql.com/downloads/mysql/))
- **Maven 3.6+** for building the project ([Download](https://maven.apache.org/download.cgi))
- **Git** for version control ([Download](https://git-scm.com/downloads))

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Week16/music-library
```

### 2. Database Setup

Create a MySQL database and user:

```sql
CREATE DATABASE music_library;
CREATE USER 'music_user'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON music_library.* TO 'music_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Environment Variables

The application uses environment variables for database configuration. Set these in your environment or create a `.env` file:

```bash
# Database Configuration
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=music_library
export MYSQL_USER=music_user
export MYSQL_PASSWORD=your_secure_password

# Server Configuration (optional)
export PORT=8080
```

**Note**: The application uses `src/main/resources/application.yaml` which references these environment variables:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:music_library}
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:password}
```

See [Configuration](#-configuration) section for complete details.

### 4. Build the Project

```bash
# Clean and compile
mvn clean compile

# Run tests (optional)
mvn test

# Package the application
mvn package
```

### 5. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/music-library-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`

### 6. Verify Installation

Visit the API info endpoint:
```bash
curl http://localhost:8080/api
```

Or open Swagger UI in your browser:
```
http://localhost:8080/swagger-ui/index.html
```

### 7. Load Sample Data (Optional)

To populate the database with sample data, use the provided Postman collection:

**Windows:**
```bash
populate-music-library.bat
```

**Linux/Mac:**
```bash
chmod +x populate-music-library.sh
./populate-music-library.sh
```

Or import the Postman collection: `Music-Library-Sample-Data.postman_collection.json`

---

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **OpenAPI YAML**: [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)
- **API Info**: [http://localhost:8080/api](http://localhost:8080/api)

The Swagger UI provides:
- Interactive endpoint testing
- Request/response schemas
- Example payloads
- Authentication details (if applicable)

---

## 🎯 API Endpoints

### General

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api` | API information and available endpoints | 200 |

### Artists

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/artists` | Create a new artist | 201 |
| GET | `/api/artists` | Get all artists (paginated) | 200 |
| GET | `/api/artists/{id}` | Get artist by ID | 200 |
| PUT | `/api/artists/{id}` | Update an artist | 200 |
| DELETE | `/api/artists/{id}` | Delete an artist | 204 |
| GET | `/api/artists/{artistId}/albums` | Get all albums by artist | 200 |

### Albums

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/albums` | Create a new album | 201 |
| GET | `/api/albums` | Get all albums (paginated) | 200 |
| GET | `/api/albums/{id}` | Get album by ID | 200 |
| PUT | `/api/albums/{id}` | Update an album | 200 |
| DELETE | `/api/albums/{id}` | Delete an album | 204 |

### Genres

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/genres` | Create a new genre | 201 |
| GET | `/api/genres` | Get all genres (paginated) | 200 |
| GET | `/api/genres/{id}` | Get genre by ID | 200 |
| PUT | `/api/genres/{id}` | Update a genre | 200 |
| DELETE | `/api/genres/{id}` | Delete a genre | 204 |
| GET | `/api/genres/{genreId}/albums` | Get all albums by genre | 200 |

### Database Management

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| DELETE | `/api/reset?confirm=true` | Reset database (requires confirmation) | 200 |

### Query Parameters

All list endpoints support pagination and sorting:

- **`page`** - Page number (zero-based, default: 0)
- **`size`** - Items per page (default: 20)
- **`sort`** - Sort criteria (format: `field,direction`)
  - Examples: `name,asc`, `createdAt,desc`, `title,asc`

**Example Requests:**

```bash
# Get first page of artists, sorted by name
GET /api/artists?page=0&size=10&sort=name,asc

# Get second page of albums, sorted by release date descending
GET /api/albums?page=1&size=20&sort=releaseDate,desc

# Get all genres, sorted by name
GET /api/genres?sort=name,asc
```

---

## 📊 Data Models

### Artist Entity

Represents a musician or band.

```json
{
  "artistId": 1,
  "name": "The Rolling Stones",
  "description": "Legendary British rock band formed in 1962",
  "createdAt": "2025-01-19T04:40:37",
  "updatedAt": "2025-01-19T04:40:37"
}
```

**Fields:**
- `artistId` (Long) - Auto-generated primary key
- `name` (String, required) - Artist name (max 255 characters)
- `description` (String, optional) - Biographical information (TEXT)
- `createdAt` (LocalDateTime) - Auto-generated creation timestamp
- `updatedAt` (LocalDateTime) - Auto-updated modification timestamp

**Relationships:**
- One-to-Many with Album (cascade all, orphan removal)

---

### Album Entity

Represents a music album with detailed metadata.

```json
{
  "albumId": 1,
490│  "title": "Abbey Road",
491│  "releaseDate": "1969-09-26",
492│  "releaseYear": 1969,
493│  "coverImageUrl": "https://example.com/covers/abbey-road.jpg",
  "trackCount": 10,
  "catalogNumber": "COC-59100",
  "artist": {
    "artistId": 1,
498│    "name": "The Beatles"
  },
  "genres": [
    {
      "genreId": 1,
      "name": "Rock"
    }
  ],
  "createdAt": "2025-01-19T04:40:37",
  "updatedAt": "2025-01-19T04:40:37"
}
```

**Fields:**
- `albumId` (Long) - Auto-generated primary key
- `title` (String, required) - Album title (max 255 characters)
- `releaseDate` (LocalDate, optional) - Full release date
- `releaseYear` (Integer, computed) - Derived from releaseDate (transient field)
- `coverImageUrl` (String, optional) - URL to album cover image (max 255 characters)
- `trackCount` (Integer, optional) - Number of tracks on the album
- `catalogNumber` (String, optional) - Unique catalog identifier (max 50 characters)
- `artist` (Artist, required) - Many-to-One relationship with Artist
- `genres` (Set<Genre>, optional) - Many-to-Many relationship with Genre
- `createdAt` (LocalDateTime) - Auto-generated creation timestamp
- `updatedAt` (LocalDateTime) - Auto-updated modification timestamp

**Relationships:**
- Many-to-One with Artist (required)
- Many-to-Many with Genre (via `album_genre` join table)

---

### Genre Entity

Represents a musical genre or category.

```json
{
  "genreId": 1,
  "name": "Rock",
  "description": "Rock music genre characterized by electric guitars and strong rhythms",
  "createdAt": "2025-01-19T04:40:37",
  "updatedAt": "2025-01-19T04:40:37"
}
```

**Fields:**
- `genreId` (Long) - Auto-generated primary key
- `name` (String, required) - Genre name (max 100 characters)
- `description` (String, optional) - Genre description (TEXT)
- `createdAt` (LocalDateTime) - Auto-generated creation timestamp
- `updatedAt` (LocalDateTime) - Auto-updated modification timestamp

**Relationships:**
- Many-to-Many with Album (inverse side, mapped by `genres`)

---

## 📦 DTOs (Data Transfer Objects)

The API uses DTOs to separate internal entity representations from external API contracts.

### CreateArtistRequest

Used for creating new artists.

```json
{
  "name": "The Beatles",
  "description": "Iconic British rock band from Liverpool"
}
```

### CreateAlbumRequest

Used for creating new albums.

```json
{
  "title": "Abbey Road",
  "artistId": 1,
  "genreIds": [1, 2],
  "releaseDate": "1969-09-26",
  "coverImageUrl": "https://example.com/covers/abbey-road.jpg",
  "trackCount": 17,
  "catalogNumber": "PCS-7088"
}
```

### CreateGenreRequest

Used for creating new genres.

```json
{
  "name": "Progressive Rock",
  "description": "Complex rock music with experimental elements"
}
```

### ApiInfoResponse

Returned by `GET /api` endpoint with comprehensive API information.

### DatabaseResetResponse

Returned by `DELETE /api/reset` endpoint with reset statistics.

---

## 🏗️ Project Structure

```
music-library/
├── src/
│   ├── main/
│   │   ├── java/music/library/
│   │   │   ├── MusicLibraryApplication.java    # Main application class
│   │   │   ├── controller/                     # REST controllers
│   │   │   │   ├── MusicLibraryController.java # Main API controller (GET, POST, PUT, DELETE)
│   │   │   │   └── SwaggerRedirectController.java # Swagger UI redirect
│   │   │   ├── entity/                         # JPA entities
│   │   │   │   ├── Artist.java                 # Artist entity with albums relationship
│   │   │   │   ├── Album.java                  # Album entity with artist and genres
│   │   │   │   └── Genre.java                  # Genre entity with albums relationship
│   │   │   ├── repository/                     # Spring Data repositories
│   │   │   │   ├── ArtistRepository.java       # Artist data access
│   │   │   │   ├── AlbumRepository.java        # Album data access
│   │   │   │   └── GenreRepository.java        # Genre data access
│   │   │   ├── service/                        # Business logic layer
│   │   │   │   ├── ArtistService.java          # Artist CRUD operations
│   │   │   │   ├── AlbumService.java           # Album CRUD operations
│   │   │   │   ├── GenreService.java           # Genre CRUD operations
│   │   │   │   └── DatabaseResetService.java   # Database reset functionality
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── CreateArtistRequest.java    # DTO for creating artists
│   │   │   │   ├── CreateAlbumRequest.java     # DTO for creating albums
│   │   │   │   ├── CreateGenreRequest.java     # DTO for creating genres
│   │   │   │   ├── UpdateArtistRequest.java    # DTO for updating artists (PUT)
│   │   │   │   ├── UpdateAlbumRequest.java     # DTO for updating albums (PUT)
│   │   │   │   ├── UpdateGenreRequest.java     # DTO for updating genres (PUT)
│   │   │   │   ├── ApiInfoResponse.java        # API welcome endpoint response
│   │   │   │   └── DatabaseResetResponse.java  # Database reset response
│   │   │   ├── exception/                      # Exception handling
│   │   │   │   ├── ResourceNotFoundException.java # Custom 404 exception
│   │   │   │   ├── GlobalExceptionHandler.java # Centralized error handling
│   │   │   │   └── ApiError.java               # Standardized error response
│   │   │   ├── config/                         # Configuration classes
│   │   │   │   ├── SwaggerConfig.java          # OpenAPI/Swagger configuration
│   │   │   │   ├── CorsConfig.java             # CORS configuration
│   │   │   │   └── PageConfig.java             # Pagination configuration
│   │   │   └── specification/                  # JPA Specifications
│   │   │       └── AlbumSpecs.java             # Dynamic query specifications
│   │   └── resources/
│   │       ├── application.yaml                # Main configuration
│   │       ├── application-test.yaml           # Test profile configuration
│   │       └── static/                         # Static web resources
│   │           ├── index.html                  # API welcome page
│   │           ├── library.html                # Music library browser UI
│   │           └── covers/                     # Album cover images directory
│   └── test/
│       └── java/music/library/
│           ├── integration/                    # Integration tests
│           │   ├── ArtistControllerIT.java     # Artist endpoint tests
│           │   ├── AlbumControllerIT.java      # Album endpoint tests (GET, POST)
│           │   ├── AlbumControllerUpdateDeleteIT.java # Album PUT/DELETE tests
│           │   ├── GenreControllerIT.java      # Genre endpoint tests
│           │   └── RestResponsePage.java       # Pagination test helper
│           ├── service/                        # Service layer tests
│           │   ├── ArtistServiceTest.java      # Artist service unit tests
│           │   ├── AlbumServiceTest.java       # Album service unit tests
│           │   └── AlbumServiceBidirectionalTest.java # Relationship tests
│           └── repository/                     # Repository tests
│               └── AlbumRepositoryTest.java    # Album repository tests
├── pom.xml                                     # Maven configuration
├── README.md                                   # This file
├── populate-music-library.bat                  # Windows data loader script
├── populate-music-library.sh                   # Unix/Mac data loader script
├── Dockerfile                                  # Docker containerization config
├── docker-compose.yaml                         # Docker Compose configuration
└── Music-Library-Sample-Data.postman_collection.json # Postman API collection
```

### Package Structure

The application follows a standard layered architecture:

- **`music.library`** - Root package
  - **`controller`** - REST API endpoints (presentation layer)
  - **`service`** - Business logic (service layer)
  - **`repository`** - Data access (persistence layer)
  - **`entity`** - JPA entities (domain model)
  - **`dto`** - Data Transfer Objects (API contracts)
  - **`exception`** - Custom exceptions and error handling
  - **`config`** - Spring configuration classes
  - **`specification`** - JPA Specifications for dynamic queries

---

## 🔧 Configuration

### Application Configuration (application.yaml)

The application uses YAML configuration with environment variables for flexibility:

```yaml
# Server Configuration
server:
  port: ${PORT:8080}  # Server port, fallback to 8080 if not specified

# Database Configuration
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}

  # JPA / Hibernate Configuration
  jpa:
    hibernate:
      ddl-auto: update              # Hibernate manages schema updates
    show-sql: true                  # Print SQL statements (debug)

  # Springdoc / Swagger UI Configuration
  springdoc:
    api-docs:
      path: /v3/api-docs
    swagger-ui:
      enabled: true
      path: /swagger-ui.html
      cors-enabled: true
      servers:
        - url: https://suanne-speedless-chrissy.ngrok-free.dev

# Prevent Spring from executing plain .sql scripts
sql:
  init:
    mode: never
```

### Environment Variables

Required environment variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `MYSQL_HOST` | MySQL server hostname | `localhost` |
| `MYSQL_PORT` | MySQL server port | `3306` |
| `MYSQL_DATABASE` | Database name | `music_library` |
| `MYSQL_USER` | Database username | `music_user` |
| `MYSQL_PASSWORD` | Database password | `your_secure_password` |
| `PORT` | Server port (optional) | `8080` |

### Test Configuration (application-test.yaml)

Tests use an H2 in-memory database for fast, isolated testing:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

---

## 🧪 Testing

The application includes a comprehensive test suite covering multiple layers.

### Test Structure

```
src/test/java/music/library/
├── integration/              # Full stack integration tests
│   ├── ArtistControllerIT.java
│   ├── AlbumControllerIT.java
│   ├── AlbumControllerUpdateDeleteIT.java
│   └── GenreControllerIT.java
├── service/                  # Service layer unit tests
│   ├── ArtistServiceTest.java
│   ├── AlbumServiceTest.java
│   └── AlbumServiceBidirectionalTest.java
└── repository/               # Repository layer tests
    └── AlbumRepositoryTest.java
```

### Test Types

#### 1. Integration Tests (`*IT.java`)

Full-stack tests using `@SpringBootTest` and `MockMvc`:

- Test complete request/response cycle
- Use H2 in-memory database
- Verify HTTP status codes, headers, and response bodies
- Test pagination, sorting, and filtering
- Validate error handling and edge cases

**Example:**
```java
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArtistControllerIT {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreateArtist() throws Exception {
        mockMvc.perform(post("/api/artists")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"The Beatles\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("The Beatles"));
    }
}
```

#### 2. Service Tests (`*Test.java`)

Unit tests for business logic using Mockito:

- Mock repository dependencies
- Test service methods in isolation
- Verify exception handling
- Test bidirectional relationship management

**Example:**
```java
@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {
    @Mock
    private ArtistRepository artistRepository;
    
    @InjectMocks
    private ArtistService artistService;
    
    @Test
    void testFindById_Success() {
        Artist artist = new Artist();
        artist.setArtistId(1L);
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        
        Artist result = artistService.findById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getArtistId());
    }
}
```

#### 3. Repository Tests

Tests for custom repository queries and JPA behavior:

- Test custom query methods
- Verify relationship mappings
- Test cascade operations

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ArtistControllerIT

# Run tests
mvn test

# View test results
# Individual test results are in target/surefire-reports/
```

### Test Results

Test execution results are generated by **Maven Surefire**:

- Test reports generated in `target/surefire-reports/`
- XML and text reports for each test class
- Console output shows test pass/fail summary

### Test Configuration

Tests use a separate profile (`test`) with H2 database:

```yaml
# application-test.yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
```

The Maven Surefire plugin automatically activates the test profile:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <spring.profiles.active>test</spring.profiles.active>
        </systemPropertyVariables>
    </configuration>
</plugin>
```

### Test Best Practices

✅ Use `@TestInstance(PER_CLASS)` for efficient test data reuse  
✅ Clean up test data in `@AfterEach` or `@AfterAll` methods  
✅ Use meaningful test names (e.g., `testCreateArtist_WithValidData_ReturnsCreated`)  
✅ Test both success and failure scenarios  
✅ Verify exception messages and error responses  
✅ Use `@Transactional` for tests that modify data  

---

## 🚀 Deployment Options

The Music Library API supports multiple deployment strategies, from local development to production cloud infrastructure.

---

### 1. AWS Cloud Deployment (Production)

**Live Production API**: [http://project.jcarl.net:8080/api](http://project.jcarl.net:8080/api)  
**Swagger UI**: [http://project.jcarl.net:8080/swagger-ui/index.html](http://project.jcarl.net:8080/swagger-ui/index.html)  
**Custom Domain**: `project.jcarl.net` (AWS Route 53 managed)

The application is deployed on AWS using a containerized, serverless architecture with managed services.

#### AWS Services Used

| Service | Purpose | Configuration |
|---------|---------|---------------|
| **RDS MySQL** | Production database | `music-library-db.cv4kawuomqo5.us-west-2.rds.amazonaws.com` |
| **ECR** | Docker image registry | `913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library` |
| **ECS Fargate** | Container orchestration | Cluster: `music-library-cluster1` |
| **Route 53** | DNS management | Domain: `project.jcarl.net` with automated updates |
| **Lambda** | Infrastructure automation | Auto-updates DNS on ECS IP changes |
| **EventBridge** | Event monitoring | Triggers Lambda on ECS task state changes |
| **CodeBuild** | CI/CD pipeline | Automated builds with `buildspec.yml` |
| **S3** | Build artifacts | Secure storage for deployment files |
| **IAM** | Access management | Role-based security policies |

#### Step-by-Step AWS Deployment Process

##### 1. Database Setup (RDS)

```bash
# Create RDS MySQL instance
aws rds create-db-instance \
  --db-instance-identifier music-library-db \
  --db-instance-class db.t3.micro \
  --engine mysql \
  --master-username admin \
  --master-user-password <password> \
  --allocated-storage 20 \
  --vpc-security-group-ids <security-group-id>

# Configure security group to allow ECS access
aws ec2 authorize-security-group-ingress \
  --group-id <rds-security-group-id> \
  --protocol tcp \
  --port 3306 \
  --source-group <ecs-security-group-id>
```

**RDS Endpoint**: `music-library-db.cv4kawuomqo5.us-west-2.rds.amazonaws.com:3306`

##### 2. Container Registry Setup (ECR)

```bash
# Create ECR repository
aws ecr create-repository \
  --repository-name music-library \
  --region us-west-2

# Authenticate Docker to ECR
aws ecr get-login-password --region us-west-2 | \
  docker login --username AWS \
  --password-stdin 913212790762.dkr.ecr.us-west-2.amazonaws.com

# Build and push Docker image
docker build -t music-library .
docker tag music-library:latest \
  913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
docker push 913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
```

**ECR Repository**: `913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library`

##### 3. CodeBuild Configuration (CI/CD)

Create `buildspec.yml` in project root:

```yaml
version: 0.2

phases:
  pre_build:
    commands:
      - echo Logging in to Amazon ECR...
      - aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 913212790762.dkr.ecr.us-west-2.amazonaws.com
  build:
    commands:
      - echo Build started on `date`
      - echo Building the Docker image...
      - docker build -t music-library .
      - docker tag music-library:latest 913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
  post_build:
    commands:
      - echo Build completed on `date`
      - echo Pushing the Docker image...
      - docker push 913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest
```

```bash
# Create CodeBuild project
aws codebuild create-project \
  --name music-library-build \
  --source type=GITHUB,location=<github-repo-url> \
  --artifacts type=NO_ARTIFACTS \
  --environment type=LINUX_CONTAINER,image=aws/codebuild/standard:7.0,computeType=BUILD_GENERAL1_SMALL \
  --service-role <codebuild-role-arn>
```

##### 4. ECS Cluster Setup

```bash
# Create ECS cluster
aws ecs create-cluster \
  --cluster-name music-library-cluster1 \
  --region us-west-2

# Register task definition
aws ecs register-task-definition \
  --family music-library-task \
  --network-mode awsvpc \
  --requires-compatibilities FARGATE \
  --cpu 256 \
  --memory 512 \
  --container-definitions '[
    {
      "name": "music-library",
      "image": "913212790762.dkr.ecr.us-west-2.amazonaws.com/music-library:latest",
      "portMappings": [{"containerPort": 8080, "protocol": "tcp"}],
      "environment": [
        {"name": "MYSQL_HOST", "value": "music-library-db.cv4kawuomqo5.us-west-2.rds.amazonaws.com"},
        {"name": "MYSQL_PORT", "value": "3306"},
        {"name": "MYSQL_DATABASE", "value": "music_library"},
        {"name": "MYSQL_USER", "value": "admin"},
        {"name": "MYSQL_PASSWORD", "value": "<password>"}
      ]
    }
  ]'
```

##### 5. ECS Service Creation

```bash
# Create ECS service
aws ecs create-service \
  --cluster music-library-cluster1 \
  --service-name music-library-service \
  --task-definition music-library-task \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={
    subnets=[<subnet-id>],
    securityGroups=[<security-group-id>],
    assignPublicIp=ENABLED
  }"
```

**ECS Service**: `music-library-service` in cluster `music-library-cluster1`

#### Security Configuration

##### ECS Security Group

```bash
# Allow HTTP traffic on port 8080
aws ec2 authorize-security-group-ingress \
  --group-id <ecs-security-group-id> \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0

# Allow outbound to RDS
aws ec2 authorize-security-group-egress \
  --group-id <ecs-security-group-id> \
  --protocol tcp \
  --port 3306 \
  --destination-group <rds-security-group-id>
```

##### RDS Security Group

```bash
# Allow inbound from ECS only
aws ec2 authorize-security-group-ingress \
  --group-id <rds-security-group-id> \
  --protocol tcp \
  --port 3306 \
  --source-group <ecs-security-group-id>
```

#### Environment Variables

The application is configured via environment variables in the ECS task definition:

```yaml
environment:
  - MYSQL_HOST=music-library-db.cv4kawuomqo5.us-west-2.rds.amazonaws.com
  - MYSQL_PORT=3306
  - MYSQL_DATABASE=music_library
  - MYSQL_USER=admin
  - MYSQL_PASSWORD=<secure-password>
  - PORT=8080
```

These variables are referenced in `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=false
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
server:
  port: ${PORT:8080}
```

#### Docker Multi-Stage Build

The `Dockerfile` uses a multi-stage build for optimization:

```dockerfile
# Stage 1: Build application with Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Benefits**:
- **Smaller image size**: Only JRE in final image (~150MB vs ~600MB)
- **Faster deployments**: Less data to transfer
- **Better security**: Build tools not in production image
- **Layer caching**: Maven dependencies cached separately

#### Deployment Validation

The AWS deployment was validated using comprehensive Postman testing:

- ✅ **182 API requests** executed successfully
- ✅ **600 tests** passed (100% pass rate)
- ✅ **19.2 seconds** total execution time
- ✅ All CRUD operations verified
- ✅ Relationship queries validated
- ✅ Error handling confirmed
- ✅ Pagination and sorting tested

#### AWS Deployment Benefits

- **Scalability**: ECS Fargate auto-scales based on demand
- **Reliability**: Managed services with built-in redundancy
- **Security**: VPC isolation, security groups, IAM roles
- **Maintenance**: Automated backups, patching, monitoring
- **Cost-effective**: Pay only for resources used
- **CI/CD**: Automated builds and deployments with CodeBuild

---

### 2. Railway Platform (Alternative Cloud Option)

Railway is a modern cloud platform that simplifies deployment with automatic CI/CD from GitHub.

#### Railway Setup

1. **Create Railway Account**: [railway.app](https://railway.app)
2. **Create New Project**: Connect GitHub repository
3. **Add MySQL Database**: Railway provides managed MySQL
4. **Configure Environment Variables**:
   ```
   MYSQL_HOST=${{MYSQLHOST}}
   MYSQL_PORT=${{MYSQLPORT}}
   MYSQL_DATABASE=${{MYSQLDATABASE}}
   MYSQL_USER=${{MYSQLUSER}}
   MYSQL_PASSWORD=${{MYSQLPASSWORD}}
   ```
5. **Deploy**: Railway automatically builds and deploys on git push

#### Railway Benefits

- Zero-configuration deployments
- Automatic HTTPS certificates
- Built-in monitoring and logs
- Free tier available for testing
- GitHub integration for CI/CD

---

### 3. ngrok Tunneling (Local Development)

**Live API**: [https://suanne-speedless-chrissy.ngrok-free.dev](https://suanne-speedless-chrissy.ngrok-free.dev)

ngrok provides secure tunneling to expose your local development server to the internet.

#### Prerequisites

1. Java 17 or higher installed
2. MySQL 8.0+ running locally
3. ngrok account ([Sign up](https://ngrok.com/))
4. ngrok installed locally

#### Deployment Steps

1. **Start the Application**
   - Ensure MySQL is running
   - Set environment variables for database connection
   - Run the Spring Boot application: `mvn spring-boot:run`

2. **Start ngrok Tunnel**
   - Open a terminal and run: `ngrok http 8080`
   - ngrok will provide a public URL (e.g., https://x-x-x.ngrok-free.dev)
   - This URL forwards to your local application

3. **Access the API**
   - Use the ngrok URL to access your API from anywhere
   - Swagger UI: `https://x-x-x.ngrok-free.dev/swagger-ui.html`
   - API endpoints: `https://x-x-x.ngrok-free.dev/api`

#### Configuration

The application is configured with environment variables:

```yaml
server:
  port: ${PORT:8080}  # Default port 8080

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=false
```

#### Benefits of ngrok

- **Quick setup**: No deployment configuration needed
- **Secure tunneling**: HTTPS encryption to localhost
- **Request inspection**: Built-in debugging tools
- **Zero cost**: Free tier for development
- **Instant sharing**: Share local work with team/clients

#### Note

The ngrok URL changes each time you restart ngrok (unless using a paid plan with reserved domains). Update the URL in your documentation and clients accordingly.

---

### Deployment Comparison

| Feature | AWS (Production) | Railway | ngrok (Dev) |
|---------|------------------|---------|-------------|
| **Cost** | Pay-as-you-go | Free tier available | Free tier available |
| **Scalability** | Auto-scaling | Auto-scaling | Not scalable |
| **Reliability** | 99.99% SLA | 99.9% uptime | Depends on local machine |
| **Setup Complexity** | High (full control) | Low (automated) | Very low |
| **CI/CD** | CodeBuild | Built-in GitHub integration | Manual |
| **Custom Domain** | Yes (Route 53) | Yes | Paid plans only |
| **Best For** | Production apps | Small-medium projects | Development/testing |

---

## 🚨 Error Handling

The API uses a global exception handler (`@ControllerAdvice`) for consistent error responses.

### Standard Error Response

All errors return an `ApiError` object:

```json
{
  "timestamp": "2025-01-19T10:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "Artist not found with id: 999",
  "path": "/api/artists/999"
}
```

### HTTP Status Codes

| Status Code | Description | Example |
|-------------|-------------|---------|
| **200 OK** | Successful GET/PUT request | Artist retrieved successfully |
| **201 Created** | Successful POST request | Artist created successfully |
| **204 No Content** | Successful DELETE request | Artist deleted successfully |
| **400 Bad Request** | Invalid input data | Missing required field |
| **404 Not Found** | Resource not found | Artist with ID 999 not found |
| **500 Internal Server Error** | Server error | Database connection failed |

### Validation Errors

Input validation errors return detailed field-level errors:

```json
{
  "timestamp": "2025-01-19T10:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Artist name must not be blank",
    "description": "Description must be ≤ 1000 characters"
  },
  "path": "/api/artists"
}
```

### Exception Types

- **`ResourceNotFoundException`** - Entity not found (404)
- **`MethodArgumentNotValidException`** - Validation failure (400)
- **`HttpMessageNotReadableException`** - Malformed JSON (400)
- **`DataIntegrityViolationException`** - Database constraint violation (400)
- **`Exception`** - Generic server error (500)

---

## 📝 License

This project is part of a Java/MySQL backend development bootcamp (Week 16) and is for **educational purposes**.

---

## ❓ Support

For questions, issues, or feedback:

1. **Check the API documentation**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
2. **Review test examples**: See `src/test/java/music/library/`
3. **Check Postman collection**: `Music-Library-Sample-Data.postman_collection.json`

---

## 🎓 Learning Outcomes

This project demonstrates proficiency in:

✅ **Spring Boot 3.x** - Modern Spring framework features  
✅ **RESTful API Design** - Standard HTTP methods and status codes  
✅ **JPA/Hibernate** - Entity relationships and lazy loading  
✅ **Testing** - Unit, integration, and repository tests  
✅ **API Documentation** - OpenAPI/Swagger specification  
✅ **Cloud Deployment** - AWS ECS Fargate with RDS, ECR, and CodeBuild  
✅ **Containerization** - Docker multi-stage builds  
✅ **Error Handling** - Global exception handling  
✅ **Rich Content** - 50 artists and 100+ albums with cover images  
✅ **Input Validation** - Bean Validation (JSR-380)  
✅ **DTO Pattern** - Separation of concerns  
✅ **Environment Configuration** - YAML with environment variables  

---

## 🙏 Acknowledgments

- **Spring Boot Team** - Open-source Java framework
- **Quickstart** - Backend development bootcamp | quickstart.com/bootcamp
- **Amazon Web Services (AWS)** - Cloud infrastructure and managed services | aws.amazon.com
- **ngrok** - Secure tunneling for local development | ngrok.com
- **Postman** - The World's Leading API Platform | postman.com

---

**Built with ❤️ by JC - Backend Developer**

**Happy coding! 🎵**