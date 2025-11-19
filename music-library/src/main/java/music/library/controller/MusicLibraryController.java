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
import music.library.dto.DatabaseResetResponse;
import org.springframework.http.ResponseEntity;

/**
 * Main REST controller for the Music Library API.
 * 
 * Provides comprehensive CRUD endpoints for managing artists, albums, and
 * genres, along with relationship-based queries and database management
 * operations. All endpoints return JSON and follow RESTful conventions.
 * 
 * Base Path: /api
 * 
 * Endpoints: - POST /api/artists - Create new artist (201 Created) - GET
 * /api/artists - List all artists with pagination (200 OK) - GET
 * /api/artists/{id} - Get artist by ID (200 OK, 404 Not Found) - PUT
 * /api/artists/{id} - Update artist (200 OK, 404 Not Found) - DELETE
 * /api/artists/{id} - Delete artist (204 No Content, 404 Not Found)
 * 
 * - POST /api/albums - Create new album (201 Created) 
 * - GET /api/albums - List  all albums with pagination (200 OK) 
 * - GET /api/albums/{id} - Get album by ID (200 OK, 404 Not Found) 
 * - PUT /api/albums/{id} - Update album (200 OK, 404 Not Found) 
 * - DELETE /api/albums/{id} - Delete album (204 No Content, 404 Not Found)
 * 
 * - POST /api/genres - Create new genre (201 Created) 
 * - GET /api/genres - List all genres with pagination (200 OK) 
 * - GET /api/genres/{id} - Get genre by ID (200 OK, 404 Not Found) 
 * - PUT /api/genres/{id} - Update genre (200 OK, 404 Not Found) 
 * - DELETE /api/genres/{id} - Delete genre (204 No Content, 404 Not Found)
 * 
 * - GET /api/artists/{artistId}/albums 
 * - Get all albums by artist (200 OK) 
 * - GET /api/genres/{genreId}/albums 
 * - Get all albums by genre (200 OK) 
 * - DELETE /api/reset?confirm=true - Reset database (200 OK, 400 Bad Request)
 * 
 * Pagination: All list endpoints support Spring Data pagination via query
 * parameters: - page: zero-based page number (default: 0) - size: page size
 * (default: 20) - sort: sort criteria (e.g., "name,asc" or "createdAt,desc")
 * 
 * Error Handling: All exceptions are handled by GlobalExceptionHandler,
 * returning standardized ApiError responses with appropriate HTTP status codes.
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 * @see music.library.service.ArtistService
 * @see music.library.service.AlbumService
 * @see music.library.service.GenreService
 * @see music.library.exception.GlobalExceptionHandler
 */

@RestController
@RequestMapping("/api")
public class MusicLibraryController {

	// Service layer dependencies injected via Spring's @Autowired
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

	/**
	 * Retrieves all artists with pagination support.
	 * 
	 * @param pageable pagination parameters (page, size, sort) - automatically
	 *                 bound from query params
	 * @return paginated list of artists with metadata (totalElements, totalPages,
	 *         etc.)
	 */
	@GetMapping("/artists")
	public Page<Artist> getAllArtists(@ParameterObject Pageable pageable) {
		return artistSvc.findAll(pageable);
	}

	/**
	 * Retrieves a single artist by ID.
	 * 
	 * @param id the artist ID
	 * @return the artist entity
	 * @throws music.library.exception.ResourceNotFoundException if artist not found
	 *                                                           (404)
	 */
	@GetMapping("/artists/{id}")
	public Artist getArtistById(@PathVariable Long id) {
		return artistSvc.findById(id);
	}

	/**
	 * Updates an existing artist.
	 * 
	 * @param id the artist ID to update
	 * @param a  the updated artist data (validated)
	 * @return the updated artist entity
	 * @throws music.library.exception.ResourceNotFoundException if artist not found
	 *                                                           (404)
	 */
	@PutMapping("/artists/{id}")
	public Artist updateArtist(@PathVariable Long id, @Valid @RequestBody Artist a) {
		return artistSvc.update(id, a);
	}

	/**
	 * Deletes an artist by ID. Note: Deletion behavior depends on cascade settings.
	 * If albums reference this artist, the operation may fail with a constraint
	 * violation or cascade delete albums.
	 * 
	 * @param id the artist ID to delete
	 * @throws music.library.exception.ResourceNotFoundException if artist not found
	 *                                                           (404)
	 */
	@DeleteMapping("/artists/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteArtist(@PathVariable Long id) {
		artistSvc.delete(id);
	}

	/**
	 * Creates a new album.
	 * 
	 * @param request the album creation request containing title, artistId,
	 *                genreId, releaseYear, etc.
	 * @return the created album entity with generated ID and timestamps
	 * @throws music.library.exception.ResourceNotFoundException if referenced
	 *                                                           artist or genre not
	 *                                                           found
	 */
	@PostMapping("/albums")
	@ResponseStatus(HttpStatus.CREATED)
	public Album createAlbum(@Valid @RequestBody CreateAlbumRequest request) {
		return albumSvc.createAlbum(request);
	}

	/**
	 * Retrieves all albums with pagination support.
	 * 
	 * @param pageable pagination parameters (page, size, sort)
	 * @return paginated list of albums with metadata
	 */
	@GetMapping("/albums")
	public Page<Album> getAllAlbums(@ParameterObject Pageable pageable) {
		return albumSvc.findAll(pageable);
	}

	/**
	 * Retrieves a single album by ID.
	 * 
	 * @param id the album ID
	 * @return the album entity
	 * @throws music.library.exception.ResourceNotFoundException if album not found
	 *                                                           (404)
	 */
	@GetMapping("/albums/{id}")
	public Album getAlbumById(@PathVariable Long id) {
		return albumSvc.findById(id);
	}

	/**
	 * Updates an existing album.
	 * 
	 * @param id the album ID to update
	 * @param a  the updated album data (validated)
	 * @return the updated album entity
	 * @throws music.library.exception.ResourceNotFoundException if album not found
	 *                                                           (404)
	 */
	@PutMapping("/albums/{id}")
	public Album updateAlbum(@PathVariable Long id, @Valid @RequestBody Album a) {
		return albumSvc.update(id, a);
	}

	/**
	 * Deletes an album by ID.
	 * 
	 * @param id the album ID to delete
	 * @throws music.library.exception.ResourceNotFoundException if album not found
	 *                                                           (404)
	 */
	@DeleteMapping("/albums/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAlbum(@PathVariable Long id) {
		albumSvc.delete(id);
	}

	/**
	 * Creates a new genre.
	 * 
	 * @param request the genre creation request containing name and optional description
	 * @return the created genre entity with generated ID and timestamps
	 */
	@PostMapping("/genres")
	@ResponseStatus(HttpStatus.CREATED)
	public Genre createGenre(@Valid @RequestBody CreateGenreRequest request) {
		return genreSvc.create(request);
	}

	/**
	 * Retrieves all genres with pagination support.
	 * 
	 * @param pageable pagination parameters (page, size, sort)
	 * @return paginated list of genres with metadata
	 */
	@GetMapping("/genres")
	public Page<Genre> getAllGenres(@ParameterObject Pageable pageable) {
		return genreSvc.findAll(pageable);
	}

	/**
	 * Retrieves a single genre by ID.
	 * 
	 * @param id the genre ID
	 * @return the genre entity
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 */
	@GetMapping("/genres/{id}")
	public Genre getGenreById(@PathVariable Long id) {
		return genreSvc.findById(id);
	}

	/**
	 * Updates an existing genre.
	 * 
	 * @param id the genre ID to update
	 * @param g the updated genre data (validated)
	 * @return the updated genre entity
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 */
	@PutMapping("/genres/{id}")
	public Genre updateGenre(@PathVariable Long id, @Valid @RequestBody Genre g) {
		return genreSvc.update(id, g);
	}

	/**
	 * Deletes a genre by ID.
	 * Note: Deletion may fail if albums reference this genre, depending on cascade settings.
	 * 
	 * @param id the genre ID to delete
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 */
	@DeleteMapping("/genres/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGenre(@PathVariable Long id) {
		genreSvc.delete(id);
	}

	/**
	 * Retrieves all albums by a specific artist.
	 * 
	 * @param artistId the artist ID
	 * @return list of albums by the artist (empty list if none found)
	 * @throws music.library.exception.ResourceNotFoundException if artist not found (404)
	 */
	@GetMapping("/artists/{artistId}/albums")
	public List<Album> getAlbumsByArtist(@PathVariable Long artistId) {
		return albumSvc.findByArtistId(artistId);
	}

	/**
	 * Retrieves all albums in a specific genre.
	 * 
	 * @param genreId the genre ID
	 * @return list of albums in the genre (empty list if none found)
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 */
	@GetMapping("/genres/{genreId}/albums")
	public List<Album> getAlbumsByGenre(@PathVariable Long genreId) {
		return albumSvc.findByGenreId(genreId);
	}

	@Operation(summary = "Reset database", description = "Deletes all data from the database "
			+ "(albums, artists, and genres) in the correct order to avoid foreign key constraint violations. "
			+ "⚠️ WARNING: This operation cannot be undone! You must pass confirm=true as a query parameter "
			+ "to execute this action.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Database successfully reset - all data deleted and auto-increment sequences reset to 1", 
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = DatabaseResetResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad Request - Confirmation parameter missing or invalid. "
					+ "Pass ?confirm=true to confirm the reset operation.", 
					content = @Content(mediaType = "application/json")) })
	@DeleteMapping("/reset")
	public ResponseEntity<DatabaseResetResponse> resetDatabase(
			@RequestParam(value = "confirm", required = false, defaultValue = "false") boolean confirm) {
		if (!confirm) {
			throw new IllegalArgumentException(
					"⚠️ WARNING: This will delete ALL data from the database (artists, albums, genres). "
							+ "This action CANNOT be undone! " + "To confirm, pass the query parameter: "
									+ "?confirm=true");
		}
		resetSvc.resetDatabase();
		DatabaseResetResponse response = new DatabaseResetResponse(
			"Database reset successfully. All data has been deleted and auto-increment sequences have been reset to 1."
		);
		return ResponseEntity.ok(response);
	}
}
