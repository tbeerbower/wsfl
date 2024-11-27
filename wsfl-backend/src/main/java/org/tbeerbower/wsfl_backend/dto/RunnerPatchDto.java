package org.tbeerbower.wsfl_backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for partially updating a runner")
public class RunnerPatchDto {
    
    @Size(min = 1, max = 100, message = "Runner name must be between 1 and 100 characters")
    @Schema(description = "Runner's full name", example = "John Doe")
    private String name;
    
    @Pattern(regexp = "^(M|F)$", message = "Gender must be either 'M' or 'F'")
    @Schema(description = "Runner's gender (M or F)", example = "M")
    private String gender;
    
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
