package music.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "artist")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;   // renamed from bio

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* One‑to‑many with Album */
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude   // avoid circular equals/hashcode
    private Set<Album> albums = new HashSet<>();

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}