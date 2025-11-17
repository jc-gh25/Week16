package music.library.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Uniform error payload returned by {@link GlobalExceptionHandler}.
 */
@Getter
@Builder
public class ApiError {

    /** Timestamp when the error occurred (ISO 8601). */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final LocalDateTime timestamp;

    /** HTTP status code (e.g., 400, 404, 500). */
    private final int status;

    /** Short reason phrase (e.g., "Bad Request"). */
    private final String error;

    /** Human‑readable message – can be a single string or a concatenated list. */
    private final String message;

    /** The request path that triggered the error (e.g., "/api/albums"). */
    private final String path;

    /** Optional list of field‑level validation errors. */
    private final List<String> validationErrors;
    
    
    
}