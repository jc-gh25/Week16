package music.library.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;    // for explicit init
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import music.library.dto.CreateAlbumRequest;
import music.library.dto.UpdateAlbumRequest;
import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.exception.DuplicateResourceException;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;
import music.library.repository.ArtistRepository;
import music.library.repository.GenreRepository;
import music.library.specification.AlbumSpecs;

/**
 * Service layer for Album entity business logic.
 * 
 * Handles all album-related operations including CRUD, relationship management
 * (artist and genres), and advanced search/filtering capabilities. All methods
 * are transactional to ensure data consistency and proper Hibernate session management.
 * 
 * Key Responsibilities:
 * - Album CRUD operations with validation
 * - Managing many-to-many relationships with genres (bidirectional sync)
 * - Managing many-to-one relationship with artists
 * - Advanced search with multiple criteria (title, release year range, genre)
 * - Pagination support for all list operations
 * 
 * Transaction Management: All methods run within a transaction to guarantee
 * a Hibernate Session exists for the entire method execution. This ensures
 * lazy-loaded collections can be accessed and bidirectional relationships
 * remain synchronized.
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Album
 * @see AlbumRepository
 * @see AlbumSpecs
 */
@Service
@RequiredArgsConstructor
@Transactional  // Guarantees a Hibernate Session lives for the entire method execution
public class AlbumService {

	// Repository dependencies injected via Lombok's @RequiredArgsConstructor
	private final AlbumRepository albumRepo;
	private final GenreRepository genreRepo;
	private final ArtistRepository artistRepo;
	
	// ========== CRUD Operations ==========

	/**
	 * Retrieves all albums without pagination.
	 * Note: Use paginated version for production to avoid loading large datasets.
	 * 
	 * @return list of all albums
	 */
	public List<Album> findAll() {
		return albumRepo.findAll();
	}

	/**
	 * Retrieves all albums with pagination support.
	 * 
	 * @param pageable pagination parameters (page, size, sort)
	 * @return paginated list of albums
	 */
	public Page<Album> findAll(Pageable pageable) {
		return albumRepo.findAll(pageable);
	}

	/**
	 * Retrieves a single album by ID.
	 * 
	 * @param id the album ID
	 * @return the album entity
	 * @throws ResourceNotFoundException if album not found
	 */
	public Album findById(Long id) {
		return albumRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				"Album with id " + id + " not found"));
	}

	/**
	 * Creates a new album from an Album entity.
	 * Note: Prefer createAlbum(CreateAlbumRequest) for API endpoints.
	 * 
	 * @param a the album entity to create
	 * @return the persisted album with generated ID and timestamps
	 */
	public Album create(Album a) {
		// Check for duplicate album title (case-insensitive)
		if (albumRepo.existsByTitleIgnoreCase(a.getTitle())) {
			throw new DuplicateResourceException("Album with title '" + a.getTitle() + "' already exists");
		}
		return albumRepo.save(a);
	}

	/**
	 * Creates a new album using a DTO with artist and genre IDs.
	 * This is the preferred method for API endpoints as it validates
	 * that referenced entities exist before creating the album.
	 * 
	 * Business Logic:
	 * 1. Validates artist exists (throws exception if not found)
	 * 2. Validates all genre IDs exist (throws exception if any not found)
	 * 3. Creates album with bidirectional relationships properly set
	 * 4. Persists album with auto-generated ID and timestamps
	 * 
	 * @param request DTO containing title, releaseDate, artistId, and optional genreIds
	 * @return the created album entity with all relationships loaded
	 * @throws ResourceNotFoundException if artist or any genre ID not found
	 */
	public Album createAlbum(CreateAlbumRequest request) {
	    // Check for duplicate album title (case-insensitive)
	    if (albumRepo.existsByTitleIgnoreCase(request.getTitle())) {
	        throw new DuplicateResourceException("Album with title '" + request.getTitle() + "' already exists");
	    }
	    
	    // Fetch and validate the artist exists
	    Artist artist = artistRepo.findById(request.getArtistId())
	        .orElseThrow(() -> new ResourceNotFoundException(
	            "Artist with ID " + request.getArtistId() + " not found"));
	    
	    // Fetch and validate all genres exist
	    List<Genre> genres = new ArrayList<>();
	    if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
	        System.out.println("=== DEBUG: Request genreIds: " + request.getGenreIds());
	        for (Long genreId : request.getGenreIds()) {
	            Genre genre = genreRepo.findById(genreId)
	                .orElseThrow(() -> new ResourceNotFoundException(
	                    "Genre with ID " + genreId + " not found"));
	            System.out.println("=== DEBUG: Fetched genre: " + genre.getGenreId() + " - " + genre.getName());
	            genres.add(genre);
	        }
	        System.out.println("=== DEBUG: Total genres in list: " + genres.size());
	    }
	    
	    // Build the album entity with validated relationships
	    Album album = new Album();
	    album.setTitle(request.getTitle());
	    album.setReleaseDate(request.getReleaseDate());
	    album.setCoverImageUrl(request.getCoverImageUrl());
	    album.setTrackCount(request.getTrackCount());
	    album.setCatalogNumber(request.getCatalogNumber());
	    album.setArtist(artist);
	    
	    Set<Genre> genreSet = new HashSet<>(genres);
	    System.out.println("=== DEBUG: Genres in HashSet: " + genreSet.size());
	    for (Genre g : genreSet) {
	        System.out.println("=== DEBUG: Genre in set: " + g.getGenreId());
	    }
	    
	    album.setGenres(genreSet);
	    
	    return albumRepo.save(album);
	}

	/**
	 * Updates an existing album using a DTO with artist and genre IDs.
	 * This is the preferred method for API endpoints as it validates
	 * that referenced entities exist before updating the album.
	 * 
	 * Business Logic:
	 * 1. Validates album exists (throws exception if not found)
	 * 2. Validates artist exists (throws exception if not found)
	 * 3. Validates all genre IDs exist (throws exception if any not found)
	 * 4. Updates album fields with new values
	 * 5. Replaces genre associations if genreIds provided
	 * 6. Persists updated album
	 * 
	 * @param id the album ID to update
	 * @param request DTO containing updated title, releaseDate, artistId, and optional genreIds
	 * @return the updated album entity with all relationships loaded
	 * @throws ResourceNotFoundException if album, artist, or any genre ID not found
	 */
	public Album updateAlbum(Long id, UpdateAlbumRequest request) {
	    // Fetch and validate the album exists
	    Album album = findById(id);
	    
	    // Fetch and validate the artist exists
	    Artist artist = artistRepo.findById(request.getArtistId())
	        .orElseThrow(() -> new ResourceNotFoundException(
	            "Artist with ID " + request.getArtistId() + " not found"));
	    
	    // Fetch and validate all genres exist (if provided)
	    List<Genre> genres = new ArrayList<>();
	    if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
	        for (Long genreId : request.getGenreIds()) {
	            Genre genre = genreRepo.findById(genreId)
	                .orElseThrow(() -> new ResourceNotFoundException(
	                    "Genre with ID " + genreId + " not found"));
	            genres.add(genre);
	        }
	    }
	    
	    // Update the album fields
	    album.setTitle(request.getTitle());
	    album.setReleaseDate(request.getReleaseDate());
	    album.setCoverImageUrl(request.getCoverImageUrl());
	    album.setTrackCount(request.getTrackCount());
	    album.setCatalogNumber(request.getCatalogNumber());
	    album.setArtist(artist);
	    
	    // Update genres if provided (replaces existing genre associations)
	    if (request.getGenreIds() != null) {
	        Set<Genre> genreSet = new HashSet<>(genres);
	        album.setGenres(genreSet);
	    }
	    
	    return albumRepo.save(album);
	}

	/**
	 * Updates an existing album.
	 * Only updates title, releaseDate, and artist. Genre updates should use
	 * addGenre/removeGenre methods to maintain bidirectional consistency.
	 * 
	 * @param id the album ID to update
	 * @param a the album entity with updated values
	 * @return the updated album entity
	 * @throws ResourceNotFoundException if album not found
	 */
	public Album update(Long id, Album a) {
		Album existing = findById(id);
		existing.setTitle(a.getTitle());
		existing.setReleaseDate(a.getReleaseDate());
		existing.setArtist(a.getArtist()); // Optional: allows changing the artist
		return albumRepo.save(existing);
	}

	/**
	 * Deletes an album by ID.
	 * Note: Cascade behavior depends on entity configuration. Genre relationships
	 * are automatically cleaned up via join table.
	 * 
	 * @param id the album ID to delete
	 * @throws ResourceNotFoundException if album not found
	 */
	public void delete(Long id) {
		// Check if album exists before deleting - throws 404 if not found
		findById(id);
		albumRepo.deleteById(id);
	}

	// ========== Many-to-Many Relationship Management ==========
	
	/**
	 * Adds a genre to an album's genre collection.
	 * Maintains bidirectional relationship consistency by updating both sides.
	 * 
	 * Important: Album owns the relationship (via @JoinTable), so changes to
	 * album.genres are what actually modify the join table. However, we also
	 * update genre.albums to keep the object graph consistent in memory.
	 * 
	 * @param albumId the album ID
	 * @param genreId the genre ID to add
	 * @return the updated album with the new genre
	 * @throws ResourceNotFoundException if album or genre not found
	 */
	public Album addGenre(Long albumId, Long genreId) {
		Album album = findById(albumId);
		Genre genre = genreRepo.findById(genreId).orElseThrow(() -> new ResourceNotFoundException(
				"Genre with ID " + genreId + " not found"));
		album.getGenres().add(genre); // Owning side - this updates the join table
		genre.getAlbums().add(album); // Inverse side - keeps object graph consistent
        Hibernate.initialize(genre.getAlbums()); // Force initialization while transaction is open
		albumRepo.save(album);
		genreRepo.save(genre);
		return album;
	}

	/**
	 * Removes a genre from an album's genre collection.
	 * Maintains bidirectional relationship consistency by updating both sides.
	 * 
	 * @param albumId the album ID
	 * @param genreId the genre ID to remove
	 * @return the updated album without the removed genre
	 * @throws ResourceNotFoundException if album or genre not found
	 */
	public Album removeGenre(Long albumId, Long genreId) {
		Album album = findById(albumId);
		Genre genre = genreRepo.findById(genreId).orElseThrow(() -> new ResourceNotFoundException(
				"Genre with ID " + genreId + " not found"));
		album.getGenres().remove(genre); // Owning side - updates join table
		genre.getAlbums().remove(album); // Inverse side - keeps object graph consistent
		Hibernate.initialize(genre.getAlbums()); // Initialize before transaction ends
		// Explicit save makes intent clear (transaction will flush at commit anyway)
		albumRepo.save(album);
		genreRepo.save(genre);
		return album;
	}
	
	// ========== Search and Filtering ==========
	
	/**
	 * Searches albums with multiple optional criteria using JPA Specifications.
	 * All criteria are combined with AND logic. If no criteria provided, returns all albums.
	 * 
	 * Supported Filters:
	 * - title: case-insensitive partial match
	 * - startYear/endYear: release year range (inclusive)
	 * - genreId: albums that have this genre
	 * 
	 * @param title partial title to search (case-insensitive), null to skip
	 * @param startYear minimum release year (inclusive), null for no lower bound
	 * @param endYear maximum release year (inclusive), null for no upper bound
	 * @param genreId genre ID to filter by, null to skip
	 * @param pageable pagination parameters
	 * @return paginated search results
	 */
	public Page<Album> search(String title, Integer startYear, Integer endYear, Long genreId, Pageable pageable) {

		Specification<Album> spec = null;

		// Build specification dynamically based on provided criteria
		if (title != null && !title.isBlank()) {
		    spec = AlbumSpecs.titleContains(title);
		}
		if (startYear != null || endYear != null) {
		    spec = (spec == null)
		            ? AlbumSpecs.releasedBetween(startYear, endYear)
		            : spec.and(AlbumSpecs.releasedBetween(startYear, endYear));
		}
		if (genreId != null) {
		    spec = (spec == null)
		            ? AlbumSpecs.hasGenre(genreId)
		            : spec.and(AlbumSpecs.hasGenre(genreId));
		}

		// Null spec means "match everything" - returns all albums with pagination
		return albumRepo.findAll(spec, pageable);
	}
	
	/**
	 * Retrieves all albums by a specific artist.
	 * Uses Spring Data JPA property expression (artist.artistId).
	 * 
	 * @param artistId the artist ID
	 * @return list of albums by the artist (empty if none found)
	 */
	public List<Album> findByArtistId(Long artistId) {
	    return albumRepo.findByArtist_ArtistId(artistId);
	}

	/**
	 * Retrieves all albums in a specific genre.
	 * Uses Spring Data JPA property expression for many-to-many (genres.genreId).
	 * 
	 * @param genreId the genre ID
	 * @return list of albums in the genre (empty if none found)
	 */
	public List<Album> findByGenreId(Long genreId) {
	    return albumRepo.findByGenres_GenreId(genreId);
	}
}
