package music.library.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.repository.ArtistRepository;
import music.library.repository.GenreRepository;

@SpringBootTest
@ActiveProfiles("test") // forces the H2 test profile
class AlbumServiceBidirectionalTest {

	@Autowired
	private AlbumService albumService;

	@Autowired
	private ArtistRepository artistRepo;

	@Autowired
	private GenreRepository genreRepo;

	@Test
	@Transactional
	void addGenre_updatesBothSides() {
		// Create an artist
		Artist artist = artistRepo.save(Artist.builder().name("Test Artist").description("for service test").build());

		// Create an album
		Album album = albumService.create(Album.builder().title("Rock Album").artist(artist)
				.releaseDate(java.time.LocalDate.of(1995, 6, 1)).build());

		// Create a genre
		Genre genre = genreRepo.save(Genre.builder().name("Rock").description("hard rock").build());

		// Use the service method that updates both sides
		albumService.addGenre(album.getAlbumId(), genre.getGenreId());

		// Reload genre to see the inverse side
		Genre refreshed = genreRepo.findById(genre.getGenreId())
				.orElseThrow(() -> new AssertionError("Genre vanished"));
		// Confirms that the Genre.albums set now contains the newly-associated album, 
		// so the bidirectional link works.
		assertThat(refreshed.getAlbums()).extracting(Album::getTitle).containsExactly("Rock Album");
	}

	@Test
	@Transactional
	// Verifies that the removal method also updates the inverse side.
	void removeGenre_cleansBothSides() {
		// Same as above, but also add the genre first
		Artist artist = artistRepo.save(Artist.builder().name("Test Artist").description("for remove test").build());

		Album album = albumService.create(Album.builder().title("Jazz Album").artist(artist)
				.releaseDate(java.time.LocalDate.of(2000, 1, 1)).build());

		Genre genre = genreRepo.save(Genre.builder().name("Jazz").description("smooth").build());

		// Add the genre (service updates both sides)
		albumService.addGenre(album.getAlbumId(), genre.getGenreId());

		// Remove the genre
		albumService.removeGenre(album.getAlbumId(), genre.getGenreId());

		// Reload genre and assert the album is gone.
		// Forces JPA to fetch the inverse collection from the DB, 
		// guaranteeing we're not looking at a stale in‑memory object.
		Genre refreshed = genreRepo.findById(genre.getGenreId())
				.orElseThrow(() -> new AssertionError("Genre vanished"));

		assertThat(refreshed.getAlbums()).isEmpty();
	}

}
