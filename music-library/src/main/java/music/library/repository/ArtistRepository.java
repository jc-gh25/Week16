package music.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Artist;

/**
 * Spring Data JPA repository for Artist entity.
 * 
 * Provides standard CRUD operations and query methods for Artist entities.
 * Spring Data JPA automatically implements this interface at runtime, providing
 * methods like findAll(), findById(), save(), deleteById(), etc.
 * 
 * No custom query methods are needed for this repository as all operations
 * are covered by the standard JpaRepository methods.
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Artist
 * @see JpaRepository
 */
public interface ArtistRepository extends JpaRepository<Artist, Long> { }
