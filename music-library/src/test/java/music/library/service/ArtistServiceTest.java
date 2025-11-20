package music.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import music.library.entity.Artist;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.ArtistRepository;

@ExtendWith(MockitoExtension.class)          // <-- enables @Mock/@InjectMocks

class ArtistServiceTest {

    @Mock
    private ArtistRepository repo;          // <-- mocked dependency

    @InjectMocks
    private ArtistService service;          // <-- class under test, with repo injected

    @Test
    void findById_whenExists_returnsArtist() {
        // arrange – a fake Artist that the mock will return
        Artist mock = new Artist();
        mock.setArtistId(5L);
        mock.setName("Test Artist");
        when(repo.findById(5L)).thenReturn(Optional.of(mock));

        // act
        Artist result = service.findById(5L);

        // assert
        assertThat(result).isSameAs(mock);
        verify(repo).findById(5L);
    }

    @Test
    void findById_whenMissing_throwsNotFound() {
        // arrange – repository returns empty
        when(repo.findById(99L)).thenReturn(Optional.empty());

        // act / assert
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist with ID 99 not found");
    }

    @Test
    void create_savesAndReturnsArtist() {
        Artist input = new Artist();
        input.setName("New Artist");

        when(repo.save(input)).thenReturn(input); // mock save just echoes back

        Artist saved = service.create(input);

        assertThat(saved.getName()).isEqualTo("New Artist");
        verify(repo).save(input);
    }

    // -----------------------------------------------------------------
    // Can add more tests here (update, delete, pagination, etc.)
    // -----------------------------------------------------------------
}