package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for error responses")
public class ErrorDto {
    
    @Schema(description = "Error message", example = "Resource not found")
    private final String message;
    
    @Schema(description = "HTTP status code", example = "404")
    private final int status;
    
    @Schema(description = "Error timestamp", example = "2024-01-20T10:15:30.123Z")
    private final String timestamp;
    
    @Schema(description = "Request path", example = "/api/races/123")
    private final String path;

    public ErrorDto(String message, int status, String timestamp, String path) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    // Getters
    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    public String getPath() { return path; }
}
