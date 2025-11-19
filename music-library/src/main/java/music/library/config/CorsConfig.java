package music.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    
    // Allows the Swagger UI (and any client run locally) to call the API.
    // In a real production environment we would replace "*" with
	// the exact origin(s) we want to allow (e.g., https://myfrontend.example.com).
	@Override
    public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
        // Allow all origins for development, or specify the frontend URL
        .allowedOrigins(
            "https://javabc.up.railway.app",
            "http://localhost:3000",
            "http://localhost:8080",
            "*"
        )
        // Allow specific HTTP methods
        .allowedMethods(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "PATCH",
            "OPTIONS"
        )
        // Allow all headers including Content-Type and Authorization
        .allowedHeaders("*")
        // Allow credentials (cookies, authorization headers, etc.)
        .allowCredentials(true)
        // Cache preflight response for 1 hour (3600 seconds)
        .maxAge(3600);
    }
}