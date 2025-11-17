package music.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // enables auto‑configuration, component scan, etc.
public class MusicLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicLibraryApplication.class, args);
    }
}
