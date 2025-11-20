package music.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import music.library.entity.Album;
import music.library.entity.Genre;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;
import music.library.repository.GenreRepository;


/* What this test shows:
 * Happy path (findById, addGenre) – verifies the service returns the expected entity.
 * Error path – asserts that ResourceNotFoundException bubbles up correctly.
 * Specification usage – don’t need to test the internals of AlbumSpecs here;
 * just confirm the service forwards the spec and pageable to the repository. */

@ExtendWith(MockitoExtension.class)

class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepo;

    @Mock
    private GenreRepository genreRepo;

    @InjectMocks
    private AlbumService albumService;


    /* ---------- findById ---------- */
    @Test
    void findById_existing_returnsAlbum() {
        Album mock = new Album();
        mock.setAlbumId(1L);
        mock.setTitle("Test Album");
        when(albumRepo.findById(1L)).thenReturn(Optional.of(mock));

        Album result = albumService.findById(1L);

        assertThat(result).isSameAs(mock);
        verify(albumRepo).findById(1L);
    }

    @Test
    void findById_missing_throwsNotFound() {
        when(albumRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album with id 99 not found");
    }

    /* ---------- addGenre ---------- */
    @Test
    void addGenre_successful() {
        Album album = new Album();
        album.setAlbumId(1L);
        album.setGenres(new HashSet<>());

        Genre genre = new Genre();
        genre.setGenreId(2L);

        when(albumRepo.findById(1L)).thenReturn(Optional.of(album));
        when(genreRepo.findById(2L)).thenReturn(Optional.of(genre));
        when(albumRepo.save(any())).thenAnswer(i -> i.getArgument(0)); // return the passed album

        Album saved = albumService.addGenre(1L, 2L);

        assertThat(saved.getGenres()).containsExactly(genre);
        verify(albumRepo).save(album);
    }

    @Test
    void addGenre_genreMissing_throwsNotFound() {
        Album album = new Album();
        album.setAlbumId(1L);
        when(albumRepo.findById(1L)).thenReturn(Optional.of(album));
        when(genreRepo.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.addGenre(1L, 2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Genre with ID 2 not found");
    }

    /* ---------- search (Specification) ---------- */
    @Test
    void search_withTitleAndYear_returnsPage() {
        // Prepare a dummy page
        Album a1 = new Album(); a1.setAlbumId(1L); a1.setTitle("Promised Land");
        Page<Album> page = new PageImpl<>(List.of(a1));

        // Mock the repository call – we don't care about the actual spec internals,
        // just that the service forwards the spec and pageable.
        when(albumRepo.findAll((Specification<Album>) any(), any(Pageable.class)))
                .thenReturn(page);

        Page<Album> result = albumService.search("promised", 1997, 1997, null,
                PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isOne();
        assertThat(result.getContent().get(0).getTitle()).containsIgnoringCase("promised");
        verify(albumRepo).findAll((Specification<Album>) any(), any(Pageable.class));
    }
}