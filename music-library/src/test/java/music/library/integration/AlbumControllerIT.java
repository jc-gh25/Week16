package music.library.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.exception.ApiError;
import music.library.repository.AlbumRepository;
import music.library.repository.ArtistRepository;
import music.library.repository.GenreRepository;

/* This test starts the whole application (embedded Tomcat, MySQL, or H2) and performs real HTTP calls. 
 * It validates:
 * Routing (/api/albums, /api/albums/search)
 * Validation (@Valid, @Positive)
 * Error payload (ApiError JSON)
 * Pagination (page, size)
 * 
 * @SpringBootTest(webEnvironment = RANDOM_PORT) launches the full stack on a random port, 
 * avoiding port conflicts.
 * TestRestTemplate is a convenient wrapper around RestTemplate that automatically 
 * follows redirects and marshals JSON.
 * ParameterizedTypeReference<>() lets us capture the generic Page<Album> response type.
 * The ApiError class is reused to assert the error payload shape.
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // reuse DB state across tests
class AlbumControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArtistRepository artistRepo;

    @Autowired
    private GenreRepository genreRepo;

    @Autowired
    private AlbumRepository albumRepo;

    private String baseUrl() {
        return "http://localhost:" + port + "/api";
    }

    private Artist savedArtist;
    private Genre savedGenre;

    @BeforeAll
    void setUpData() {
        // Create a reusable artist & genre
        savedArtist = artistRepo.save(Artist.builder()
                .name("Test Artist")
                .description("For integration tests")
                .build());

        savedGenre = genreRepo.save(Genre.builder()
                .name("Jazz")
                .description("Smooth tunes")
                .build());

        // Seed a few albums
        Album a1 = Album.builder()
                .title("Jazz Classics")
                .artist(savedArtist)
                .releaseDate(LocalDate.of(1995, 6, 1))
                .catalogNumber("JAZZ95")
                .genres(Set.of(savedGenre))
                .build();

        Album a2 = Album.builder()
                .title("Modern Jazz")
                .artist(savedArtist)
                .releaseDate(LocalDate.of(2020, 3, 15))
                .catalogNumber("JAZZ20")
                .genres(Set.of(savedGenre))
                .build();

        albumRepo.saveAll(List.of(a1, a2));
    }

    @Test
    void getAllAlbums_paginated() {
        String url = baseUrl() + "/albums?page=0&size=1";

        ResponseEntity<RestResponsePage<Album>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<Album>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<Album> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getSize()).isEqualTo(1);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void searchAlbums_byTitle_andGenre() {
        String url = baseUrl() + "/albums/search?title=jazz&genreId=" + savedGenre.getGenreId();

        ResponseEntity<RestResponsePage<Album>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<Album>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<Album> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(Album::getTitle)
                .allMatch(t -> t.toLowerCase().contains("jazz"));
    }

    @Test
    void createAlbum_invalidPayload_returns400() {
        // Missing required title (blank) – should trigger Bean Validation
        Album bad = Album.builder()
                .artist(savedArtist)          // artist is ok
                .releaseDate(LocalDate.now())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Album> request = new HttpEntity<>(bad, headers);

        ResponseEntity<ApiError> response = restTemplate.exchange(
                baseUrl() + "/albums",
                HttpMethod.POST,
                request,
                ApiError.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError err = response.getBody();
        assertThat(err).isNotNull();
        assertThat(err.getMessage()).isEqualTo("Validation failed");
        assertThat(err.getValidationErrors())
                .anyMatch(msg -> msg.contains("title"))
                .anyMatch(msg -> msg.contains("must not be blank"));
    }

    @Test
    void getAlbum_notFound_returns404() {
        ResponseEntity<ApiError> response = restTemplate.exchange(
                baseUrl() + "/albums/99999",
                HttpMethod.GET,
                null,
                ApiError.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError err = response.getBody();
        assertThat(err).isNotNull();
        assertThat(err.getMessage()).contains("Album with id 99999 not found");
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
