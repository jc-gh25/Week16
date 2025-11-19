package music.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Genre;

/**
 * Spring Data JPA repository for Genre entity.
 * 
 * Provides standard CRUD operations and query methods for Genre entities.
 * Spring Data JPA automatically implements this interface at runtime, providing
 * methods like findAll(), findById(), save(), deleteById(), etc.
 * 
 * No custom query methods are needed for this repository as all operations
 * are covered by the standard JpaRepository methods.
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Genre
 * @see JpaRepository
 */
public interface GenreRepository extends JpaRepository<Genre, Long> { }