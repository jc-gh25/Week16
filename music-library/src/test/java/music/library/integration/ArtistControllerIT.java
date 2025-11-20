package music.library.integration;

import music.library.entity.Artist;
import music.library.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for Artist Controller endpoints.
 * 
 * Tests cover all CRUD operations for the Artist API including:
 * - Retrieving all artists
 * - Retrieving a single artist by ID
 * - Creating new artists
 * - Error handling for invalid IDs and request bodies
 * 
 * Uses @SpringBootTest with WebEnvironment.RANDOM_PORT to start a full
 * application context and TestRestTemplate for HTTP communication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Artist Controller Integration Tests")
class ArtistControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArtistRepository artistRepository;

    private Artist testArtist;
    private Artist anotherArtist;

    /**
     * Setup method that runs before each test.
     * Clears the repository and creates test data.
     */
    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();

        testArtist = new Artist();
        testArtist.setName("The Beatles");
        testArtist.setDescription("British rock band from Liverpool");
        testArtist = artistRepository.save(testArtist);

        anotherArtist = new Artist();
        anotherArtist.setName("Pink Floyd");
        anotherArtist.setDescription("English rock band known for progressive rock");
        anotherArtist = artistRepository.save(anotherArtist);
    }

    /**
     * Test: GET /api/artists - Retrieve all artists
     * 
     * Verifies that:
     * - The endpoint returns HTTP 200 OK
     * - The response contains all saved artists
     * - The response body is not null
     */
    @Test
    @DisplayName("Should retrieve all artists successfully")
    void testGetAllArtists() {
        // When
        ResponseEntity<RestResponsePage<Artist>> response = restTemplate.exchange(
                "/api/artists",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<Artist>>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<Artist> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(page.getContent())
                .extracting(Artist::getName)
                .contains("The Beatles", "Pink Floyd");
    }

    /**
     * Test: GET /api/artists/{id} - Retrieve a single artist by ID
     * 
     * Verifies that:
     * - The endpoint returns HTTP 200 OK
     * - The returned artist has correct properties
     * - The artist ID matches the requested ID
     */
    @Test
    @DisplayName("Should retrieve a single artist by ID")
    void testGetArtistById() {
        // When
        ResponseEntity<Artist> response = restTemplate.getForEntity(
                "/api/artists/{id}",
                Artist.class,
                testArtist.getArtistId()
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getArtistId()).isEqualTo(testArtist.getArtistId());
        assertThat(response.getBody().getName()).isEqualTo("The Beatles");
        assertThat(response.getBody().getDescription())
                .isEqualTo("British rock band from Liverpool");
    }

    /**
     * Test: POST /api/artists - Create a new artist
     * 
     * Verifies that:
     * - The endpoint returns HTTP 201 CREATED
     * - The created artist has the correct properties
     * - The artist is persisted in the database
     */
    @Test
    @DisplayName("Should create a new artist successfully")
    void testCreateArtist() {
        // Given
        Artist newArtist = new Artist();
        newArtist.setName("David Bowie");
        newArtist.setDescription("English rock musician and actor");

        // When
        ResponseEntity<Artist> response = restTemplate.postForEntity(
                "/api/artists",
                newArtist,
                Artist.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getArtistId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("David Bowie");
        assertThat(response.getBody().getDescription())
                .isEqualTo("English rock musician and actor");

        // Verify persistence
        assertThat(artistRepository.findById(response.getBody().getArtistId()))
                .isPresent()
                .hasValueSatisfying(artist -> 
                    assertThat(artist.getName()).isEqualTo("David Bowie")
                );
    }

    /**
     * Test: GET /api/artists/{id} - Error handling for invalid artist ID
     * 
     * Verifies that:
     * - The endpoint returns HTTP 404 NOT FOUND for non-existent artist
     * - The response indicates the resource was not found
     */
    @Test
    @DisplayName("Should return 404 when artist ID does not exist")
    void testGetArtistByInvalidId() {
        // Given
        Long invalidId = 99999L;

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/artists/{id}",
                String.class,
                invalidId
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Test: POST /api/artists - Error handling for invalid request body
     * 
     * Verifies that:
     * - The endpoint returns HTTP 400 BAD REQUEST for invalid input
     * - Missing required fields are properly rejected
     */
    @Test
    @DisplayName("Should return 400 when creating artist with invalid request body")
    void testCreateArtistWithInvalidBody() {
        // Given
        Artist invalidArtist = new Artist();
        // Missing required name field

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/artists",
                invalidArtist,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * RestResponsePage is a Jackson-friendly wrapper for Spring Data's Page interface.
     * Since Page is an interface, Jackson cannot instantiate it directly during deserialization.
     * This class extends PageImpl and provides a @JsonCreator constructor that Jackson can use
     * to properly deserialize paginated REST responses.
     */
    static class RestResponsePage<T> extends PageImpl<T> {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public RestResponsePage(@JsonProperty("content") List<T> content,
                                @JsonProperty("number") int number,
                                @JsonProperty("size") int size,
                                @JsonProperty("totalElements") Long totalElements,
                                @JsonProperty("pageable") JsonNode pageable,
                                @JsonProperty("last") boolean last,
                                @JsonProperty("totalPages") int totalPages,
                                @JsonProperty("sort") JsonNode sort,
                                @JsonProperty("first") boolean first,
                                @JsonProperty("numberOfElements") int numberOfElements) {
            super(content, PageRequest.of(number, size), totalElements);
        }

        public RestResponsePage(List<T> content) {
            super(content);
        }
    }
}
