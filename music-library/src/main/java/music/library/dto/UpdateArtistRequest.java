package music.library.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for updating an existing Artist.
 * This DTO accepts simple field values for updating artist information.
 * 
 * Note: For updates, the name field is required to ensure data integrity.
 * All other fields are optional and can be null if not being updated.
 */
public class UpdateArtistRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String bio;
    private String country;
    private Integer formedYear;
    private String website;
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
