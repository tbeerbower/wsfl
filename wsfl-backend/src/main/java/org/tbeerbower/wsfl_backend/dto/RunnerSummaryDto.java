package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary representation of a runner")
public class RunnerSummaryDto {
    
    @Schema(description = "Runner's unique identifier", example = "1")
    private final Long id;
    
    @Schema(description = "Runner's full name", example = "John Doe")
    private final String name;
    
    @Schema(description = "Runner's gender (M or F)", example = "M")
    private final String gender;

    public RunnerSummaryDto(Long id, String name, String gender) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
}