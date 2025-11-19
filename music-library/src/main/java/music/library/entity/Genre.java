package music.library.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "genre")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
// @EqualsAndHashCode
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genreId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Inverse side of the many-to-many.
    @JsonIgnore /*This hides the collection from the JSON representation, 
    * breaking the cycle for serialization and for OpenAPI generation.*/
    @ManyToMany(mappedBy = "genres", fetch = FetchType.EAGER) 
    /* Fetch eagerly so that test code 
    *  (which runs outside a transaction) can safely call genre.getAlbums(). 
    *  In a production API we would probably keep it lazy and wrap the test in a transaction, 
    *  but for a bootcamp assignment the eager fetch is acceptable and keeps the code simple.*/
    @EqualsAndHashCode.Exclude  //Avoids accidental recursion; prevents StackOverflowErrors
    // if you ever put Genre in a Set that relies on equals().
    @Builder.Default  //When the builder is used, start with the value given in the field declaration 
    // unless the call explicitly sets something else.
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