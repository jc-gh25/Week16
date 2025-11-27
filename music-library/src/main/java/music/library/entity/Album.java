package music.library.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "album")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //Collections should never participate in equals()/hashCode()
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Album {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long albumId;

	@Column(nullable = false, length = 255, unique = true)
	@NotBlank(message = "Album title must not be blank")
	@Size(max = 255, message = "Album title must be ≤ 255 characters")
	private String title;
	// private Integer releaseYear; // keep for quick year filtering

	// NEW FIELD: release date. Some releases have an exact day/month (e.g., a
	// summer compilation).
	// Having a LocalDate gives you richer sorting and filtering options.
	// Keeping a derived getReleaseYear() method preserves backward compatibility
	// for any code that still expects a year.
	private LocalDate releaseDate; // stores full date; optional

	@Transient // not persisted, calculated on the fly
	public Integer getReleaseYear() {
		return (releaseDate != null) ? releaseDate.getYear() : null;
	}

	// NEW FIELD: cover image URL. Most music apps display album art. Even a
	// placeholder URL
	// (e.g., a link to a public image or a local static folder) makes the API feel
	// more real.
	@Size(max = 255, message = "Cover image URL too long")
	private String coverImageUrl;

	// NEW FIELD: track count. Useful for UI sorting and shows you can extend the
	// model
	// without breaking existing APIs
	@PositiveOrZero(message = "Track count must be zero or positive")
	private Integer trackCount; // optional

	// NEW FIELD: catalog number. OPTIONAL - may be null or empty.
	@Column(name = "catalog_number", length = 50, unique = true, nullable = true)
	@Size(max = 50, message = "Catalog number must be ≤ 50 characters")
	private String catalogNumber; // optional
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

	/* Many-to-one with Artist */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "artist_id", nullable = false)
	@NotNull(message = "Artist must be supplied")
	private Artist artist;

	/* Owner side of the many-to-many with Genre via join table */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "album_genre", 
	    joinColumns = @JoinColumn(name = "album_id"), 
	    inverseJoinColumns = @JoinColumn(name = "genre_id"))
	@Builder.Default
	private Set<Genre> genres = new HashSet<>();

	@PrePersist
	void onCreate() {
		createdAt = updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
