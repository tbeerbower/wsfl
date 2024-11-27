package org.tbeerbower.wsfl_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a new runner")
public class RunnerCreateDto {
    
    @NotBlank(message = "Name is required")
    @Schema(description = "Runner's full name", example = "John Doe")
    private String name;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(M|F)$", message = "Gender must be either 'M' or 'F'")
    @Schema(description = "Runner's gender (M or F)", example = "M")
    private String gender;
    
    // Constructors
    public RunnerCreateDto() {
    }
    
    public RunnerCreateDto(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
}
