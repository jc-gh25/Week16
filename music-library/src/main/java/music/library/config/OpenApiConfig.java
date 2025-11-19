package music.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Music Library API")
                        .description("Demo API for the boot‑camp final project – CRUD for Artists, Albums, Genres")
                        .version("1.0.0"));
    }
}