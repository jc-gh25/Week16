package music.library.service;

import java.util.List;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.entity.Genre;
import music.library.repository.AlbumRepository;

public class AlbumService {
	
	private final AlbumRepository repo;

    public List<Album> findAll() => repo.findAll();
    public Album findById(Long id) => repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    public Album create(Artist a)  => repo.save(a);
    public Album update(Long id, Artist a){
    	Album existing = findById(id);
        existing.setName(a.getName());
        existing.setDescription(a.getDescription());
        return repo.save(existing);
    }
    public void delete(Long id) => repo.deleteById(id);
    
    public Album addGenre(Long albumId, Long genreId) {
        Album album = albumRepo.findById(albumId).orElseThrow(...);
        Genre genre = genreRepo.findById(genreId).orElseThrow(...);
        album.getGenres().add(genre);
        return albumRepo.save(album);   // INSERT into album_genre
    }

    public Album removeGenre(Long albumId, Long genreId) {
        Album album = albumRepo.findById(albumId).orElseThrow(...);
        Genre genre = genreRepo.findById(genreId).orElseThrow(...);
        album.getGenres().remove(genre);
        return albumRepo.save(album);   // DELETE from album_genre
    }

}
