package music.library.integration;

import music.library.entity.Genre;
import music.library.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for Genre Controller endpoints.
 * 
 * Tests cover all CRUD operations for the Genre API including:
 * - Retrieving all genres
 * - Retrieving a single genre by ID
 * - Creating new genres
 * - Error handling for invalid IDs
 * 
 * Uses @SpringBootTest with WebEnvironment.RANDOM_PORT to start a full
 * application context and TestRestTemplate for HTTP communication.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Genre Controller Integration Tests")
class GenreControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GenreRepository genreRepository;

    private Genre testGenre;
    private Genre anotherGenre;

    /**
     * Setup method that runs before each test.
     * Clears the repository and creates test data.
     */
    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();

        testGenre = new Genre();
        testGenre.setName("Rock");
        testGenre.setDescription("Rock music genre characterized by electric guitars and strong beats");
        testGenre = genreRepository.save(testGenre);

        anotherGenre = new Genre();
        anotherGenre.setName("Jazz");
        anotherGenre.setDescription("Jazz music genre known for improvisation and complex harmonies");
        anotherGenre = genreRepository.save(anotherGenre);
    }

    /**
     * Test: GET /api/genres - Retrieve all genres
     * 
     * Verifies that:
     * - The endpoint returns HTTP 200 OK
     * - The response contains all saved genres
     * - The response body is not null
     */
    @Test
    @DisplayName("Should retrieve all genres successfully")
    void testGetAllGenres() {
        // When
        ResponseEntity<Genre[]> response = restTemplate.getForEntity(
                "/api/genres",
                Genre[].class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(response.getBody())
                .extracting(Genre::getName)
                .contains("Rock", "Jazz");
    }

    /**
     * Test: GET /api/genres/{id} - Retrieve a single genre by ID
     * 
     * Verifies that:
     * - The endpoint returns HTTP 200 OK
     * - The returned genre has correct properties
     * - The genre ID matches the requested ID
     */
    @Test
    @DisplayName("Should retrieve a single genre by ID")
    void testGetGenreById() {
        // When
        ResponseEntity<Genre> response = restTemplate.getForEntity(
                "/api/genres/{id}",
                Genre.class,
                testGenre.getGenreId()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGenreId()).isEqualTo(testGenre.getGenreId());
        assertThat(response.getBody().getName()).isEqualTo("Rock");
        assertThat(response.getBody().getDescription())
                .isEqualTo("Rock music genre characterized by electric guitars and strong beats");
    }

    /**
     * Test: POST /api/genres - Create a new genre
     * 
     * Verifies that:
     * - The endpoint returns HTTP 201 CREATED
     * - The created genre has the correct properties
     * - The genre is persisted in the database
     */
    @Test
    @DisplayName("Should create a new genre successfully")
    void testCreateGenre() {
        // Given
        Genre newGenre = new Genre();
        newGenre.setName("Pop");
        newGenre.setDescription("Popular music genre with catchy melodies and broad appeal");

        // When
        ResponseEntity<Genre> response = restTemplate.postForEntity(
                "/api/genres",
                newGenre,
                Genre.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGenreId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Pop");
        assertThat(response.getBody().getDescription())
                .isEqualTo("Popular music genre with catchy melodies and broad appeal");

        // Verify persistence
        assertThat(genreRepository.findById(response.getBody().getGenreId()))
                .isPresent()
                .hasValueSatisfying(genre -> 
                    assertThat(genre.getName()).isEqualTo("Pop")
                );
    }

    /**
     * Test: GET /api/genres/{id} - Error handling for invalid genre ID
     * 
     * Verifies that:
     * - The endpoint returns HTTP 404 NOT FOUND for non-existent genre
     * - The response indicates the resource was not found
     */
    @Test
    @DisplayName("Should return 404 when genre ID does not exist")
    void testGetGenreByInvalidId() {
        // Given
        Long invalidId = 99999L;

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/genres/{id}",
                String.class,
                invalidId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
