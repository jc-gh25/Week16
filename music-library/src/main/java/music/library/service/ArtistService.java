package music.library.service;

import lombok.RequiredArgsConstructor;
import music.library.entity.Artist;
import music.library.exception.ResourceNotFoundException;
import music.library.repository.ArtistRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtistService {

    private final ArtistRepository repo;

    public List<Artist> findAll() {
        return repo.findAll();
    }
    
    public Page<Artist> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Artist findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(
        		"Artist with ID " + id + " not found"));
    }

    public Artist create(Artist a) {
        return repo.save(a);
    }

    public Artist update(Long id, Artist a) {
        Artist existing = findById(id);
        existing.setName(a.getName());
        existing.setDescription(a.getDescription());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}