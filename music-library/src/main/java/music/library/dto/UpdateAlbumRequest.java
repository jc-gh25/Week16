package music.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for updating an existing Album.
 * This DTO accepts ID references instead of full entity objects,
 * avoiding cascading validation issues.
 * The releaseDate field is optional and can be null if the release date is unknown.
 * 
 * Note: For updates, the artist field is required to ensure data integrity.
 * Genre IDs can be updated to change the album's genre associations.
 */
public class UpdateAlbumRequest {
    
    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    private LocalDate releaseDate;
    
    @NotNull(message = "Artist ID is required")
    @Positive(message = "Artist ID must be a positive number")
    private Long artistId;
    
    private List<@Positive(message = "Genre ID must be a positive number") Long> genreIds;
    
    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageUrl;
    
    @Min(value = 1, message = "Track count must be at least 1")
    private Integer trackCount;
    
    @Size(max = 100, message = "Catalog number must not exceed 100 characters")
    private String catalogNumber;

    
    // Constructors
    public UpdateAlbumRequest() {
    }
    
    public UpdateAlbumRequest(String title, LocalDate releaseDate, Long artistId, List<Long> genreIds) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.artistId = artistId;
        this.genreIds = genreIds;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public Long getArtistId() {
        return artistId;
    }
    
    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }
    
    public List<Long> getGenreIds() {
        return genreIds;
    }
    
    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }
    
    @Override
    public String toString() {
        return "UpdateAlbumRequest{" +
                "title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", artistId=" + artistId +
                ", genreIds=" + genreIds +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", trackCount=" + trackCount +
                ", catalogNumber='" + catalogNumber + '\'' +
                '}';
    }
}
