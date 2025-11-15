package music.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> { }