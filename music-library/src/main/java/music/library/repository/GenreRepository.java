package music.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Genre;

/**
 * Spring Data JPA repository for Genre entity.
 * 
 * Provides standard CRUD operations and query methods for Genre entities.
 * Spring Data JPA automatically implements this interface at runtime, providing
 * methods like findAll(), findById(), save(), deleteById(), etc.
 * 
 * Custom query methods for duplicate checking:
 * - findByNameIgnoreCase: Finds a genre by name (case-insensitive)
 * - existsByNameIgnoreCase: Checks if a genre with the given name exists
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Genre
 * @see JpaRepository
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    /**
     * Finds a genre by name (case-insensitive).
     * Used for duplicate checking before creating new genres.
     * 
     * @param name the genre name to search for
     * @return Optional containing the genre if found, empty otherwise
     */
    Optional<Genre> findByNameIgnoreCase(String name);
    
    /**
     * Checks if a genre with the given name exists (case-insensitive).
     * More efficient than findByNameIgnoreCase when only existence check is needed.
     * 
     * @param name the genre name to check
     * @return true if a genre with this name exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
