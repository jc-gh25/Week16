package music.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import music.library.dto.CreateAlbumRequest;
import music.library.dto.UpdateAlbumRequest;
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
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AlbumControllerUpdateDeleteIT {

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
    private Album testAlbum;

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
        
        // Create and save test album
        testAlbum = new Album();
        testAlbum.setTitle("Original Album");
        testAlbum.setReleaseDate(LocalDate.of(2023, 1, 1));
        testAlbum.setArtist(testArtist);
        // Use managed genre entity to avoid detached entity issues
        Genre managedGenre = genreRepository.findById(testGenre.getGenreId()).orElseThrow();
        testAlbum.setGenres(Set.of(managedGenre));
        testAlbum = albumRepository.save(testAlbum);
    }

    @Test
    void testUpdateAlbum() {
        // Create updated album data using DTO
        UpdateAlbumRequest updateRequest = new UpdateAlbumRequest();
        updateRequest.setTitle("Updated Album Title");
        updateRequest.setReleaseDate(LocalDate.of(2024, 1, 1));
        updateRequest.setArtistId(testArtist.getArtistId());
        updateRequest.setGenreIds(Arrays.asList(testGenre.getGenreId()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateAlbumRequest> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Album> response = restTemplate.exchange(
                baseUrl + "/" + testAlbum.getAlbumId(),
                HttpMethod.PUT,
                request,
                Album.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Album Title");
        assertThat(response.getBody().getReleaseDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(response.getBody().getAlbumId()).isEqualTo(testAlbum.getAlbumId());
    }

    @Test
    void testUpdateAlbumNotFound() {
        // Create updated album data using DTO
        UpdateAlbumRequest updateRequest = new UpdateAlbumRequest();
        updateRequest.setTitle("Updated Album Title");
        updateRequest.setReleaseDate(LocalDate.of(2024, 1, 1));
        updateRequest.setArtistId(testArtist.getArtistId());
        updateRequest.setGenreIds(Arrays.asList(testGenre.getGenreId()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateAlbumRequest> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<Album> response = restTemplate.exchange(
                baseUrl + "/999",
                HttpMethod.PUT,
                request,
                Album.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteAlbum() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + testAlbum.getAlbumId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify album is deleted
        Optional<Album> deletedAlbum = albumRepository.findById(testAlbum.getAlbumId());
        assertThat(deletedAlbum).isEmpty();
    }

    @Test
    void testDeleteAlbumNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/999",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}