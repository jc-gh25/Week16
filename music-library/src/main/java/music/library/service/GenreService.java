package music.library.service;

import java.util.List;

import music.library.entity.Album;
import music.library.entity.Artist;
import music.library.repository.AlbumRepository;
import music.library.repository.GenreRepository;

public class GenreService {
	
	private final GenreRepository repo;

    public List<Genre> findAll() => repo.findAll();
    public Genre findById(Long id) => repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    public Genre create(Artist a)  => repo.save(a);
    public Genre update(Long id, Artist a){
    	Genre existing = findById(id);
        existing.setName(a.getName());
        existing.setDescription(a.getDescription());
        return repo.save(existing);
    }
    public void delete(Long id) => repo.deleteById(id);

}
