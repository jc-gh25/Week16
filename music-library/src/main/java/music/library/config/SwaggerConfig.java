package music.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    /**
     * Configures OpenAPI/Swagger to use HTTPS URLs.
     * This fixes the "Mixed Content" error where Swagger UI (loaded over HTTPS)
     * tries to make requests to HTTP endpoints.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Music Library API")
                        .version("1.0")
                        .description("REST API for managing artists, albums, and genres"))
                .servers(List.of(
                        // Production server (Railway) - HTTPS
                        new Server()
                                .url("https://javabc.up.railway.app")
                                .description("Production Server (Railway)"),
                        // Local development server (optional)
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }
}
