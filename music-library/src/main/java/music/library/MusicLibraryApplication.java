package music.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Music Library Spring Boot application.
 * 
 * This class bootstraps the entire application, triggering Spring Boot's
 * auto-configuration mechanism which automatically configures:
 * - Embedded Tomcat web server
 * - Spring MVC for REST endpoints
 * - JPA/Hibernate for database access
 * - Jackson for JSON serialization
 * - SpringDoc OpenAPI for Swagger documentation
 * 
 * The @SpringBootApplication annotation is a convenience annotation that combines:
 * - @Configuration: Marks this as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Scans for components in this package and sub-packages
 * 
 * Application Configuration:
 * - Database settings: src/main/resources/application.properties
 * - Flyway migrations: src/main/resources/db/migration/
 * - Static content: src/main/resources/static/
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
 
@SpringBootApplication  // Enables auto-configuration, component scan, and configuration
public class MusicLibraryApplication {

    /**
     * Main method that launches the Spring Boot application.
     * 
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        SpringApplication.run(MusicLibraryApplication.class, args);
    }
}
