package music.library.exception;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * Uniform error payload returned by {@link GlobalExceptionHandler}.
 */
@Getter
@Builder
public class ApiError {

    // When the error occurred
	@Builder.Default
    private Instant timestamp = Instant.now();  // UTC instant

    // HTTP status code (e.g., 400, 404, 500). 
    private final int status;

    /** Short reason phrase (e.g., "Bad Request"). */
    private final String error;

    // Human‑readable message – can be a single string or a concatenated list.
    private final String message;

    // The request path that triggered the error (e.g., "/api/albums").
    private final String path;

    // Optional list of field‑level validation errors.
    private final List<String> validationErrors;
    
    
    
}