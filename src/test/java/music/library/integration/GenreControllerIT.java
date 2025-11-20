package music.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import music.library.dto.CreateGenreRequest;
import music.library.entity.Genre;
import music.library.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GenreRepository genreRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/genres";
    }

    @Test
    void testCreateGenre() {
        // Use CreateGenreRequest DTO
        CreateGenreRequest genreRequest = new CreateGenreRequest();
        genreRequest.setName("Test Genre");
        genreRequest.setDescription("Test Description");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateGenreRequest> request = new HttpEntity<>(genreRequest, headers);

        ResponseEntity<Genre> response = restTemplate.postForEntity(baseUrl, request, Genre.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Genre");
        assertThat(response.getBody().getDescription()).isEqualTo("Test Description");
        assertThat(response.getBody().getGenreId()).isNotNull();
    }

    @Test
    void testCreateGenreWithoutDescription() {
        // Use CreateGenreRequest DTO
        CreateGenreRequest genreRequest = new CreateGenreRequest();
        genreRequest.setName("Test Genre Without Description");
        // Don't set description to test null handling

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateGenreRequest> request = new HttpEntity<>(genreRequest, headers);

        ResponseEntity<Genre> response = restTemplate.postForEntity(baseUrl, request, Genre.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Genre Without Description");
        assertThat(response.getBody().getGenreId()).isNotNull();
        // Description can be null
    }

    @Test
    void testGetAllGenres() {
        // Create test genre
        Genre genre = new Genre();
        genre.setName("Test Genre");
        genre.setDescription("Test Description");
        genreRepository.save(genre);

        // Use a more flexible approach to handle the Page response
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("content");
        
        // Extract the content array and verify it has the expected data
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> content = (java.util.List<Map<String, Object>>) response.getBody().get("content");
        assertThat(content).hasSize(1);
        assertThat(content.get(0).get("name")).isEqualTo("Test Genre");
        assertThat(content.get(0).get("description")).isEqualTo("Test Description");
    }

    @Test
    void testGetGenreById() {
        // Create test genre
        Genre genre = new Genre();
        genre.setName("Test Genre");
        genre.setDescription("Test Description");
        Genre savedGenre = genreRepository.save(genre);

        ResponseEntity<Genre> response = restTemplate.getForEntity(
                baseUrl + "/" + savedGenre.getGenreId(),
                Genre.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Genre");
        assertThat(response.getBody().getDescription()).isEqualTo("Test Description");
        assertThat(response.getBody().getGenreId()).isEqualTo(savedGenre.getGenreId());
    }

    @Test
    void testGetGenreByIdNotFound() {
        ResponseEntity<Genre> response = restTemplate.getForEntity(
                baseUrl + "/999",
                Genre.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateGenre() {
        // Create test genre
        Genre genre = new Genre();
        genre.setName("Original Genre");
        genre.setDescription("Original Description");
        Genre savedGenre = genreRepository.save(genre);

        // Update genre
        Genre updatedGenre = new Genre();
        updatedGenre.setName("Updated Genre");
        updatedGenre.setDescription("Updated Description");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Genre> request = new HttpEntity<>(updatedGenre, headers);

        ResponseEntity<Genre> response = restTemplate.exchange(
                baseUrl + "/" + savedGenre.getGenreId(),
                HttpMethod.PUT,
                request,
                Genre.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Genre");
        assertThat(response.getBody().getDescription()).isEqualTo("Updated Description");
        assertThat(response.getBody().getGenreId()).isEqualTo(savedGenre.getGenreId());
    }

    @Test
    void testDeleteGenre() {
        // Create test genre
        Genre genre = new Genre();
        genre.setName("Test Genre");
        genre.setDescription("Test Description");
        Genre savedGenre = genreRepository.save(genre);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + savedGenre.getGenreId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify genre is deleted
        assertThat(genreRepository.findById(savedGenre.getGenreId())).isEmpty();
    }

}