package music.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    
    // Allows the Swagger UI (and any client run locally) to call the API.
    // In a real production environment we would replace "*" with
	// the exact origin(s) we want to allow (e.g., https://myfrontend.example.com).
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {  // tells Spring MVC to add the CORS
            	// response headers to every request.
            // "**" means every path (/**) – can narrow it to /api/**
                registry.addMapping("/**")
                      .allowedOrigins("*")  // makes the API publicly callable from any origin (for dev only) 
                        .allowedMethods("*")  // GET,POST,PUT,DELETE,OPTIONS,...
                        .allowedHeaders("*")  // any request header
                        .allowCredentials(false);  // not needed for simple API calls
            }
        };
    }
}