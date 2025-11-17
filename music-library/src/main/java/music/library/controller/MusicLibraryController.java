package music.library.controller;

import lombok.RequiredArgsConstructor;
import music.library.entity.*;
import music.library.service.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicLibraryController {

    private final ArtistService artistSvc;
    private final AlbumService albumSvc;
    private final GenreService genreSvc;

    /* ---------- Artists ---------- */
    
    // Replaced with paginated version
    //@GetMapping("/artists")
    //public List<Artist> allArtists() { return artistSvc.findAll(); }
    
    // returns Page<Artist>
    @GetMapping("/artists")
    public Page<Artist> allArtists(Pageable pageable) {
        return artistSvc.findAll(pageable);
    }

    @PostMapping("/artists")
    public Artist createArtist(@Valid @RequestBody Artist a) { return artistSvc.create(a); }

    @GetMapping("/artists/{id}")
    public Artist getArtist(@PathVariable Long id) { return artistSvc.findById(id); }

    @PutMapping("/artists/{id}")
    public Artist updateArtist(@PathVariable Long id, @Valid @RequestBody Artist a) {
        return artistSvc.update(id, a);
    }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Albums ---------- */
    
    // Replaced with paginated version
    //@GetMapping("/albums")
    //public List<Album> allAlbums() { return albumSvc.findAll(); }
    
    // returns Page<Album>
    @GetMapping("/albums")
    public Page<Album> allAlbums(Pageable pageable) {
        // If the client omits page/size, Spring supplies a default (page=0, size=20)
        return albumSvc.findAll(pageable);
    }

    @PostMapping("/albums")
    public Album createAlbum(@Valid @RequestBody Album a) { return albumSvc.create(a); }

    @GetMapping("/albums/{id}")
    public Album getAlbum(@PathVariable Long id) { return albumSvc.findById(id); }

    @PutMapping("/albums/{id}")
    public Album updateAlbum(@PathVariable Long id, @Valid @RequestBody Album a) {
        return albumSvc.update(id, a);
    }

    @DeleteMapping("/albums/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Genres ---------- */
    
    // Replaced with paginated version
    //@GetMapping("/genres")
    //public List<Genre> allGenres() { return genreSvc.findAll(); }
    
    // overload that returns a Page<Genre>
    @GetMapping("/genres")
    public Page<Genre> allGenres(Pageable pageable) {
        return genreSvc.findAll(pageable);
    }

    @PostMapping("/genres")
    public Genre createGenre(@Valid @RequestBody Genre g) { return genreSvc.create(g); }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Long id) { return genreSvc.findById(id); }

    @PutMapping("/genres/{id}")
    public Genre updateGenre(@PathVariable Long id, @Valid @RequestBody Genre g) {
        return genreSvc.update(id, g);
    }

    @DeleteMapping("/genres/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Album-Genre join operations ---------- */
    @PostMapping("/albums/{albumId}/genres/{genreId}")
    public Album addGenreToAlbum(@PathVariable Long albumId,
                                 @PathVariable Long genreId) {
        return albumSvc.addGenre(albumId, genreId);
    }

    @DeleteMapping("/albums/{albumId}/genres/{genreId}")
    public Album removeGenreFromAlbum(@PathVariable Long albumId,
                                      @PathVariable Long genreId) {
        return albumSvc.removeGenre(albumId, genreId);
    }
    
    /* ---------- Search endpoint ---------- */
    @GetMapping("/albums/search")
    public Page<Album> searchAlbums(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Long genreId,
            Pageable pageable) {

        return albumSvc.search(title, startYear, endYear, genreId, pageable);
    }
}