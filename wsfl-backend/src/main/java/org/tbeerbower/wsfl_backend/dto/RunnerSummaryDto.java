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

    @Schema(description = "Average overall place", example = "5.0")
    private final Double averageOverallPlace;
    @Schema(description = "Average gender place", example = "3.0")
    private final Double averageGenderPlace;
    @Schema(description = "Total number of races", example = "10")
    private final Integer totalRaces;

    public RunnerSummaryDto(Long id, String name, String gender, Double averageOverallPlace, Double averageGenderPlace, Integer totalRaces) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.averageOverallPlace = averageOverallPlace;
        this.averageGenderPlace = averageGenderPlace;
        this.totalRaces = totalRaces;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public Double getAverageOverallPlace() { return averageOverallPlace; }
    public Double getAverageGenderPlace() { return averageGenderPlace; }
    public Integer getTotalRaces() { return totalRaces; }
}