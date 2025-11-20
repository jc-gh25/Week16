package music.library.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for the API welcome/info endpoint.
 * 
 * This DTO provides comprehensive information about the Music Library API,
 * including project metadata, available endpoints, resources, and documentation links.
 * Designed to be displayed when users navigate to /api or /api/info.
 * 
 * @author JC - Backend Developer Bootcamp Portfolio
 */
public class ApiInfoResponse {
    
    private String title;
    private String description;
    private String version;
    private String author;
    private String email;
    private LocalDateTime timestamp;
    private String baseUrl;
    private Map<String, String> documentation;
    private List<EndpointCategory> endpoints;
    private List<String> features;
    private Map<String, String> metadata;
    
    // Constructors
    public ApiInfoResponse() {
    }
    
    public ApiInfoResponse(String title, String description, String version, String author, 
                          String email, String baseUrl, Map<String, String> documentation,
                          List<EndpointCategory> endpoints, List<String> features, 
                          Map<String, String> metadata) {
        this.title = title;
        this.description = description;
        this.version = version;
        this.author = author;
        this.email = email;
        this.timestamp = LocalDateTime.now();
        this.baseUrl = baseUrl;
        this.documentation = documentation;
        this.endpoints = endpoints;
        this.features = features;
        this.metadata = metadata;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public Map<String, String> getDocumentation() {
        return documentation;
    }
    
    public void setDocumentation(Map<String, String> documentation) {
        this.documentation = documentation;
    }
    
    public List<EndpointCategory> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoints(List<EndpointCategory> endpoints) {
        this.endpoints = endpoints;
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    /**
     * Inner class representing an endpoint category with its operations.
     */
    public static class EndpointCategory {
        private String category;
        private String description;
        private List<Endpoint> operations;
        
        public EndpointCategory() {
        }
        
        public EndpointCategory(String category, String description, List<Endpoint> operations) {
            this.category = category;
            this.description = description;
            this.operations = operations;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public List<Endpoint> getOperations() {
            return operations;
        }
        
        public void setOperations(List<Endpoint> operations) {
            this.operations = operations;
        }
    }
    
    /**
     * Inner class representing a single endpoint operation.
     */
    public static class Endpoint {
        private String method;
        private String path;
        private String description;
        private String responseCode;
        
        public Endpoint() {
        }
        
        public Endpoint(String method, String path, String description, String responseCode) {
            this.method = method;
            this.path = path;
            this.description = description;
            this.responseCode = responseCode;
        }
        
        public String getMethod() {
            return method;
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getResponseCode() {
            return responseCode;
        }
        
        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }
    }
}
