package music.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import music.library.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long>, JpaSpecificationExecutor<Album> {
    
    List<Album> findByArtist_ArtistId(Long artistId);
    
    List<Album> findByGenres_GenreId(Long genreId);
    
}

