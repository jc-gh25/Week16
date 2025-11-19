# Music Library API

A RESTful API for managing a music library built with Spring Boot and MySQL. This project provides endpoints for managing artists, albums, and songs with full CRUD operations.

## 🎵 Project Overview

The Music Library API allows you to:
- Manage artists with biographical information
- Organize albums by artists
- Track songs within albums
- Perform comprehensive searches and filtering
- Handle relationships between artists, albums, and songs

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA / Hibernate
- **API Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Java Version**: 17+

## 📋 Prerequisites

Before running this application, ensure you have:

- Java 17 or higher installed
- MySQL 8.0+ running locally or accessible remotely
- Maven 3.6+ for building the project
- Git for version control

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Week16
```

### 2. Database Setup
Create a MySQL database for the application:
```sql
CREATE DATABASE music_library;
CREATE USER 'music_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON music_library.* TO 'music_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure Database Connection
Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/music_library
spring.datasource.username=music_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### 5. Load Sample Data (Optional)
To populate the database with sample data, run the provided scripts:

**Windows:**
```bash
populate-music-library.bat
```

**Linux/Mac:**
```bash
chmod +x populate-music-library.sh
./populate-music-library.sh
```

## 📚 API Documentation

Once the application is running, you can access the interactive API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

## 🎯 API Endpoints

### Artists
- `GET /api/artists` - Get all artists (paginated)
- `GET /api/artists/{id}` - Get artist by ID
- `POST /api/artists` - Create new artist
- `PUT /api/artists/{id}` - Update artist
- `DELETE /api/artists/{id}` - Delete artist

### Albums
- `GET /api/albums` - Get all albums (paginated)
- `GET /api/albums/{id}` - Get album by ID
- `GET /api/albums/artist/{artistId}` - Get albums by artist
- `POST /api/albums` - Create new album
- `PUT /api/albums/{id}` - Update album
- `DELETE /api/albums/{id}` - Delete album

### Songs
- `GET /api/songs` - Get all songs (paginated)
- `GET /api/songs/{id}` - Get song by ID
- `GET /api/songs/album/{albumId}` - Get songs by album
- `POST /api/songs` - Create new song
- `PUT /api/songs/{id}` - Update song
- `DELETE /api/songs/{id}` - Delete song

## 📊 Data Models

### Artist
```json
{
  "artistId": 1,
  "name": "The Rolling Stones",
  "description": "Legendary British rock band",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Album
```json
{
  "albumId": 1,
  "title": "Sticky Fingers",
  "releaseYear": 1971,
  "artistId": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Song
```json
{
  "songId": 1,
  "title": "Brown Sugar",
  "duration": 219,
  "albumId": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## 🔍 Query Parameters

Most GET endpoints support pagination and sorting:
- `page` - Page number (default: 0)
- `size` - Items per page (default: 20)
- `sort` - Sort criteria (e.g., `name,asc` or `createdAt,desc`)

Example:
```
GET /api/artists?page=0&size=10&sort=name,asc
```

## 🧪 Testing with Postman

A comprehensive Postman collection is included in the project:
- **Collection File**: `Music-Library-Sample-Data.postman_collection.json`
- **Documentation**: `README-SAMPLE-DATA.md`

Import the collection into Postman to test all endpoints with sample data.

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/javabc/musiclibrary/
│   │   ├── controller/          # REST controllers
│   │   ├── model/              # Entity classes
│   │   ├── repository/         # Data access layer
│   │   ├── service/            # Business logic
│   │   └── MusicLibraryApplication.java
│   └── resources/
│       ├── application.properties
│       └── static/             # Static web content
└── test/                       # Unit and integration tests
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server configuration
server.port=8080

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/music_library
spring.datasource.username=music_user
spring.datasource.password=your_password

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Swagger configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🚨 Error Handling

The API includes comprehensive error handling with appropriate HTTP status codes:
- `200 OK` - Successful requests
- `201 Created` - Successful resource creation
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of a Java/MySQL bootcamp (Week 16) and is for educational purposes.

## 📞 Support

For questions or issues:
1. Check the API documentation at `/swagger-ui.html`
2. Review the sample data documentation in `README-SAMPLE-DATA.md`
3. Test endpoints using the provided Postman collection

---

**Happy coding! 🎵**