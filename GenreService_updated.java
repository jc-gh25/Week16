package music.library.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import music.library.dto.CreateGenreRequest;
import music.library.entity.Album;
import music.library.entity.Genre;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.AlbumRepository;
import music.library.repository.GenreRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GenreService {

    private final GenreRepository repo;
    private final AlbumRepository albumRepo;   // needed for the join ops

    public List<Genre> findAll() {
        return repo.findAll();
    }
    
    public Page<Genre> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Genre findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
        		"Genre with ID " + id + " not found"));
    }

    public Genre create(Genre g) {
        return repo.save(g);
    }

    public Genre createGenre(CreateGenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        return repo.save(genre);
    }

    public Genre update(Long id, Genre g) {
        Genre existing = findById(id);
        existing.setName(g.getName());
        existing.setDescription(g.getDescription());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
    
    // Make the Genre - Album relationship fully bidirectional
    public Genre addAlbum(Long genreId, Long albumId) {
        Genre genre = repo.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with ID " + genreId + " not found"));
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album with ID " + albumId + " not found"));
        genre.getAlbums().add(album);
        return repo.save(genre);
    }

    public Genre removeAlbum(Long genreId, Long albumId) {
        Genre genre = repo.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with ID " + genreId + " not found"));
        Album album = albumRepo.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album with ID " + albumId + " not found"));
        genre.getAlbums().remove(album);
        return repo.save(genre);
    }
}