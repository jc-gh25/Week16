package music.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for creating a new Album.
 * This DTO accepts ID references instead of full entity objects,
 * avoiding cascading validation issues.
 * The releaseDate field is optional and can be null if the release date is unknown.
 */
public class CreateAlbumRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private LocalDate releaseDate;
    
    @NotNull(message = "Artist ID is required")
    private Long artistId;
    
    @NotEmpty(message = "At least one genre ID is required")
    private List<Long> genreIds;
    
	private String coverImageUrl;
    private Integer trackCount;
    private String catalogNumber;

    
    // Constructors
    public CreateAlbumRequest() {
    }
    
    public CreateAlbumRequest(String title, LocalDate releaseDate, Long artistId, List<Long> genreIds) {
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
        return "CreateAlbumRequest{" +
                "title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", artistId=" + artistId +
                ", genreIds=" + genreIds +
                '}';
    }
}
