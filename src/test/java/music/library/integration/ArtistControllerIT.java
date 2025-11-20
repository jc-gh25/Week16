package music.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import music.library.dto.CreateArtistRequest;
import music.library.entity.Artist;
import music.library.repository.ArtistRepository;
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
public class ArtistControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/artists";
    }

    @Test
    void testCreateArtist() {
        // Use CreateArtistRequest DTO with correct field name (bio, not description)
        CreateArtistRequest artistRequest = new CreateArtistRequest();
        artistRequest.setName("Test Artist");
        artistRequest.setBio("Test Description for the artist"); // Use 'bio' field from DTO

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateArtistRequest> request = new HttpEntity<>(artistRequest, headers);

        ResponseEntity<Artist> response = restTemplate.postForEntity(baseUrl, request, Artist.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Artist");
        assertThat(response.getBody().getDescription()).isEqualTo("Test Description for the artist");
        assertThat(response.getBody().getArtistId()).isNotNull();
    }

    @Test
    void testCreateArtistWithoutBiography() {
        // Use CreateArtistRequest DTO
        CreateArtistRequest artistRequest = new CreateArtistRequest();
        artistRequest.setName("Test Artist Without Bio");
        // Don't set bio to test null handling

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateArtistRequest> request = new HttpEntity<>(artistRequest, headers);

        ResponseEntity<Artist> response = restTemplate.postForEntity(baseUrl, request, Artist.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Artist Without Bio");
        assertThat(response.getBody().getArtistId()).isNotNull();
        // Description can be null
    }

    @Test
    void testGetAllArtists() {
        // Create test artist
        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist.setDescription("Test Description");
        artistRepository.save(artist);

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
        assertThat(content.get(0).get("name")).isEqualTo("Test Artist");
        assertThat(content.get(0).get("description")).isEqualTo("Test Description");
    }

    @Test
    void testGetArtistById() {
        // Create test artist
        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist.setDescription("Test Description");
        Artist savedArtist = artistRepository.save(artist);

        ResponseEntity<Artist> response = restTemplate.getForEntity(
                baseUrl + "/" + savedArtist.getArtistId(),
                Artist.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test Artist");
        assertThat(response.getBody().getDescription()).isEqualTo("Test Description");
        assertThat(response.getBody().getArtistId()).isEqualTo(savedArtist.getArtistId());
    }

    @Test
    void testGetArtistByIdNotFound() {
        ResponseEntity<Artist> response = restTemplate.getForEntity(
                baseUrl + "/999",
                Artist.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateArtist() {
        // Create test artist
        Artist artist = new Artist();
        artist.setName("Original Artist");
        artist.setDescription("Original Description");
        Artist savedArtist = artistRepository.save(artist);

        // Update artist
        Artist updatedArtist = new Artist();
        updatedArtist.setName("Updated Artist");
        updatedArtist.setDescription("Updated Description");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Artist> request = new HttpEntity<>(updatedArtist, headers);

        ResponseEntity<Artist> response = restTemplate.exchange(
                baseUrl + "/" + savedArtist.getArtistId(),
                HttpMethod.PUT,
                request,
                Artist.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Updated Artist");
        assertThat(response.getBody().getDescription()).isEqualTo("Updated Description");
        assertThat(response.getBody().getArtistId()).isEqualTo(savedArtist.getArtistId());
    }

    @Test
    void testDeleteArtist() {
        // Create test artist
        Artist artist = new Artist();
        artist.setName("Test Artist");
        artist.setDescription("Test Description");
        Artist savedArtist = artistRepository.save(artist);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + savedArtist.getArtistId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify artist is deleted
        assertThat(artistRepository.findById(savedArtist.getArtistId())).isEmpty();
    }
}