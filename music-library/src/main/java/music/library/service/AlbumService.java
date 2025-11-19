package music.library.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Hibernate;    // for explicit init
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import music.library.dto.CreateAlbumRequest;
import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;
import music.library.repository.ArtistRepository;
import music.library.repository.GenreRepository;
import music.library.specification.AlbumSpecs;

@Service
@RequiredArgsConstructor
@Transactional  /* Guarantees a Hibernate Session (and therefore a first-level cache)
* lives for the entire method execution. make the method robust if we switch the fetch mode back to LAZY*/
public class AlbumService {

	private final AlbumRepository albumRepo;
	private final GenreRepository genreRepo;
	private final ArtistRepository artistRepo;
	
	// CRUD

	public List<Album> findAll() {
		return albumRepo.findAll();
	}

	public Page<Album> findAll(Pageable pageable) {
		return albumRepo.findAll(pageable);
	}

	public Album findById(Long id) {
		return albumRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
				"Album with id " + id + " not found"));
	}

	public Album create(Album a) {
		return albumRepo.save(a);
	}

	// Create album using DTO with artist and genre IDs
	public Album createAlbum(CreateAlbumRequest request) {
		// Fetch the artist
		Artist artist = artistRepo.findById(request.getArtistId())
			.orElseThrow(() -> new ResourceNotFoundException(
				"Artist with ID " + request.getArtistId() + " not found"));
		
		// Fetch all genres
		List<Genre> genres = new ArrayList<>();
		if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
			for (Long genreId : request.getGenreIds()) {
				Genre genre = genreRepo.findById(genreId)
					.orElseThrow(() -> new ResourceNotFoundException(
						"Genre with ID " + genreId + " not found"));
				genres.add(genre);
			}
		}
		
		// Create the album
		Album album = new Album();
		album.setTitle(request.getTitle());
		album.setReleaseDate(request.getReleaseDate());
		album.setArtist(artist);
		album.setGenres(new HashSet<>(genres));
		
		return albumRepo.save(album);
	}

	public Album update(Long id, Album a) {
		Album existing = findById(id);
		existing.setTitle(a.getTitle());
		existing.setReleaseDate(a.getReleaseDate());
		existing.setArtist(a.getArtist()); // optional, depends on your use‑case
		return albumRepo.save(existing);
	}

	public void delete(Long id) {
		albumRepo.deleteById(id);
	}
	

	// Join-table helpers 
	public Album addGenre(Long albumId, Long genreId) {
		Album album = findById(albumId);
		Genre genre = genreRepo.findById(genreId).orElseThrow(() -> new ResourceNotFoundException(
				"Genre with ID " + genreId + " not found"));
		album.getGenres().add(genre); // Owns the relation
		genre.getAlbums().add(album); // Keep inverse side in sync
        Hibernate.initialize(genre.getAlbums()); /* Force initialization of the inverse collection.
        * Guarantees the inverse collection is loaded while the transaction is open */
		albumRepo.save(album);
		genreRepo.save(genre);
		return album;
	}

	public Album removeGenre(Long albumId, Long genreId) {
		Album album = findById(albumId);
		Genre genre = genreRepo.findById(genreId).orElseThrow(() -> new ResourceNotFoundException(
				"Genre with ID " + genreId + " not found"));
		album.getGenres().remove(genre);
		genre.getAlbums().remove(album);
		Hibernate.initialize(genre.getAlbums()); // Initialize before we leave the transaction
		// No explicit save needed – the transaction will flush at commit,
        // but calling save() is harmless and makes the intent clear.
		albumRepo.save(album);
		genreRepo.save(genre);
		return album;
	}
	
	// Search
	public Page<Album> search(String title, Integer startYear, Integer endYear, Long genreId, Pageable pageable) {

		Specification<Album> spec = null;

		if (title != null && !title.isBlank()) {
		    spec = AlbumSpecs.titleContains(title);
		}
		if (startYear != null || endYear != null) {
		    spec = (spec == null)
		            ? AlbumSpecs.releasedBetween(startYear, endYear)
		            : spec.and(AlbumSpecs.releasedBetween(startYear, endYear));
		}
		if (genreId != null) {
		    spec = (spec == null)
		            ? AlbumSpecs.hasGenre(genreId)
		            : spec.and(AlbumSpecs.hasGenre(genreId));
		}

		// If the call supplied no criteria at all, `spec` will stay null.
		// `JpaSpecificationExecutor.findAll(null, pageable)` treats a null spec as "match everything".
		return albumRepo.findAll(spec, pageable);
	}
	
	public List<Album> findByArtistId(Long artistId) {
	    return albumRepo.findByArtist_ArtistId(artistId);
	}

	public List<Album> findByGenreId(Long genreId) {
	    return albumRepo.findByGenres_GenreId(genreId);
	}
}
