package music.library.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import music.library.entity.Artist;
import music.library.exception.DuplicateResourceException;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.ArtistRepository;
import music.library.dto.CreateArtistRequest;
import music.library.dto.UpdateArtistRequest;

/**
 * Service layer for Artist entity business logic.
 * 
 * Handles all artist-related operations including CRUD operations with validation.
 * All methods are transactional to ensure data consistency.
 * 
 * Key Responsibilities:
 * - Artist CRUD operations
 * - DTO-based creation for API endpoints
 * - Pagination support for list operations
 * - Duplicate prevention for artist names
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Artist
 * @see ArtistRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {

    // Repository dependency injected via Lombok's @RequiredArgsConstructor
    private final ArtistRepository repo;

    /**
     * Retrieves all artists without pagination.
     * Note: Use paginated version for production to avoid loading large datasets.
     * 
     * @return list of all artists
     */
    public List<Artist> findAll() {
        return repo.findAll();
    }
    
    /**
     * Retrieves all artists with pagination support.
     * Applies default sorting by name in ascending order if no sort is specified.
     * 
     * @param pageable pagination parameters (page, size, sort)
     * @return paginated list of artists
     */
    public Page<Artist> findAll(Pageable pageable) {
        // Apply default sort if none specified
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("name").ascending()
            );
        }
        return repo.findAll(pageable);
    }

    /**
     * Retrieves a single artist by ID.
     * 
     * @param id the artist ID
     * @return the artist entity
     * @throws ResourceNotFoundException if artist not found
     */
    public Artist findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
        		"Artist with ID " + id + " not found"));
    }

    /**
     * Creates a new artist from an Artist entity.
     * Checks for duplicate artist names before saving.
     * Note: Prefer create(CreateArtistRequest) for API endpoints.
     * 
     * @param a the artist entity to create
     * @return the persisted artist with generated ID and timestamps
     * @throws DuplicateResourceException if an artist with the same name already exists
     */
    public Artist create(Artist a) {
        // Check for duplicate artist name (case-insensitive)
        if (repo.existsByNameIgnoreCase(a.getName())) {
            throw new DuplicateResourceException("Artist with name '" + a.getName() + "' already exists");
        }
        return repo.save(a);
    }
    
    /**
     * Creates a new artist using a DTO.
     * Checks for duplicate artist names before saving.
     * This is the preferred method for API endpoints as it uses validated request objects.
     * 
     * @param request DTO containing name and bio
     * @return ResponseEntity with HTTP 201 (CREATED) status and the created artist entity
     * @throws DuplicateResourceException if an artist with the same name already exists
     */
    public ResponseEntity<Artist> create(CreateArtistRequest request) {
        // Check for duplicate artist name (case-insensitive)
        if (repo.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Artist with name '" + request.getName() + "' already exists");
        }
        
        Artist artist = new Artist();
        artist.setName(request.getName());
        artist.setDescription(request.getBio());
        Artist savedArtist = repo.save(artist);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArtist);
    }

    /**
     * Updates an existing artist using a DTO.
     * This is the preferred method for API endpoints as it uses validated request objects.
     * 
     * Business Logic:
     * 1. Validates artist exists (throws exception if not found)
     * 2. Updates artist fields with new values from the DTO
     * 3. Persists updated artist
     * 
     * @param id the artist ID to update
     * @param request DTO containing updated name, bio, country, formedYear, website, and imageUrl
     * @return the updated artist entity
     * @throws ResourceNotFoundException if artist not found
     */
    public Artist updateArtist(Long id, UpdateArtistRequest request) {
        // Fetch and validate the artist exists
        Artist artist = findById(id);
        
        // Update the artist fields
        artist.setName(request.getName());
        artist.setDescription(request.getBio());
        
        return repo.save(artist);
    }

    /**
     * Updates an existing artist.
     * Note: Prefer updateArtist(UpdateArtistRequest) for API endpoints.
     * 
     * @param id the artist ID to update
     * @param a the artist entity with updated values
     * @return the updated artist entity
     * @throws ResourceNotFoundException if artist not found
     */
    public Artist update(Long id, Artist a) {
        Artist existing = findById(id);
        existing.setName(a.getName());
        existing.setDescription(a.getDescription());
        return repo.save(existing);
    }

    /**
     * Deletes an artist by ID.
     * Transactional annotation ensures clean deletion with cascade of orphaned albums.
     * This guarantees database consistency when deleting artists.
     * 
     * @param id the artist ID to delete
     */
    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
