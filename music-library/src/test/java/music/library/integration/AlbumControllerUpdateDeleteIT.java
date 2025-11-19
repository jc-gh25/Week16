package music.library.integration;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.repository.AlbumRepository;
import music.library.repository.ArtistRepository;
import music.library.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for Album Controller update and delete operations.
 * 
 * Tests cover update and delete operations for the Album API including:
 * - Updating an existing album
 * - Deleting an existing album
 * - Error handling for update/delete on non-existent albums
 * 
 * Uses @SpringBootTest with WebEnvironment.RANDOM_PORT to start a full
 * application context and TestRestTemplate for HTTP communication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Album Controller Update/Delete Integration Tests")
class AlbumControllerUpdateDeleteIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Album testAlbum;
    private Artist testArtist;
    private Genre testGenre;

    /**
     * Setup method that runs before each test.
     * Clears the repositories and creates test data including artist, genre, and album.
     */
    @BeforeEach
    void setUp() {
        albumRepository.deleteAll();
        artistRepository.deleteAll();
        genreRepository.deleteAll();

        // Create test artist
        testArtist = new Artist();
        testArtist.setName("Pink Floyd");
        testArtist.setDescription("English rock band known for progressive rock");
        testArtist = artistRepository.save(testArtist);

        // Create test genre
        testGenre = new Genre();
        testGenre.setName("Rock");
        testGenre.setDescription("Rock music genre characterized by electric guitars");
        testGenre = genreRepository.save(testGenre);

        // Create test album
        testAlbum = new Album();
        testAlbum.setTitle("The Wall");
        testAlbum.setReleaseDate(LocalDate.of(1979, 1, 1));
        testAlbum.setArtist(testArtist);
        testAlbum.setGenres(Set.of(testGenre));
        testAlbum = albumRepository.save(testAlbum);
    }

    /**
     * Test: PUT /api/albums/{id} - Update an existing album
     * 
     * Verifies that:
     * - The endpoint returns HTTP 200 OK
     * - The album properties are updated correctly
     * - The updated album is persisted in the database
     */
    @Test
    @DisplayName("Should update an existing album successfully")
    void testUpdateAlbum() {
        // Given
        Album updatedAlbum = new Album();
        updatedAlbum.setTitle("The Wall (Remastered)");
        updatedAlbum.setReleaseDate(LocalDate.of(2011, 1, 1));
        updatedAlbum.setArtist(testArtist);
        updatedAlbum.setGenres(Set.of(testGenre));

        // When
        ResponseEntity<Album> response = restTemplate.exchange(
                "/api/albums/{id}",
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updatedAlbum),
                Album.class,
                testAlbum.getAlbumId()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAlbumId()).isEqualTo(testAlbum.getAlbumId());
        assertThat(response.getBody().getTitle()).isEqualTo("The Wall (Remastered)");
        assertThat(response.getBody().getReleaseYear()).isEqualTo(2011);

        // Verify persistence
        assertThat(albumRepository.findById(testAlbum.getAlbumId()))
                .isPresent()
                .hasValueSatisfying(album -> {
                    assertThat(album.getTitle()).isEqualTo("The Wall (Remastered)");
                    assertThat(album.getReleaseYear()).isEqualTo(2011);
                });
    }

    /**
     * Test: DELETE /api/albums/{id} - Delete an existing album
     * 
     * Verifies that:
     * - The endpoint returns HTTP 204 NO CONTENT
     * - The album is removed from the database
     */
    @Test
    @DisplayName("Should delete an existing album successfully")
    void testDeleteAlbum() {
        // Given
        Long albumId = testAlbum.getAlbumId();
        assertThat(albumRepository.findById(albumId)).isPresent();

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/albums/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                albumId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify deletion
        assertThat(albumRepository.findById(albumId)).isNotPresent();
    }

    /**
     * Test: PUT /api/albums/{id} - Error handling for update on non-existent album
     * 
     * Verifies that:
     * - The endpoint returns HTTP 404 NOT FOUND for non-existent album
     * - The response indicates the resource was not found
     */
    @Test
    @DisplayName("Should return 404 when updating non-existent album")
    void testUpdateNonExistentAlbum() {
        // Given
        Long invalidId = 99999L;
        Album updatedAlbum = new Album();
        updatedAlbum.setTitle("Non-existent Album");
        updatedAlbum.setReleaseDate(LocalDate.of(2020, 1, 1));
        updatedAlbum.setArtist(testArtist);
        updatedAlbum.setGenres(Set.of(testGenre));

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/albums/{id}",
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updatedAlbum),
                String.class,
                invalidId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test: DELETE /api/albums/{id} - Error handling for delete on non-existent album
     * 
     * Verifies that:
     * - The endpoint returns HTTP 404 NOT FOUND for non-existent album
     * - The response indicates the resource was not found
     */
    @Test
    @DisplayName("Should return 404 when deleting non-existent album")
    void testDeleteNonExistentAlbum() {
        // Given
        Long invalidId = 99999L;

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/albums/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                invalidId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
