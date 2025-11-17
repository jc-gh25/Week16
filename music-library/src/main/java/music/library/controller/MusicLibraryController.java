package music.library.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.service.AlbumService;
import music.library.service.ArtistService;
import music.library.service.GenreService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated // <‑‑ enables validation on @PathVariable, @RequestParam, etc.
public class MusicLibraryController {

	private final ArtistService artistSvc;
	private final AlbumService albumSvc;
	private final GenreService genreSvc;

	/* ---------- Artists ---------- */
	@GetMapping("/artists")
	public Page<Artist> allArtists(Pageable pageable) {
		return artistSvc.findAll(pageable);
	}

	@PostMapping("/artists")
	public Artist createArtist(@Valid @RequestBody Artist a) {
		return artistSvc.create(a);
	}

	@GetMapping("/artists/{id}")
	public Artist getArtist(@PathVariable @Positive Long id) {
		return artistSvc.findById(id);
	}

	@PutMapping("/artists/{id}")
	public Artist updateArtist(@PathVariable @Positive Long id, @Valid @RequestBody Artist a) {
		return artistSvc.update(id, a);
	}

	@DeleteMapping("/artists/{id}")
	public ResponseEntity<Void> deleteArtist(@PathVariable @Positive Long id) {
		artistSvc.delete(id);
		return ResponseEntity.noContent().build();
	}

	/* ---------- Albums ---------- */
	@GetMapping("/albums")
	public Page<Album> allAlbums(Pageable pageable) {
		return albumSvc.findAll(pageable);
	}

	@PostMapping("/albums")
	public Album createAlbum(@Valid @RequestBody Album a) {
		return albumSvc.create(a);
	}

	@GetMapping("/albums/{id}")
	public Album getAlbum(@PathVariable @Positive Long id) {
		return albumSvc.findById(id);
	}

	@PutMapping("/albums/{id}")
	public Album updateAlbum(@PathVariable @Positive Long id, @Valid @RequestBody Album a) {
		return albumSvc.update(id, a);
	}

	@DeleteMapping("/albums/{id}")
	public ResponseEntity<Void> deleteAlbum(@PathVariable @Positive Long id) {
		albumSvc.delete(id);
		return ResponseEntity.noContent().build();
	}

	/* ---------- Genres ---------- */
	@GetMapping("/genres")
	public Page<Genre> allGenres(Pageable pageable) {
		return genreSvc.findAll(pageable);
	}

	@PostMapping("/genres")
	public Genre createGenre(@Valid @RequestBody Genre g) {
		return genreSvc.create(g);
	}

	@GetMapping("/genres/{id}")
	public Genre getGenre(@PathVariable @Positive Long id) {
		return genreSvc.findById(id);
	}

	@PutMapping("/genres/{id}")
	public Genre updateGenre(@PathVariable @Positive Long id, @Valid @RequestBody Genre g) {
		return genreSvc.update(id, g);
	}

	@DeleteMapping("/genres/{id}")
	public ResponseEntity<Void> deleteGenre(@PathVariable @Positive Long id) {
		genreSvc.delete(id);
		return ResponseEntity.noContent().build();
	}

	/* ---------- Album‑Genre join operations ---------- */
	@PostMapping("/albums/{albumId}/genres/{genreId}")
	public Album addGenreToAlbum(@PathVariable @Positive Long albumId, @PathVariable @Positive Long genreId) {
		return albumSvc.addGenre(albumId, genreId);
	}

	@DeleteMapping("/albums/{albumId}/genres/{genreId}")
	public Album removeGenreFromAlbum(@PathVariable @Positive Long albumId, @PathVariable @Positive Long genreId) {
		return albumSvc.removeGenre(albumId, genreId);
	}

	/* ---------- Search endpoint ---------- */
	@GetMapping("/albums/search")
	public Page<Album> searchAlbums(@RequestParam(required = false) String title,
			@RequestParam(required = false) Integer startYear, @RequestParam(required = false) Integer endYear,
			@RequestParam(required = false) Long genreId, Pageable pageable) {

		return albumSvc.search(title, startYear, endYear, genreId, pageable);
	}
}