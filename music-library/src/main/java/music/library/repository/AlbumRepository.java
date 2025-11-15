package music.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> { }

