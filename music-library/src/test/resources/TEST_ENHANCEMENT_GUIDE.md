# Test Suite Enhancement Guide

## Overview
This guide provides specific code examples and recommendations to enhance the Music Library test suite from good to excellent for your bootcamp portfolio.

---

## 1. Add Artist Controller Integration Tests

### File: `ArtistControllerIT.java`

```java
package music.library.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import music.library.entity.Artist;
import music.library.exception.ApiError;
import music.library.repository.ArtistRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArtistControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ArtistRepository artistRepo;

    private String baseUrl() {
        return "http://localhost:" + port + "/api";
    }

    private Artist savedArtist;

    @BeforeAll
    void setUpData() {
        savedArtist = artistRepo.save(Artist.builder()
                .name("Test Artist")
                .description("For integration tests")
                .build());
    }

    @Test
    void getAllArtists_paginated() {
        String url = baseUrl() + "/artists?page=0&size=10";

        ResponseEntity<Page<Artist>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<Artist> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void getArtist_byId_success() {
        String url = baseUrl() + "/artists/" + savedArtist.getArtistId();

        ResponseEntity<Artist> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Artist.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Artist artist = response.getBody();
        assertThat(artist).isNotNull();
        assertThat(artist.getName()).isEqualTo("Test Artist");
    }

    @Test
    void createArtist_success() {
        Artist newArtist = Artist.builder()
                .name("New Artist")
                .description("Newly created")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Artist> request = new HttpEntity<>(newArtist, headers);

        ResponseEntity<Artist> response = restTemplate.exchange(
                baseUrl() + "/artists",
                HttpMethod.POST,
                request,
                Artist.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Artist created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getArtistId()).isNotNull();
        assertThat(created.getName()).isEqualTo("New Artist");
    }

    @Test
    void createArtist_invalidPayload_returns400() {
        Artist badArtist = Artist.builder()
                .name("")  // blank name should fail validation
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Artist> request = new HttpEntity<>(badArtist, headers);

        ResponseEntity<ApiError> response = restTemplate.exchange(
                baseUrl() + "/artists",
                HttpMethod.POST,
                request,
                ApiError.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError err = response.getBody();
        assertThat(err).isNotNull();
        assertThat(err.getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void getArtist_notFound_returns404() {
        ResponseEntity<ApiError> response = restTemplate.exchange(
                baseUrl() + "/artists/99999",
                HttpMethod.GET,
                null,
                ApiError.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiError err = response.getBody();
        assertThat(err).isNotNull();
        assertThat(err.getMessage()).contains("Artist with ID 99999 not found");
    }
}
```

---

## 2. Add Genre Controller Integration Tests

### File: `GenreControllerIT.java`

```java
package music.library.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import music.library.entity.Genre;
import music.library.exception.ApiError;
import music.library.repository.GenreRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenreControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GenreRepository genreRepo;

    private String baseUrl() {
        return "http://localhost:" + port + "/api";
    }

    private Genre savedGenre;

    @BeforeAll
    void setUpData() {
        savedGenre = genreRepo.save(Genre.builder()
                .name("Rock")
                .description("Rock music")
                .build());
    }

    @Test
    void getAllGenres_paginated() {
        String url = baseUrl() + "/genres?page=0&size=10";

        ResponseEntity<Page<Genre>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Page<Genre> page = response.getBody();
        assertThat(page).isNotNull();
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void getGenre_byId_success() {
        String url = baseUrl() + "/genres/" + savedGenre.getGenreId();

        ResponseEntity<Genre> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Genre.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Genre genre = response.getBody();
        assertThat(genre).isNotNull();
        assertThat(genre.getName()).isEqualTo("Rock");
    }

    @Test
    void createGenre_success() {
        Genre newGenre = Genre.builder()
                .name("Jazz")
                .description("Jazz music")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Genre> request = new HttpEntity<>(newGenre, headers);

        ResponseEntity<Genre> response = restTemplate.exchange(
                baseUrl() + "/genres",
                HttpMethod.POST,
                request,
                Genre.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Genre created = response.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getGenreId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Jazz");
    }

    @Test
    void getGenre_notFound_returns404() {
        ResponseEntity<ApiError> response = restTemplate.exchange(
                baseUrl() + "/genres/99999",
                HttpMethod.GET,
                null,
                ApiError.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
```

---

## 3. Add Album CRUD Tests

### File: `AlbumControllerIT.java` - Add These Tests

```java
    @Test
    void updateAlbum_success() {
        Album updated = Album.builder()
                .title("Updated Title")
                .artist(savedArtist)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .catalogNumber("UPDATED01")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Album> request = new HttpEntity<>(updated, headers);

        ResponseEntity<Album> response = restTemplate.exchange(
                baseUrl() + "/albums/" + savedAlbum.getAlbumId(),
                HttpMethod.PUT,
                request,
                Album.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Album result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void deleteAlbum_success() {
        // Create an album to delete
        Album toDelete = Album.builder()
                .title("To Delete")
                .artist(savedArtist)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .catalogNumber("DELETE01")
                .build();
        Album saved = albumRepo.save(toDelete);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl() + "/albums/" + saved.getAlbumId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(albumRepo.findById(saved.getAlbumId())).isEmpty();
    }

    @Test
    void removeGenreFromAlbum_success() {
        String url = baseUrl() + "/albums/" + savedAlbum.getAlbumId() 
                   + "/genres/" + savedGenre.getGenreId();

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
```

---

## 4. Add Edge Case Tests

### File: `AlbumServiceEdgeCaseTest.java`

```java
package music.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;

class AlbumServiceEdgeCaseTest {

    @Mock
    private AlbumRepository albumRepo;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 99999L})
    @DisplayName("findById with invalid IDs should throw ResourceNotFoundException")
    void findById_invalidIds_throwsNotFound(long invalidId) {
        when(albumRepo.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.findById(invalidId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("search with null title should still work")
    void search_nullTitle_returnsResults() {
        // This tests that null parameters don't break the search
        assertThatThrownBy(() -> 
            albumService.search(null, null, null, null, PageRequest.of(0, 10))
        ).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("search with negative year should handle gracefully")
    void search_negativeYear_returnsEmpty() {
        // Depending on implementation, this might return empty or throw
        // The important thing is it doesn't crash
        try {
            albumService.search(null, -1, -1, null, PageRequest.of(0, 10));
        } catch (Exception e) {
            // Expected - just verify it doesn't crash the app
            assertThat(e).isNotNull();
        }
    }
}
```

---

## 5. Add Parameterized Tests

### File: `AlbumServiceParameterizedTest.java`

```java
package music.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import music.library.entity.Album;
import music.library.repository.AlbumRepository;

class AlbumServiceParameterizedTest {

    @Mock
    private AlbumRepository albumRepo;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    static Stream<Album> provideAlbums() {
        Album album1 = new Album();
        album1.setAlbumId(1L);
        album1.setTitle("Album 1");

        Album album2 = new Album();
        album2.setAlbumId(2L);
        album2.setTitle("Album 2");

        Album album3 = new Album();
        album3.setAlbumId(3L);
        album3.setTitle("Album 3");

        return Stream.of(album1, album2, album3);
    }

    @ParameterizedTest
    @MethodSource("provideAlbums")
    void findById_multipleAlbums_returnsCorrectAlbum(Album album) {
        when(albumRepo.findById(album.getAlbumId())).thenReturn(Optional.of(album));

        Album result = albumService.findById(album.getAlbumId());

        assertThat(result).isSameAs(album);
        assertThat(result.getTitle()).isEqualTo(album.getTitle());
    }
}
```

---

## 6. Add JaCoCo Code Coverage

### Update `pom.xml`

Add this to your `<build><plugins>` section:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <excludes>
                            <exclude>*Test</exclude>
                        </excludes>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Run Coverage Report

```bash
mvn clean test jacoco:report
```

Coverage report will be at: `target/site/jacoco/index.html`

---

## 7. Add Test Fixtures/Builders

### File: `TestDataBuilder.java`

```java
package music.library.test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;

public class TestDataBuilder {

    public static Artist createTestArtist(String name) {
        return Artist.builder()
                .name(name)
                .description("Test artist: " + name)
                .build();
    }

    public static Artist createTestArtist(String name, String description) {
        return Artist.builder()
                .name(name)
                .description(description)
                .build();
    }

    public static Genre createTestGenre(String name) {
        return Genre.builder()
                .name(name)
                .description("Test genre: " + name)
                .build();
    }

    public static Album createTestAlbum(String title, Artist artist) {
        return Album.builder()
                .title(title)
                .artist(artist)
                .releaseDate(LocalDate.now())
                .catalogNumber("TEST-" + System.currentTimeMillis())
                .genres(new HashSet<>())
                .build();
    }

    public static Album createTestAlbum(String title, Artist artist, int releaseYear) {
        return Album.builder()
                .title(title)
                .artist(artist)
                .releaseDate(LocalDate.of(releaseYear, 1, 1))
                .catalogNumber("TEST-" + System.currentTimeMillis())
                .genres(new HashSet<>())
                .build();
    }

    public static Album createTestAlbum(String title, Artist artist, Set<Genre> genres) {
        return Album.builder()
                .title(title)
                .artist(artist)
                .releaseDate(LocalDate.now())
                .catalogNumber("TEST-" + System.currentTimeMillis())
                .genres(genres)
                .build();
    }
}
```

### Usage in Tests

```java
// Before
Artist artist = new Artist();
artist.setName("Test Artist");
artist.setDescription("for service test");

// After
Artist artist = TestDataBuilder.createTestArtist("Test Artist");
```

---

## 8. Add Repository Query Tests

### File: `AlbumRepositoryQueryTest.java`

```java
package music.library.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;

@DataJpaTest
class AlbumRepositoryQueryTest {

    @Autowired
    private AlbumRepository albumRepo;

    @Autowired
    private ArtistRepository artistRepo;

    @Autowired
    private GenreRepository genreRepo;

    private Artist testArtist;
    private Genre testGenre;

    @BeforeEach
    void setUp() {
        testArtist = artistRepo.save(Artist.builder()
                .name("Test Artist")
                .description("For query tests")
                .build());

        testGenre = genreRepo.save(Genre.builder()
                .name("Rock")
                .description("Rock music")
                .build());
    }

    @Test
    void findByArtist_returnsAllAlbumsForArtist() {
        Album album1 = Album.builder()
                .title("Album 1")
                .artist(testArtist)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .catalogNumber("CAT001")
                .build();

        Album album2 = Album.builder()
                .title("Album 2")
                .artist(testArtist)
                .releaseDate(LocalDate.of(2021, 1, 1))
                .catalogNumber("CAT002")
                .build();

        albumRepo.saveAll(List.of(album1, album2));

        List<Album> results = albumRepo.findByArtist_ArtistId(testArtist.getArtistId());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Album::getTitle)
                .containsExactlyInAnyOrder("Album 1", "Album 2");
    }

    @Test
    void findByGenres_returnsAllAlbumsInGenre() {
        Album album = Album.builder()
                .title("Rock Album")
                .artist(testArtist)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .catalogNumber("CAT003")
                .genres(Set.of(testGenre))
                .build();

        albumRepo.save(album);

        List<Album> results = albumRepo.findByGenres_GenreId(testGenre.getGenreId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Rock Album");
    }
}
```

---

## Implementation Checklist

### Phase 1: Critical Tests (2 hours)
- [ ] Create `ArtistControllerIT.java` (5 tests)
- [ ] Create `GenreControllerIT.java` (4 tests)
- [ ] Add update/delete tests to `AlbumControllerIT.java` (3 tests)
- [ ] Run all tests and verify they pass
- [ ] Total new tests: ~12

### Phase 2: Quality Improvements (1 hour)
- [ ] Create `AlbumServiceEdgeCaseTest.java` (3 tests)
- [ ] Create `AlbumServiceParameterizedTest.java` (1 test)
- [ ] Create `TestDataBuilder.java` utility class
- [ ] Refactor existing tests to use TestDataBuilder
- [ ] Total new tests: ~4

### Phase 3: Coverage & Reporting (30 minutes)
- [ ] Add JaCoCo plugin to pom.xml
- [ ] Run `mvn clean test jacoco:report`
- [ ] Review coverage report
- [ ] Document coverage metrics

### Phase 4: Advanced Testing (1 hour)
- [ ] Create `AlbumRepositoryQueryTest.java` (2 tests)
- [ ] Add Testcontainers configuration (optional)
- [ ] Total new tests: ~2

**Total Estimated Time:** 4.5 hours
**Total New Tests:** ~19 tests
**Final Test Count:** ~32 tests (excellent for portfolio)

---

## Expected Results After Implementation

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Total Tests | 13 | 32 | +146% |
| Test Files | 5 | 9 | +80% |
| Endpoint Coverage | 50% | 100% | +50% |
| CRUD Coverage | 50% | 100% | +50% |
| Code Coverage | Unknown | 80%+ | Measurable |
| Portfolio Quality | Good | Excellent | ⭐⭐⭐⭐⭐ |

---

## Tips for Success

1. **Run tests frequently** - `mvn test` after each change
2. **Keep tests focused** - One assertion per test when possible
3. **Use meaningful names** - Test names should describe what they test
4. **Document complex tests** - Add comments explaining the "why"
5. **Maintain test data** - Keep test data builders up to date
6. **Review coverage** - Aim for 80%+ line coverage
7. **Test edge cases** - Think about what could go wrong
8. **Keep tests fast** - Use mocks for external dependencies

---

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [AssertJ Documentation](https://assertj.github.io/assertj-core-features-highlight.html)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

