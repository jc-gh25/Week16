package music.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    /**
     * Configures OpenAPI/Swagger with comprehensive API metadata.
     * This configuration includes title, version, description, contact info, and license.
     * 
     * The API documentation is available at:
     * - Swagger UI: /swagger-ui.html
     * - OpenAPI JSON: /v3/api-docs
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Music Library API")
                        .version("1.0")
                        .description("REST API for managing artists, albums, and genres. " +
                                "This API provides full CRUD operations for music library management, " +
                                "including artist profiles, album collections, and genre categorization.")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@musiclibrary.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("AWS Server"),
                        // Local development server
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }

    /**
     * Groups Artist-related API endpoints.
     * All endpoints under /api/artists/** will be grouped together in the Swagger UI.
     */
    @Bean
    public GroupedOpenApi artistApi() {
        return GroupedOpenApi.builder()
                .group("artists")
                .pathsToMatch("/api/artists/**")
                .build();
    }

    /**
     * Groups Album-related API endpoints.
     * All endpoints under /api/albums/** will be grouped together in the Swagger UI.
     */
    @Bean
    public GroupedOpenApi albumApi() {
        return GroupedOpenApi.builder()
                .group("albums")
                .pathsToMatch("/api/albums/**")
                .build();
    }

    /**
     * Groups Genre-related API endpoints.
     * All endpoints under /api/genres/** will be grouped together in the Swagger UI.
     */
    @Bean
    public GroupedOpenApi genreApi() {
        return GroupedOpenApi.builder()
                .group("genres")
                .pathsToMatch("/api/genres/**")
                .build();
    }

    /**
     * Groups Database-related API endpoints.
     * All endpoints under /api/database/** will be grouped together in the Swagger UI.
     */
    @Bean
    public GroupedOpenApi databaseApi() {
        return GroupedOpenApi.builder()
                .group("database")
                .pathsToMatch("/api/database/**")
                .build();
    }

    /**
     * Groups all public API endpoints.
     * This provides a unified view of all API endpoints under /api/**.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("music-library-api")
                .pathsToMatch("/api/**")
                .build();
    }
}
