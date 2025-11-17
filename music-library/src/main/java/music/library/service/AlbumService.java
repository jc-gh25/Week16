package music.library.service;

import lombok.RequiredArgsConstructor;
import music.library.entity.Album;
import music.library.entity.Genre;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;
import music.library.repository.GenreRepository;
import music.library.specification.AlbumSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepo;
	private final GenreRepository genreRepo;
	
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
		album.getGenres().add(genre);
		genre.getAlbums().add(album);
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
		albumRepo.save(album);
		genreRepo.save(genre);
		return album;
	}
	
	// Service method that combines the specifications
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
		// `JpaSpecificationExecutor.findAll(null, pageable)` treats a null spec as “match everything”.
		return albumRepo.findAll(spec, pageable);
	}
}