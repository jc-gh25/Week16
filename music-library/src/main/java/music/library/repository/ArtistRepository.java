package music.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Artist;

/**
 * Spring Data JPA repository for Artist entity.
 * 
 * Provides standard CRUD operations and query methods for Artist entities.
 * Spring Data JPA automatically implements this interface at runtime, providing
 * methods like findAll(), findById(), save(), deleteById(), etc.
 * 
 * Custom query methods for duplicate checking:
 * - findByNameIgnoreCase: Finds an artist by name (case-insensitive)
 * - existsByNameIgnoreCase: Checks if an artist with the given name exists
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Artist
 * @see JpaRepository
 */
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    /**
     * Finds an artist by name (case-insensitive).
     * Used for duplicate checking before creating new artists.
     * 
     * @param name the artist name to search for
     * @return Optional containing the artist if found, empty otherwise
     */
    Optional<Artist> findByNameIgnoreCase(String name);
    
    /**
     * Checks if an artist with the given name exists (case-insensitive).
     * More efficient than findByNameIgnoreCase when only existence check is needed.
     * 
     * @param name the artist name to check
     * @return true if an artist with this name exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
