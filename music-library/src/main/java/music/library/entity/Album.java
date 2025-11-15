package music.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "album")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;

    @Column(nullable = false, length = 255)
    private String title;

    private Integer releaseYear;   // YEAR maps nicely to Integer

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* Many‑to‑one with Artist */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Artist artist;

    /* Many‑to‑many with Genre via join table */
    @ManyToMany
    @JoinTable(
        name = "album_genre",
        joinColumns = @JoinColumn(name = "album_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}