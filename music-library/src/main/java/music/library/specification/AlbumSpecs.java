package music.library.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import music.library.entity.Album;
import music.library.entity.Genre;

/**
 * Helper class that builds JPA Specifications for Album entity queries.
 * 
 * This class provides static factory methods that create reusable query predicates
 * for filtering Album entities. Each method returns a {@link Specification} that
 * can be combined using {@code Specification.where(...).and(...).or(...)} to build
 * complex dynamic queries.
 * 
 * Specifications use the JPA Criteria API under the hood, providing type-safe
 * queries that are compiled at build time rather than runtime. This approach
 * is more flexible than Spring Data JPA's method name queries and safer than
 * string-based JPQL or native SQL.
 * 
 * Usage Example:
 * <pre>
 * Specification<Album> spec = AlbumSpecs.titleContains("rock")
 *     .and(AlbumSpecs.releasedBetween(1970, 1980))
 *     .and(AlbumSpecs.hasGenre(genreId));
 * Page<Album> results = albumRepo.findAll(spec, pageable);
 * </pre>
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see Album
 * @see Specification
 * @see music.library.service.AlbumService#search
 */
public class AlbumSpecs {

    /**
     * Creates a specification that matches albums whose title contains the given term.
     * The search is case-insensitive and matches partial strings.
     * 
     * @param term the search term to match (case-insensitive)
     * @return a Specification that filters albums by title containing the term
     * 
     * Example: titleContains("rock") matches "Rock Album", "Classic Rock", "ROCK"
     */
    public static Specification<Album> titleContains(String term) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + term.toLowerCase() + "%");
    }

    /**
     * Creates a specification that matches albums released between two years (inclusive).
     * Either or both bounds can be null to create open-ended ranges.
     * 
     * @param startYear the minimum release year (inclusive), null for no lower bound
     * @param endYear the maximum release year (inclusive), null for no upper bound
     * @return a Specification that filters albums by release year range, or null if both bounds are null
     * 
     * Examples:
     * - releasedBetween(1970, 1980) matches albums from 1970-01-01 to 1980-12-31
     * - releasedBetween(2000, null) matches albums from 2000 onwards
     * - releasedBetween(null, 1990) matches albums up to 1990
     * - releasedBetween(null, null) returns null (no filtering)
     */
    public static Specification<Album> releasedBetween(Integer startYear, Integer endYear) {
        return (root, query, cb) -> {
            // No filtering if both bounds are null
            if (startYear == null && endYear == null) {
                return null;
            }
            
            // Both bounds provided - use BETWEEN
            if (startYear != null && endYear != null) {
                return cb.between(root.get("releaseDate"),
                                  LocalDate.of(startYear, 1, 1),
                                  LocalDate.of(endYear, 12, 31));
            }
            
            // Only start year provided - greater than or equal to
            if (startYear != null) {
                return cb.greaterThanOrEqualTo(root.get("releaseDate"),
                                              LocalDate.of(startYear, 1, 1));
            }
            
            // Only end year provided - less than or equal to
            return cb.lessThanOrEqualTo(root.get("releaseDate"),
                                        LocalDate.of(endYear, 12, 31));
        };
    }

    /**
     * Creates a specification that matches albums linked to a specific genre.
     * Uses a JOIN on the many-to-many genres relationship.
     * 
     * @param genreId the primary key of the genre to filter by, null to skip filtering
     * @return a Specification that filters albums by genre, or null if genreId is null
     * 
     * Example: hasGenre(5L) matches all albums that have genre with ID 5
     */
    public static Specification<Album> hasGenre(Long genreId) {
        return (root, query, cb) -> {
            if (genreId == null) return null;
            
            // Join the genres collection (many-to-many relationship)
            // Using join(String) allows type inference to work: Join<Album, Genre>
            Join<Album, Genre> join = root.join("genres");
            
            // Filter by the genre's primary key
            return cb.equal(join.get("genreId"), genreId);
        };
    }
}
