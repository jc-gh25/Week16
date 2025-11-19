package music.library.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import music.library.dto.CreateAlbumRequest;
import music.library.dto.CreateArtistRequest;
import music.library.dto.CreateGenreRequest;
import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.service.AlbumService;
import music.library.service.ArtistService;
import music.library.service.DatabaseResetService;
import music.library.service.GenreService;

@RestController
@RequestMapping("/api")
public class MusicLibraryController {

	@Autowired
	private ArtistService artistSvc;
	@Autowired
	private AlbumService albumSvc;
	@Autowired
	private GenreService genreSvc;
	@Autowired
	private DatabaseResetService resetSvc;

	@Operation(summary = "Create a new artist", description = "Creates a new artist in the music library with the provided name and optional description. The artistId, createdAt, and updatedAt fields are automatically generated.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Artist successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Artist.class), examples = @ExampleObject(value = "{\"artistId\":1,\"name\":\"The Rolling Stones\",\"description\":\"Legendary British rock band\",\"createdAt\":\"2025-01-19T04:40:37.345906\",\"updatedAt\":\"2025-01-19T04:40:37.345906\"}"))),
			@ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data (e.g., missing required name field, name exceeds 255 characters)", content = @Content(mediaType = "application/json")) })
	@PostMapping("/artists")
	@ResponseStatus(HttpStatus.CREATED)
	public Artist createArtist(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Artist object to be created. The 'name' field is required (max 255 characters), and 'description' is optional. Do not include artistId, createdAt, updatedAt, or albums fields as they are auto-generated or ignored.", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = Artist.class), examples = @ExampleObject(name = "Create Artist Example", value = "{\"name\":\"The Beatles\",\"description\":\"Iconic British rock band from Liverpool\"}"))) @Valid @RequestBody CreateArtistRequest request)

	{
		return artistSvc.create(request);
	}

	@GetMapping("/artists")
	public Page<Artist> getAllArtists(@ParameterObject Pageable pageable) {
		return artistSvc.findAll(pageable);
	}

	@GetMapping("/artists/{id}")
	public Artist getArtistById(@PathVariable Long id) {
		return artistSvc.findById(id);
	}

	@PutMapping("/artists/{id}")
	public Artist updateArtist(@PathVariable Long id, @Valid @RequestBody Artist a) {
		return artistSvc.update(id, a);
	}

	@DeleteMapping("/artists/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteArtist(@PathVariable Long id) {
		artistSvc.delete(id);
	}

	@PostMapping("/albums")
	@ResponseStatus(HttpStatus.CREATED)
	public Album createAlbum(@Valid @RequestBody CreateAlbumRequest request) {
		return albumSvc.createAlbum(request);
	}

	@GetMapping("/albums")
	public Page<Album> getAllAlbums(@ParameterObject Pageable pageable) {
		return albumSvc.findAll(pageable);
	}

	@GetMapping("/albums/{id}")
	public Album getAlbumById(@PathVariable Long id) {
		return albumSvc.findById(id);
	}

	@PutMapping("/albums/{id}")
	public Album updateAlbum(@PathVariable Long id, @Valid @RequestBody Album a) {
		return albumSvc.update(id, a);
	}

	@DeleteMapping("/albums/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAlbum(@PathVariable Long id) {
		albumSvc.delete(id);
	}

	@PostMapping("/genres")
	@ResponseStatus(HttpStatus.CREATED)
	public Genre createGenre(@Valid @RequestBody CreateGenreRequest request) {
		return genreSvc.create(request);
	}

	@GetMapping("/genres")
	public Page<Genre> getAllGenres(@ParameterObject Pageable pageable) {
		return genreSvc.findAll(pageable);
	}

	@GetMapping("/genres/{id}")
	public Genre getGenreById(@PathVariable Long id) {
		return genreSvc.findById(id);
	}

	@PutMapping("/genres/{id}")
	public Genre updateGenre(@PathVariable Long id, @Valid @RequestBody Genre g) {
		return genreSvc.update(id, g);
	}

	@DeleteMapping("/genres/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGenre(@PathVariable Long id) {
		genreSvc.delete(id);
	}

	@GetMapping("/artists/{artistId}/albums")
	public List<Album> getAlbumsByArtist(@PathVariable Long artistId) {
		return albumSvc.findByArtistId(artistId);
	}

	@GetMapping("/genres/{genreId}/albums")
	public List<Album> getAlbumsByGenre(@PathVariable Long genreId) {
		return albumSvc.findByGenreId(genreId);
	}

	@Operation(summary = "Reset database", description = "Deletes all data from the database "
			+ "(albums, artists, and genres) in the correct order to avoid foreign key constraint violations."
			+ "⚠️ WARNING: This operation cannot be undone! You must pass confirm=true as a query parameter "
			+ "to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Database successfully reset - all data deleted", 
					content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request - Confirmation parameter missing or invalid. "
					+ "Pass ?confirm=true to confirm the reset operation.", 
					content = @Content(mediaType = "application/json")) })
	@DeleteMapping("/reset")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resetDatabase(
			@RequestParam(value = "confirm", required = false, defaultValue = "false") boolean confirm) {
		if (!confirm) {
			throw new IllegalArgumentException(
					"⚠️ WARNING: This will delete ALL data from the database (artists, albums, genres). "
							+ "This action CANNOT be undone! " + "To confirm, pass the query parameter: "
									+ "?confirm=true");
		}
		resetSvc.resetDatabase();
	}
}
