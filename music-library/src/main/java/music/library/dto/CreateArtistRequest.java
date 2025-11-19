package music.library.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for creating a new Artist.
 * This DTO is used to capture the required and optional information
 * needed to create an artist in the music library system.
 * The bio field is optional and can be null if biographical information is not available.
 */
public class CreateArtistRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String bio;
    
    // Constructors
    public CreateArtistRequest() {
    }
    
    public CreateArtistRequest(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    @Override
    public String toString() {
        return "CreateArtistRequest{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}
