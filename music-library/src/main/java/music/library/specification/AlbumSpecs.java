package music.library.specification;

import music.library.entity.Album;
import music.library.entity.Genre;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;

/*
 * Helper class that builds {@link Specification}s for {@link Album}.
 * Each static method returns a predicate that can be combined with
 * {@code Specification.where(...).and(...)} etc.
 */
public class AlbumSpecs {

	/** Title contains (case‑insensitive) */
    public static Specification<Album> titleContains(String term) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + term.toLowerCase() + "%");
    }

    /** Release date between two years (inclusive). Null bounds are ignored. */
    public static Specification<Album> releasedBetween(Integer startYear, Integer endYear) {
        return (root, query, cb) -> {
            if (startYear == null && endYear == null) {
                return null;   // no filtering on date
            }
            if (startYear != null && endYear != null) {
                return cb.between(root.get("releaseDate"),
                                  LocalDate.of(startYear, 1, 1),
                                  LocalDate.of(endYear, 12, 31));
            }
            if (startYear != null) {
                return cb.greaterThanOrEqualTo(root.get("releaseDate"),
                                              LocalDate.of(startYear, 1, 1));
            }
            // only endYear supplied
            return cb.lessThanOrEqualTo(root.get("releaseDate"),
                                        LocalDate.of(endYear, 12, 31));
        };
    }

    /*
     * Filter albums that are linked to a specific genre.
     * @param genreId the primary‑key of the genre; if null the predicate is omitted
     */
    public static Specification<Album> hasGenre(Long genreId) {
        return (root, query, cb) -> {
            if (genreId == null) return null;
            //use the overload join(String, JoinType) so the generic type can be inferred as Join<Album, Genre>.
            Join<Album, Genre> join = root.join("genres"); // property name in Album
            return cb.equal(join.get("genreId"), genreId);
        };
    }
}
