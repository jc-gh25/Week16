package music.library.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponsePage<T> extends PageImpl<T> {
    
    private static final long serialVersionUID = 1L;

    // Default constructor
    public RestResponsePage() {
        super(new ArrayList<>());
    }

    // Constructor for simple content list
    public RestResponsePage(List<T> content) {
        super(content != null ? content : new ArrayList<>());
    }

    // Constructor with pageable and total
    public RestResponsePage(List<T> content, Pageable pageable, long total) {
        super(content != null ? content : new ArrayList<>(), pageable, total);
    }

    // Simplified Jackson constructor for deserialization
    @JsonCreator
    public RestResponsePage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("totalElements") Long totalElements) {
        
        super(
            content != null ? content : new ArrayList<>(), 
            PageRequest.of(0, Math.max(1, content != null ? content.size() : 1)), 
            totalElements != null ? totalElements : (content != null ? content.size() : 0)
        );
    }
}