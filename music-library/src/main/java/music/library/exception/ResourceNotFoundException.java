package music.library.exception;

/**
 * Custom exception thrown when a requested entity cannot be found in the database.
 * 
 * This exception is used throughout the service layer when attempting to retrieve
 * entities (Artist, Album, Genre) by ID that don't exist. It extends RuntimeException,
 * making it an unchecked exception that doesn't require explicit handling.
 * 
 * When thrown from a service method, this exception is caught by the
 * {@link GlobalExceptionHandler} and automatically translated into a 404 Not Found
 * HTTP response with a standardized {@link ApiError} payload.
 * 
 * Usage Example:
 * <pre>
 * public Artist findById(Long id) {
 *     return repo.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException("Artist with ID " + id + " not found"));
 * }
 * </pre>
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see GlobalExceptionHandler
 * @see ApiError
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Serial version UID for serialization compatibility.
     * Recommended for all serializable subclasses of Throwable.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * 
     * @param message the detail message explaining which resource was not found
     *                (e.g., "Artist with ID 123 not found")
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
