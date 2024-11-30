package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@Schema(description = "Data transfer object for updating a race")
public class RaceUpdateDto {
    
    @Schema(description = "Name of the race", example = "Spring Championship")
    @NotBlank(message = "Race name is required")
    private String name;
    
    @Schema(description = "Date of the race", example = "2024-06-15")
    @NotNull(message = "Race date is required")
    @FutureOrPresent(message = "Race date must be in the present or future")
    private LocalDate date;
    
    @Schema(description = "Whether this is a playoff race", example = "false")
    @NotNull(message = "Playoff status is required")
    private Boolean isPlayoff;
    
    @Schema(description = "ID of the season this race belongs to", example = "1")
    @NotNull(message = "Season ID is required")
    private Long seasonId;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Boolean getIsPlayoff() { return isPlayoff; }
    public void setIsPlayoff(Boolean isPlayoff) { this.isPlayoff = isPlayoff; }
    
    public Long getSeasonId() { return seasonId; }
    public void setSeasonId(Long seasonId) { this.seasonId = seasonId; }
}
