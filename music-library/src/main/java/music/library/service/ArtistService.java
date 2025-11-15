package music.library.service;

import lombok.RequiredArgsConstructor;
import music.library.entity.Artist;
import music.library.repository.ArtistRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository repo;

    public List<Artist> findAll() => repo.findAll();
    public Artist findById(Long id) => repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    public Artist create(Artist a)  => repo.save(a);
    public Artist update(Long id, Artist a){
        Artist existing = findById(id);
        existing.setName(a.getName());
        existing.setDescription(a.getDescription());
        return repo.save(existing);
    }
    public void delete(Long id) => repo.deleteById(id);
    
}