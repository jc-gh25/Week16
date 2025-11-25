package music.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * Data Transfer Object for updating an existing Artist.
 * This DTO accepts simple field values for updating artist information.
 * 
 * Note: For updates, the name field is required to ensure data integrity.
 * All other fields are optional and can be null if not being updated.
 */
public class UpdateArtistRequest {
    
    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    @Min(value = 1000, message = "Formed year must be 1000 or later")
    @Max(value = 9999, message = "Formed year must be 9999 or earlier")
    private Integer formedYear;
    
    @Size(max = 500, message = "Website URL must not exceed 500 characters")
    private String website;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    
    // Constructors
    public UpdateArtistRequest() {
    }
    
    public UpdateArtistRequest(String name, String bio, String country, Integer formedYear, String website, String imageUrl) {
        this.name = name;
        this.bio = bio;
        this.country = country;
        this.formedYear = formedYear;
        this.website = website;
        this.imageUrl = imageUrl;
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Integer getFormedYear() {
        return formedYear;
    }
    
    public void setFormedYear(Integer formedYear) {
        this.formedYear = formedYear;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Override
    public String toString() {
        return "UpdateArtistRequest{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", country='" + country + '\'' +
                ", formedYear=" + formedYear +
                ", website='" + website + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
