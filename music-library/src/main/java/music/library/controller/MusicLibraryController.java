package music.library.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import music.library.dto.ApiInfoResponse;
import music.library.dto.ApiInfoResponse.Endpoint;
import music.library.dto.ApiInfoResponse.EndpointCategory;
import music.library.dto.CreateAlbumRequest;
import music.library.dto.UpdateAlbumRequest;
import music.library.dto.CreateArtistRequest;
import music.library.dto.UpdateArtistRequest;
import music.library.dto.CreateGenreRequest;
import music.library.dto.UpdateGenreRequest;
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
@Tag(name = "artist", description = "CRUD operations for artists")
@Tag(name = "album", description = "CRUD operations for albums")
@Tag(name = "genre", description = "CRUD operations for genres")
@Tag(name = "database", description = "Database management operations")
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

	/**
	 * Welcome/Info endpoint for the Music Library API.
	 * 
	 * Provides comprehensive information about the API including available endpoints,
	 * documentation links, features, and metadata. This endpoint serves as the entry
	 * point for developers exploring the API.
	 * 
	 * @return ApiInfoResponse containing project information, endpoints, and resources
	 */
	@Operation(
		summary = "API Welcome & Information", 
		description = "Returns comprehensive information about the Music Library API, including available endpoints, documentation links, features, and metadata. Perfect for developers getting started with the API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200", 
			description = "Successfully retrieved API information",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiInfoResponse.class)
			)
		)
	})
	@GetMapping
	public ApiInfoResponse getApiInfo() {
		// Documentation links
		Map<String, String> documentation = new LinkedHashMap<>();
		documentation.put("swagger-ui", "/swagger-ui/index.html");
		documentation.put("api-docs", "/v3/api-docs");
		documentation.put("openapi-json", "/v3/api-docs.yaml");
		documentation.put("github", "https://github.com/jc-gh25/music-library");
		
		// Build endpoint categories
		List<EndpointCategory> endpoints = new ArrayList<>();
		
		// Artists endpoints
		endpoints.add(new EndpointCategory(
			"Artists",
			"Manage music artists in the library",
			Arrays.asList(
				new Endpoint("POST", "/api/artists", "Create a new artist", "201"),
				new Endpoint("GET", "/api/artists", "Get all artists (paginated)", "200"),
				new Endpoint("GET", "/api/artists/{id}", "Get artist by ID", "200"),
				new Endpoint("PUT", "/api/artists/{id}", "Update an artist", "200"),
				new Endpoint("DELETE", "/api/artists/{id}", "Delete an artist", "204"),
				new Endpoint("GET", "/api/artists/{artistId}/albums", "Get all albums by artist", "200")
			)
		));
		
		// Albums endpoints
		endpoints.add(new EndpointCategory(
			"Albums",
			"Manage music albums in the library",
			Arrays.asList(
				new Endpoint("POST", "/api/albums", "Create a new album", "201"),
				new Endpoint("GET", "/api/albums", "Get all albums (paginated)", "200"),
				new Endpoint("GET", "/api/albums/{id}", "Get album by ID", "200"),
				new Endpoint("PUT", "/api/albums/{id}", "Update an album", "200"),
				new Endpoint("DELETE", "/api/albums/{id}", "Delete an album", "204")
			)
		));
		
		// Genres endpoints
		endpoints.add(new EndpointCategory(
			"Genres",
			"Manage music genres in the library",
			Arrays.asList(
				new Endpoint("POST", "/api/genres", "Create a new genre", "201"),
				new Endpoint("GET", "/api/genres", "Get all genres (paginated)", "200"),
				new Endpoint("GET", "/api/genres/{id}", "Get genre by ID", "200"),
				new Endpoint("PUT", "/api/genres/{id}", "Update a genre", "200"),
				new Endpoint("DELETE", "/api/genres/{id}", "Delete a genre", "204"),
				new Endpoint("GET", "/api/genres/{genreId}/albums", "Get all albums by genre", "200")
			)
		));
		
		// Database Management endpoints
		endpoints.add(new EndpointCategory(
			"Database Management",
			"Administrative operations for database management",
			Arrays.asList(
				new Endpoint("DELETE", "/api/reset?confirm=true", "Reset database (requires confirmation)", "200")
			)
		));
		
		// API features
		List<String> features = Arrays.asList(
			"RESTful API design with standard HTTP methods",
			"Comprehensive CRUD operations for Artists, Albums, and Genres",
			"Pagination support on all list endpoints (page, size, sort parameters)",
			"Relationship-based queries (albums by artist, albums by genre)",
			"Input validation with detailed error messages",
			"OpenAPI 3.0 specification with Swagger UI",
			"Standardized error handling with ApiError responses",
			"Automatic timestamp tracking (createdAt, updatedAt)",
			"Database reset functionality for development/testing",
			"JSON request/response format"
		);
		
		// Metadata
		Map<String, String> metadata = new LinkedHashMap<>();
		metadata.put("framework", "Spring Boot 3.5.7");
		metadata.put("java-version", "17");
		metadata.put("database", "MySQL/H2");
		metadata.put("build-tool", "Maven");
		metadata.put("api-standard", "OpenAPI 3.0");
		metadata.put("status", "Active Development");
		
		return new ApiInfoResponse(
			"🎵 Music Library API",
			"A comprehensive RESTful API for managing a music library with artists, albums, and genres. "
			+ "Built with Spring Boot, this API provides full CRUD operations, pagination, relationship queries, "
			+ "and follows industry best practices for API design and documentation.",
			"1.0.0",
			"Jeff - Backend Developer",
			"jeff@jcarl.net",
			"/api",
			documentation,
			endpoints,
			features,
			metadata
		);
	}

	@Operation(summary = "Create a new artist", description = "Creates a new artist in the music library with the provided name and optional description. The artistId, createdAt, and updatedAt fields are automatically generated.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Artist successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Artist.class), examples = @ExampleObject(value = "{\"artistId\":1,\"name\":\"The Rolling Stones\",\"description\":\"Legendary British rock band\",\"createdAt\":\"2025-01-19T04:40:37.345906\",\"updatedAt\":\"2025-01-19T04:40:37.345906\"}"))),
			@ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data (e.g., missing required name field, name exceeds 255 characters)", content = @Content(mediaType = "application/json")) })
	@PostMapping("/artists")
	public ResponseEntity<Artist> createArtist(
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
	@Operation(
		summary = "Get all artists",
		description = "Returns paginated list of artists with optional sorting. Supports pagination via query parameters: page (default: 0), size (default: 20), and sort (e.g., 'name,asc' or 'createdAt,desc')"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved paginated list of artists",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
		)
	})
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
	@Operation(
		summary = "Get artist by ID",
		description = "Returns a single artist by their ID"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved artist",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Artist.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Artist not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@GetMapping("/artists/{id}")
	public Artist getArtistById(
		@Parameter(description = "ID of the artist to retrieve", required = true)
		@PathVariable Long id) {
		return artistSvc.findById(id);
	}

	/**
	 * Updates an existing artist using a DTO.
	 * Validates that the artist exists before updating.
	 * Only updates the fields provided in the request DTO.
	 * 
	 * @param id the artist ID to update
	 * @param request the updated artist data (validated via DTO)
	 * @return the updated artist entity
	 * @throws music.library.exception.ResourceNotFoundException if artist not found (404)
	 * @throws org.springframework.web.bind.MethodArgumentNotValidException if validation fails (400)
	 */
	@Operation(
		summary = "Update artist",
		description = "Updates an existing artist and returns the updated entity. Only the fields provided in the request will be updated."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Artist successfully updated",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Artist.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Artist not found with the provided ID",
			content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
			responseCode = "400",
			description = "Bad Request - Invalid input data",
			content = @Content(mediaType = "application/json")
		)
	})
	@PutMapping("/artists/{id}")
	public Artist updateArtist(
		@Parameter(description = "ID of the artist to update", required = true)
		@PathVariable Long id, 
		@Valid @RequestBody UpdateArtistRequest request) {
		return artistSvc.updateArtist(id, request);
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
	@Operation(
		summary = "Delete artist",
		description = "Deletes an artist by ID. Note: Deletion behavior depends on cascade settings and may affect related albums."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "204",
			description = "Artist successfully deleted"
		),
		@ApiResponse(
			responseCode = "404",
			description = "Artist not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@DeleteMapping("/artists/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteArtist(
		@Parameter(description = "ID of the artist to delete", required = true)
		@PathVariable Long id) {
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
	@Operation(
		summary = "Create new album",
		description = "Creates an album and returns the created entity. Requires valid artistId and genreId references."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "Album successfully created",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Album.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Bad Request - Invalid input data or missing required fields",
			content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
			responseCode = "404",
			description = "Referenced artist or genre not found",
			content = @Content(mediaType = "application/json")
		)
	})
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
	@Operation(
		summary = "Get all albums",
		description = "Returns paginated list of albums with optional sorting. Supports pagination via query parameters: page (default: 0), size (default: 20), and sort (e.g., 'title,asc' or 'releaseYear,desc')"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved paginated list of albums",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
		)
	})
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
	@Operation(
		summary = "Get album by ID",
		description = "Returns a single album by its ID"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved album",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Album.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Album not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@GetMapping("/albums/{id}")
	public Album getAlbumById(
		@Parameter(description = "ID of the album to retrieve", required = true)
		@PathVariable Long id) {
		return albumSvc.findById(id);
	}

	/**
	 * Updates an existing album.
	 * 
	 * @param id the album ID to update
	 * @param request the updated album data (validated) containing title, artistId, genreIds, etc.
	 * @return the updated album entity
	 * @throws music.library.exception.ResourceNotFoundException if album, artist, or any genre not found (404)
	 */
	@Operation(
		summary = "Update album",
		description = "Updates an existing album and returns the updated entity. Only the fields provided in the request will be updated."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Album successfully updated",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Album.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Album, artist, or genre not found with the provided ID",
			content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
			responseCode = "400",
			description = "Bad Request - Invalid input data",
			content = @Content(mediaType = "application/json")
		)
	})
	@PutMapping("/albums/{id}")
	public Album updateAlbum(
		@Parameter(description = "ID of the album to update", required = true)
		@PathVariable Long id, 
		@Valid @RequestBody UpdateAlbumRequest request) {
		return albumSvc.updateAlbum(id, request);
	}

	/**
	 * Deletes an album by ID.
	 * 
	 * @param id the album ID to delete
	 * @throws music.library.exception.ResourceNotFoundException if album not found
	 *                                                           (404)
	 */
	@Operation(
		summary = "Delete album",
		description = "Deletes an album by ID"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "204",
			description = "Album successfully deleted"
		),
		@ApiResponse(
			responseCode = "404",
			description = "Album not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@DeleteMapping("/albums/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAlbum(
		@Parameter(description = "ID of the album to delete", required = true)
		@PathVariable Long id) {
		albumSvc.delete(id);
	}

	/**
	 * Creates a new genre.
	 * 
	 * @param request the genre creation request containing name and optional description
	 * @return the created genre entity with generated ID and timestamps
	 */
	@Operation(
		summary = "Create new genre",
		description = "Creates a genre and returns the created entity"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "Genre successfully created",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Genre.class))
		),
		@ApiResponse(
			responseCode = "400",
			description = "Bad Request - Invalid input data or missing required fields",
			content = @Content(mediaType = "application/json")
		)
	})
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
	@Operation(
		summary = "Get all genres",
		description = "Returns paginated list of genres with optional sorting. Supports pagination via query parameters: page (default: 0), size (default: 20), and sort (e.g., 'name,asc' or 'createdAt,desc')"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved paginated list of genres",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
		)
	})
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
	@Operation(
		summary = "Get genre by ID",
		description = "Returns a single genre by its ID"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved genre",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Genre.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Genre not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@GetMapping("/genres/{id}")
	public Genre getGenreById(
		@Parameter(description = "ID of the genre to retrieve", required = true)
		@PathVariable Long id) {
		return genreSvc.findById(id);
	}

	/**
	 * Updates an existing genre using a DTO.
	 * Validates that the genre exists before updating.
	 * Only updates the fields provided in the request DTO.
	 * 
	 * @param id the genre ID to update
	 * @param request the updated genre data (validated via DTO)
	 * @return the updated genre entity
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 * @throws org.springframework.web.bind.MethodArgumentNotValidException if validation fails (400)
	 */
	@Operation(
		summary = "Update genre",
		description = "Updates an existing genre and returns the updated entity. Only the fields provided in the request will be updated."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Genre successfully updated",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Genre.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Genre not found with the provided ID",
			content = @Content(mediaType = "application/json")
		),
		@ApiResponse(
			responseCode = "400",
			description = "Bad Request - Invalid input data",
			content = @Content(mediaType = "application/json")
		)
	})
	@PutMapping("/genres/{id}")
	public Genre updateGenre(
		@Parameter(description = "ID of the genre to update", required = true)
		@PathVariable Long id, 
		@Valid @RequestBody UpdateGenreRequest request) {
		return genreSvc.updateGenre(id, request);
	}

	/**
	 * Deletes a genre by ID.
	 * Note: Deletion may fail if albums reference this genre, depending on cascade settings.
	 * 
	 * @param id the genre ID to delete
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 */
	@Operation(
		summary = "Delete genre",
		description = "Deletes a genre by ID. Note: Deletion may fail if albums reference this genre."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "204",
			description = "Genre successfully deleted"
		),
		@ApiResponse(
			responseCode = "404",
			description = "Genre not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@DeleteMapping("/genres/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteGenre(
		@Parameter(description = "ID of the genre to delete", required = true)
		@PathVariable Long id) {
		genreSvc.delete(id);
	}

	/**
	 * Retrieves all albums by a specific artist.
	 * 
	 * @param artistId the artist ID
	 * @return list of albums by the artist (empty list if none found)
	 * @throws music.library.exception.ResourceNotFoundException if artist not found (404)
	 */
	@Operation(
		summary = "Get albums by artist",
		description = "Returns all albums by a specific artist. Returns an empty list if the artist has no albums."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved albums by artist",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Album.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Artist not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@GetMapping("/artists/{artistId}/albums")
	public List<Album> getAlbumsByArtist(
		@Parameter(description = "ID of the artist whose albums to retrieve", required = true)
		@PathVariable Long artistId) {
		return albumSvc.findByArtistId(artistId);
	}

	/**
	 * Retrieves all albums in a specific genre.
	 * 
	 * @param genreId the genre ID
	 * @return list of albums in the genre (empty list if none found)
	 * @throws music.library.exception.ResourceNotFoundException if genre not found (404)
	 */
	@Operation(
		summary = "Get albums by genre",
		description = "Returns all albums in a specific genre. Returns an empty list if the genre has no albums."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved albums by genre",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = Album.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "Genre not found with the provided ID",
			content = @Content(mediaType = "application/json")
		)
	})
	@GetMapping("/genres/{genreId}/albums")
	public List<Album> getAlbumsByGenre(
		@Parameter(description = "ID of the genre whose albums to retrieve", required = true)
		@PathVariable Long genreId) {
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
			@Parameter(description = "Confirmation flag - must be set to 'true' to execute the reset", required = true)
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
