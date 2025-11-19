package music.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import music.library.repository.AlbumRepository;
import music.library.repository.ArtistRepository;
import music.library.repository.GenreRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class DatabaseResetService {

    private final AlbumRepository albumRepo;
    private final ArtistRepository artistRepo;
    private final GenreRepository genreRepo;

    /**
     * Deletes all data from the database in the correct order to avoid foreign key constraint violations.
     * Order: Albums -> Artists -> Genres
     */
    public void resetDatabase() {
        // Delete albums first (they have foreign keys to artists and genres)
        albumRepo.deleteAll();
        
        // Delete artists (they have foreign keys from albums)
        artistRepo.deleteAll();
        
        // Delete genres (they have foreign keys from albums)
        genreRepo.deleteAll();
    }
}
