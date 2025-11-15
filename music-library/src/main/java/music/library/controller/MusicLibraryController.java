package music.library.controller;

import music.library.entity.*;
import music.library.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicLibraryController {

    private final ArtistService artistSvc;
    private final AlbumService  albumSvc;
    private final GenreService  genreSvc;

    /* ---------- Artist ---------- */
    @GetMapping("/artists")
    public List<Artist> allArtists() { return artistSvc.findAll(); }

    @PostMapping("/artists")
    public Artist createArtist(@RequestBody Artist a) { return artistSvc.create(a); }

    @GetMapping("/artists/{id}")
    public Artist getArtist(@PathVariable Long id) { return artistSvc.findById(id); }

    @PutMapping("/artists/{id}")
    public Artist updateArtist(@PathVariable Long id, @RequestBody Artist a) { return artistSvc.update(id, a); }

    @DeleteMapping("/artists/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Album ---------- */
    @GetMapping("/albums")
    public List<Album> allAlbums() { return albumSvc.findAll(); }

    @PostMapping("/albums")
    public Album createAlbum(@RequestBody Album a) { return albumSvc.create(a); }

    @GetMapping("/albums/{id}")
    public Album getAlbum(@PathVariable Long id) { return albumSvc.findById(id); }

    @PutMapping("/albums/{id}")
    public Album updateAlbum(@PathVariable Long id, @RequestBody Album a) { return albumSvc.update(id, a); }

    @DeleteMapping("/albums/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Long id) {
        albumSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Genre ---------- */
    @GetMapping("/genres")
    public List<Genre> allGenres() { return genreSvc.findAll(); }

    @PostMapping("/genres")
    public Genre createGenre(@RequestBody Genre g) { return genreSvc.create(g); }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable Long id) { return genreSvc.findById(id); }

    @PutMapping("/genres/{id}")
    public Genre updateGenre(@PathVariable Long id, @RequestBody Genre g) { return genreSvc.update(id, g); }

    @DeleteMapping("/genres/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreSvc.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- Join‑table operations ---------- */
    @PostMapping("/albums/{albumId}/genres/{genreId}")
    public Album addGenreToAlbum(@PathVariable Long albumId, @PathVariable Long genreId) {
        return albumSvc.addGenre(albumId, genreId);
    }

    @DeleteMapping("/albums/{albumId}/genres/{genreId}")
    public Album removeGenreFromAlbum(@PathVariable Long albumId, @PathVariable Long genreId) {
        return albumSvc.removeGenre(albumId, genreId);
    }
}