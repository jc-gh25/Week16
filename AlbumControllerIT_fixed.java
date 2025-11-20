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
import java.util.List;
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
    private Long testArtistId;
    private Long testGenreId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/albums";
        
        // Create and save test artist - store only ID to avoid detached entity issues
        Artist testArtist = new Artist();
        testArtist.setName("Test Artist");
        testArtist.setDescription("Test Description");
        testArtist = artistRepository.save(testArtist);
        testArtistId = testArtist.getArtistId();
        
        // Create and save test genre - store only ID to avoid detached entity issues
        Genre testGenre = new Genre();
        testGenre.setName("Test Genre");
        testGenre.setDescription("Test Description");
        testGenre = genreRepository.save(testGenre);
        testGenreId = testGenre.getGenreId();
    }

    @Test
    void testCreateAlbum() {
        // Create album request DTO to avoid detached entity issues
        CreateAlbumRequest albumRequest = new CreateAlbumRequest();
        albumRequest.setTitle("Test Album");
        albumRequest.setReleaseDate(LocalDate.of(2023, 1, 1));
        albumRequest.setArtistId(testArtistId);
        albumRequest.setGenreIds(List.of(testGenreId));

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
        // Create test album using fresh managed entities to avoid detached entity issues
        Artist managedArtist = artistRepository.findById(testArtistId).orElseThrow();
        Genre managedGenre = genreRepository.findById(testGenreId).orElseThrow();
        
        Album album = new Album();
        album.setTitle("Test Album");
        album.setReleaseDate(LocalDate.of(2023, 1, 1));
        album.setArtist(managedArtist);
        album.setGenres(Set.of(managedGenre));
        albumRepository.save(album);

        ResponseEntity<RestResponsePage<Album>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<Album>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getTitle()).isEqualTo("Test Album");
    }

    @Test
    @Transactional
    void testGetAlbumById() {
        // Create test album using fresh managed entities to avoid detached entity issues
        Artist managedArtist = artistRepository.findById(testArtistId).orElseThrow();
        Genre managedGenre = genreRepository.findById(testGenreId).orElseThrow();
        
        Album album = new Album();
        album.setTitle("Test Album");
        album.setReleaseDate(LocalDate.of(2023, 1, 1));
        album.setArtist(managedArtist);
        album.setGenres(Set.of(managedGenre));
        Album savedAlbum = albumRepository.save(album);

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
}