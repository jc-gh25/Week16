# 🎵 Music Library API

A comprehensive RESTful API for managing a music library built with **Spring Boot 3.5.7** and **MySQL**. This production-ready application provides full CRUD operations for artists, albums, and genres, with advanced features including pagination, search functionality, album cover images, comprehensive testing, and a rich library of 50 artists and over 100 albums.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Educational-yellow.svg)](LICENSE)

## 📋 Table of Contents

- [Project Overview](#-project-overview)
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
- [Database Migrations](#-database-migrations)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Error Handling](#-error-handling)
- [Contributing](#-contributing)

---

## 🎵 Project Overview

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
- **Flyway**: Database migration management

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
- **Database Migrations**: Version-controlled schema with Flyway
- **Comprehensive Testing**: Unit, integration, and repository tests
- **Code Coverage**: JaCoCo reports for test coverage metrics

---

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed ([Download](https://www.oracle.com/java/technologies/downloads/))
- **MySQL 8.0+** running locally or accessible remotely ([Download](https://dev.mysql.com/downloads/))
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

**Note**: The application uses `src/main/resources/application.yaml` which references these environment variables. See [Configuration](#-configuration) section for details.

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
  "title": "Sticky Fingers",
  "releaseDate": "1971-04-23",
  "releaseYear": 1971,
  "coverImageUrl": "https://example.com/covers/sticky-fingers.jpg",
  "trackCount": 10,
  "catalogNumber": "COC-59100",
  "artist": {
    "artistId": 1,
    "name": "The Rolling Stones"
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
│   │   │   │   └── MusicLibraryController.java
│   │   │   ├── entity/                         # JPA entities
│   │   │   │   ├── Artist.java
│   │   │   │   ├── Album.java
│   │   │   │   └── Genre.java
│   │   │   ├── repository/                     # Spring Data repositories
│   │   │   │   ├── ArtistRepository.java
│   │   │   │   ├── AlbumRepository.java
│   │   │   │   └── GenreRepository.java
│   │   │   ├── service/                        # Business logic layer
│   │   │   │   ├── ArtistService.java
│   │   │   │   ├── AlbumService.java
│   │   │   │   ├── GenreService.java
│   │   │   │   └── DatabaseResetService.java
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── CreateArtistRequest.java
│   │   │   │   ├── CreateAlbumRequest.java
│   │   │   │   ├── CreateGenreRequest.java
│   │   │   │   ├── ApiInfoResponse.java
│   │   │   │   └── DatabaseResetResponse.java
│   │   │   ├── exception/                      # Exception handling
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ApiError.java
│   │   │   ├── config/                         # Configuration classes
│   │   │   │   └── OpenApiConfig.java
│   │   │   └── specification/                  # JPA Specifications (if used)
│   │   └── resources/
│   │       ├── application.yaml                # Main configuration
│   │       ├── application-test.yaml           # Test profile configuration
│   │       └── db/migration/                   # Flyway migration scripts
│   │           ├── V1__create_initial_schema.sql
│   │           └── V2__add_sample_data.sql
│   └── test/
│       └── java/music/library/
│           ├── integration/                    # Integration tests
│           │   ├── ArtistControllerIT.java
│           │   ├── AlbumControllerIT.java
│           │   ├── AlbumControllerUpdateDeleteIT.java
│           │   └── GenreControllerIT.java
│           ├── service/                        # Service layer tests
│           │   ├── ArtistServiceTest.java
│           │   ├── AlbumServiceTest.java
│           │   └── AlbumServiceBidirectionalTest.java
│           └── repository/                     # Repository tests
│               └── AlbumRepositoryTest.java
├── pom.xml                                     # Maven configuration
├── README.md                                   # This file
├── populate-music-library.bat                  # Windows data loader
├── populate-music-library.sh                   # Unix data loader
└── Music-Library-Sample-Data.postman_collection.json
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
  port: ${PORT:8080}  # Use Railway's dynamic PORT, fallback to 8080 locally

# Database Configuration
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useSSL=true&requireSSL=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}

  # JPA / Hibernate Configuration
  jpa:
    hibernate:
      ddl-auto: validate          # Flyway manages schema, Hibernate only validates
    show-sql: true                # Print SQL statements (debug)

  # Flyway Configuration
  flyway:
    enabled: true                 # Enable Flyway for schema management
    locations: classpath:db/migration

  # Springdoc / Swagger UI
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
  flyway:
    enabled: false
```

---

## 🗄️ Database Migrations

The application uses **Flyway** for version-controlled database schema management.

### Migration Scripts

Migration scripts are located in `src/main/resources/db/migration/`:

- **`V1__create_initial_schema.sql`** - Creates tables for Artist, Album, Genre, and join tables
- **`V2__add_sample_data.sql`** - Populates database with sample data (optional)

### Migration Naming Convention

Flyway follows a strict naming convention:

```
V{version}__{description}.sql
```

Examples:
- `V1__create_initial_schema.sql`
- `V2__add_sample_data.sql`
- `V3__add_catalog_number_column.sql`

### Running Migrations

Migrations run automatically on application startup when Flyway is enabled:

```yaml
spring:
  flyway:
    enabled: true
```

To manually run migrations:

```bash
mvn flyway:migrate
```

To view migration status:

```bash
mvn flyway:info
```

### Migration Best Practices

1. **Never modify existing migrations** - Create new migrations instead
2. **Test migrations locally** before deploying
3. **Use transactions** for data migrations
4. **Keep migrations small** and focused
5. **Document complex migrations** with comments

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

# Run tests with coverage report
mvn verify

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage

The project uses **JaCoCo** for code coverage reporting:

- Coverage reports generated in `target/site/jacoco/`
- HTML reports viewable in browser
- Integrated with Maven `verify` phase

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

## 🚀 Deployment

The application runs locally with MySQL database and can be accessed via **ngrok** for external testing.

### ngrok Deployment

**Live API**: [https://suanne-speedless-chrissy.ngrok-free.dev](https://suanne-speedless-chrissy.ngrok-free.dev)

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
   - ngrok will provide a public URL (e.g., https://suanne-speedless-chrissy.ngrok-free.dev)
   - This URL forwards to your local application

3. **Access the API**
   - Use the ngrok URL to access your API from anywhere
   - Swagger UI: `https://suanne-speedless-chrissy.ngrok-free.dev/swagger-ui.html`
   - API endpoints: `https://suanne-speedless-chrissy.ngrok-free.dev/api`

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

ngrok provides:
- Secure tunneling to localhost
- Public URL for testing and sharing
- Request inspection and replay
- No deployment or hosting costs

#### Note

The ngrok URL changes each time you restart ngrok (unless using a paid plan with reserved domains). Update the URL in your documentation and clients accordingly.

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

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### Development Workflow

1. **Fork the repository**
   ```bash
   git clone https://github.com/yourusername/music-library.git
   cd music-library
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Make your changes**
   - Follow existing code style
   - Add tests for new features
   - Update documentation

4. **Run tests**
   ```bash
   mvn test
   mvn verify  # Includes coverage report
   ```

5. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```

6. **Push to your fork**
   ```bash
   git push origin feature/amazing-feature
   ```

7. **Open a Pull Request**
   - Describe your changes
   - Reference any related issues
   - Ensure CI/CD passes

### Code Style

- Follow Java naming conventions
- Use Lombok annotations to reduce boilerplate
- Add JavaDoc comments for public methods
- Keep methods small and focused
- Use meaningful variable names

### Testing Requirements

- All new features must include tests
- Maintain or improve code coverage
- Tests must pass before merging

---

## 📝 License

This project is part of a Java/MySQL backend development bootcamp (Week 16) and is for **educational purposes**.

---

## 📞 Support

For questions, issues, or feedback:

1. **Check the API documentation**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
2. **Review test examples**: See `src/test/java/music/library/`
3. **Check Postman collection**: `Music-Library-Sample-Data.postman_collection.json`
4. **Open an issue**: [GitHub Issues](https://github.com/yourusername/music-library/issues)

---

## 🎓 Learning Outcomes

This project demonstrates proficiency in:

✅ **Spring Boot 3.x** - Modern Spring framework features  
✅ **RESTful API Design** - Standard HTTP methods and status codes  
✅ **JPA/Hibernate** - Entity relationships and lazy loading  
✅ **Testing** - Unit, integration, and repository tests  
✅ **API Documentation** - OpenAPI/Swagger specification  
✅ **Deployment** - Local development with ngrok tunneling  
✅ **Error Handling** - Global exception handling  
✅ **Rich Content** - 50 artists and 100+ albums with cover images  
✅ **Input Validation** - Bean Validation (JSR-380)  
✅ **DTO Pattern** - Separation of concerns  
✅ **Environment Configuration** - YAML with environment variables  

---

## 🙏 Acknowledgments

- **Spring Boot Team** - Excellent framework and documentation
- **Promineo Tech** - Backend development bootcamp
- **ngrok** - Secure tunneling for local development

---

**Built with ❤️ by JC - Backend Developer**

**Happy coding! 🎵**