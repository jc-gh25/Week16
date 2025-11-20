package music.library.integration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponsePage<T> extends PageImpl<T> {
    
    private static final long serialVersionUID = 1L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(@JsonProperty("content") List<T> content,
                           @JsonProperty("number") Integer number,
                           @JsonProperty("size") Integer size,
                           @JsonProperty("totalElements") Long totalElements,
                           @JsonProperty("pageable") JsonNode pageable,
                           @JsonProperty("last") Boolean last,
                           @JsonProperty("totalPages") Integer totalPages,
                           @JsonProperty("sort") JsonNode sort,
                           @JsonProperty("first") Boolean first,
                           @JsonProperty("numberOfElements") Integer numberOfElements) {
        super(content != null ? content : new ArrayList<>(), 
              PageRequest.of(number != null ? number : 0, size != null ? size : 20), 
              totalElements != null ? totalElements : 0);
    }

    public RestResponsePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestResponsePage(List<T> content) {
        super(content);
    }

    public RestResponsePage() {
        super(new ArrayList<>());
    }
}