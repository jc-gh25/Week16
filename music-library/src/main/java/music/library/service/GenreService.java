package music.library.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import music.library.entity.Album;
import music.library.entity.Genre;
import music.library.exception.DuplicateResourceException;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;
import music.library.repository.GenreRepository;
import music.library.dto.CreateGenreRequest;
import music.library.dto.UpdateGenreRequest;

/**
 * Service layer for Genre entity business logic.
 * 
 * Handles all genre-related operations including CRUD operations and
 * bidirectional relationship management with albums. All methods are
 * transactional to ensure data consistency.
 * 
 * Key Responsibilities:
 * - Genre CRUD operations with validation
 * - DTO-based creation for API endpoints
 * - Managing many-to-many relationships with albums
 * - Pagination support for list operations
 * - Duplicate prevention for genre names
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Genre
 * @see GenreRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GenreService {

    // Repository dependencies injected via Lombok's @RequiredArgsConstructor
    private final GenreRepository repo;
    private final AlbumRepository albumRepo;   // Needed for bidirectional album-genre operations

    /**
     * Retrieves all genres without pagination.
     * Note: Use paginated version for production to avoid loading large datasets.
     * 
     * @return list of all genres
     */
    public List<Genre> findAll() {
        return repo.findAll();
    }
    
    /**
     * Retrieves all genres with pagination support.
     * 
     * @param pageable pagination parameters (page, size, sort)
     * @return paginated list of genres
     */
    public Page<Genre> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /**
     * Retrieves a single genre by ID.
     * 
     * @param id the genre ID
     * @return the genre entity
     * @throws ResourceNotFoundException if genre not found
     */
    public Genre findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
        		"Genre with ID " + id + " not found"));
    }

    /**
     * Creates a new genre from a Genre entity.
     * Checks for duplicate genre names before saving.
     * Note: Prefer create(CreateGenreRequest) for API endpoints.
     * 
     * @param g the genre entity to create
     * @return the persisted genre with generated ID and timestamps
     * @throws DuplicateResourceException if a genre with the same name already exists
     */
    public Genre create(Genre g) {
        // Check for duplicate genre name (case-insensitive)
        if (repo.existsByNameIgnoreCase(g.getName())) {
            throw new DuplicateResourceException("Genre with name '" + g.getName() + "' already exists");
        }
        return repo.save(g);
    }
    
    /**
     * Creates a new genre using a DTO.
     * Checks for duplicate genre names before saving.
     * This is the preferred method for API endpoints as it uses validated request objects.
     * 
     * @param request DTO containing name and description
     * @return the created genre entity with generated ID and timestamps
     * @throws DuplicateResourceException if a genre with the same name already exists
     */
    public Genre create(CreateGenreRequest request) {
        // Check for duplicate genre name (case-insensitive)
        if (repo.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Genre with name '" + request.getName() + "' already exists");
        }
        
        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        return repo.save(genre);
    }
	
	public Genre updateGenre(Long id, UpdateGenreRequest request) {
    Genre genre = findById(id);
    genre.setName(request.getName());
    genre.setDescription(request.getDescription());
    return repo.save(genre);
}

    /**
     * Updates an existing genre.
     * 
     * @param id the genre ID to update
     * @param g the genre entity with updated values
     * @return the updated genre entity
     * @throws ResourceNotFoundException if genre not found
     */
    public Genre update(Long id, Genre g) {
        Genre existing = findById(id);
        existing.setName(g.getName());
        existing.setDescription(g.getDescription());
        return repo.save(existing);
    }

    /**
     * Deletes a genre by ID.
     * Note: Cascade behavior depends on entity configuration. If albums reference
     * this genre, the operation may fail with a constraint violation.
     * 
     * @param id the genre ID to delete
     */
    public void delete(Long id) {
        repo.deleteById(id);
    }
    
    // ========== Many-to-Many Relationship Management ==========
    
    /**
     * Adds an album to a genre's album collection.
     * Maintains bidirectional relationship consistency by updating both sides.
     * 
     * Note: Album owns the relationship (via @JoinTable), so changes to
     * album.genres are what actually modify the join table. This method
     * updates the inverse side (genre.albums) for consistency.
     * 
     * @param genreId the genre ID
     * @param albumId the album ID to add
     * @return the updated genre with the new album
     * @throws ResourceNotFoundException if genre or album not found
     */
    public Genre addAlbum(Long genreId, Long albumId) {
        Genre genre = repo.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with ID " + genreId + " not found"));
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album with ID " + albumId + " not found"));
        genre.getAlbums().add(album);
        return repo.save(genre);
    }

    /**
     * Removes an album from a genre's album collection.
     * Maintains bidirectional relationship consistency by updating both sides.
     * 
     * @param genreId the genre ID
     * @param albumId the album ID to remove
     * @return the updated genre without the removed album
     * @throws ResourceNotFoundException if genre or album not found
     */
    public Genre removeAlbum(Long genreId, Long albumId) {
        Genre genre = repo.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with ID " + genreId + " not found"));
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album with ID " + albumId + " not found"));
        genre.getAlbums().remove(album);
        return repo.save(genre);
    }
}
