package music.library.exception;


// Thrown when a requested entity (Artist, Album, Genre) cannot be found.
// Throwing this from a service method will be caught by
// {@link GlobalExceptionHandler} and translated into a 404 response.
 
public class ResourceNotFoundException extends RuntimeException {
	// Recommended for all serializable subclasses.
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}