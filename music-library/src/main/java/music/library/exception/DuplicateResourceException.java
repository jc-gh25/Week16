package music.library.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 * Used to prevent duplicate entries for entities with unique constraints.
 * 
 * This exception should be caught by the GlobalExceptionHandler and returned
 * as an HTTP 409 Conflict response.
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
