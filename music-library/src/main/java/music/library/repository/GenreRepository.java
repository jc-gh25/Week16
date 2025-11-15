package music.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import music.library.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> { }