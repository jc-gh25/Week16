package music.library.dto;

/**
 * Data Transfer Object (DTO) for the database reset operation response.
 * 
 * This DTO is returned by the DELETE /api/reset endpoint to provide
 * feedback to the client about the success of the database reset operation.
 * 
 * @author Music Library API
 * @version 1.0
 */
public class DatabaseResetResponse {
    
    /**
     * A descriptive message indicating the result of the database reset operation.
     * 
     * Example: "Database reset successfully. All data has been deleted and 
     * auto-increment sequences have been reset to 1."
     */
    private String message;

    /**
     * Default constructor for JSON deserialization.
     * Required by Jackson for proper DTO mapping.
     */
    public DatabaseResetResponse() {
    }

    /**
     * Constructor to create a DatabaseResetResponse with a specific message.
     * 
     * @param message the response message describing the result of the reset operation
     */
    public DatabaseResetResponse(String message) {
        this.message = message;
    }

    /**
     * Gets the response message from the database reset operation.
     * 
     * @return the message describing the result of the reset
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message for the database reset operation.
     * 
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}