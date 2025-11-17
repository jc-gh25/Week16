package music.library.repository;

import music.library.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/* @DataJpaTest boots an in-memory database (H2 by default) and scans only JPA components. 
 * Can be used to check that entity relationships, unique constraints, and column definitions work. 
 * Unique constraint – ensures the catalog_number column really is unique.
 * Bidirectional many-to-many – confirms that both sides of the relationship (Album.genres ↔ Genre.albums) 
 * are persisted correctly.
 */

@DataJpaTest
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepo;

    @Autowired
    private ArtistRepository artistRepo;

    @Autowired
    private GenreRepository genreRepo;

    @Test
    @DisplayName("Saving an Album with a duplicate catalogNumber should violate the unique constraint")
    void uniqueCatalogNumber_constraint() {
        // Arrange – create an artist first (required FK)
        Artist artist = new Artist();
        artist.setName("Various Artists");
        artist = artistRepo.save(artist);

        Album a1 = new Album();
        a1.setTitle("First Album");
        a1.setArtist(artist);
        a1.setCatalogNumber("SILKA001");
        a1.setReleaseDate(LocalDate.of(2020, 1, 1));
        albumRepo.save(a1);

        Album a2 = new Album();
        a2.setTitle("Second Album");
        a2.setArtist(artist);
        a2.setCatalogNumber("SILKA001"); // duplicate!
        a2.setReleaseDate(LocalDate.of(2021, 1, 1));

        // Act / Assert
        assertThatThrownBy(() -> albumRepo.saveAndFlush(a2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Many‑to‑many relationship persists correctly")
    void albumGenre_bidirectional() {
        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist = artistRepo.save(artist);

        Genre rock = new Genre();
        rock.setName("Rock");
        rock = genreRepo.save(rock);

        Album album = new Album();
        album.setTitle("Rock Album");
        album.setArtist(artist);
        album.setReleaseDate(LocalDate.of(2022, 5, 5));
        album.setGenres(new HashSet<>(Set.of(rock)));

        album = albumRepo.saveAndFlush(album);

        // Reload from DB to verify join table
        Album loaded = albumRepo.findById(album.getAlbumId()).orElseThrow();

        assertThat(loaded.getGenres())
                .extracting(Genre::getName)
                .containsExactly("Rock");

        // Verify the inverse side (optional)
        Genre loadedRock = genreRepo.findById(rock.getGenreId()).orElseThrow();
        assertThat(loadedRock.getAlbums())
                .extracting(Album::getTitle)
                .containsExactly("Rock Album");
    }
}