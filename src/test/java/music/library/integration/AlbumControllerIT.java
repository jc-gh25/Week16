package music.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import music.library.dto.CreateAlbumRequest;
import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.repository.AlbumRepository;
import music.library.repository.ArtistRepository;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AlbumControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private Artist testArtist;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/albums";
        
        // Create and save test artist
        testArtist = new Artist();
        testArtist.setName("Test Artist");
        testArtist.setDescription("Test Description");
        testArtist = artistRepository.save(testArtist);
        
        // Create and save test genre
        testGenre = new Genre();
        testGenre.setName("Test Genre");
        testGenre.setDescription("Test Description");
        testGenre = genreRepository.save(testGenre);
    }

    @Test
    void testCreateAlbum() {
        // Create album request DTO to avoid detached entity issues
        CreateAlbumRequest albumRequest = new CreateAlbumRequest();
        albumRequest.setTitle("Test Album");
        albumRequest.setReleaseDate(LocalDate.of(2023, 1, 1));
        albumRequest.setArtistId(testArtist.getArtistId());
        albumRequest.setGenreIds(List.of(testGenre.getGenreId()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateAlbumRequest> request = new HttpEntity<>(albumRequest, headers);

        ResponseEntity<Album> response = restTemplate.postForEntity(baseUrl, request, Album.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Album");
        assertThat(response.getBody().getAlbumId()).isNotNull();
    }

    @Test
    @Transactional
    void testGetAllAlbums() {
        // Create test album in a transactional context to ensure proper entity management
        Album album = createTestAlbum("Test Album");

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
        assertThat(content.get(0).get("title")).isEqualTo("Test Album");
    }

    @Test
    @Transactional
    void testGetAlbumById() {
        // Create test album in a transactional context
        Album savedAlbum = createTestAlbum("Test Album");

        ResponseEntity<Album> response = restTemplate.getForEntity(
                baseUrl + "/" + savedAlbum.getAlbumId(),
                Album.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test Album");
        assertThat(response.getBody().getAlbumId()).isEqualTo(savedAlbum.getAlbumId());
    }

    @Test
    void testGetAlbumByIdNotFound() {
        ResponseEntity<Album> response = restTemplate.getForEntity(
                baseUrl + "/999",
                Album.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * Helper method to create a test album with proper entity management
     */
    private Album createTestAlbum(String title) {
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(LocalDate.of(2023, 1, 1));
        
        // Fetch fresh managed entities to avoid detached entity issues
        Artist managedArtist = artistRepository.findById(testArtist.getArtistId()).orElseThrow();
        Genre managedGenre = genreRepository.findById(testGenre.getGenreId()).orElseThrow();
        
        album.setArtist(managedArtist);
        
        // Create a new HashSet and add the managed genre to avoid detached entity issues
        Set<Genre> genres = new HashSet<>();
        genres.add(managedGenre);
        album.setGenres(genres);
        
        return albumRepository.save(album);
    }
}