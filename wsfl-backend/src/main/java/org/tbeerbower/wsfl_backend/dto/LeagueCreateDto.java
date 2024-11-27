package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Schema(description = "DTO for creating a new league")
public class LeagueCreateDto {
    @Schema(description = "Name of the league", example = "Fantasy Premier League 2024")
    @NotBlank(message = "League name is required")
    private String name;

    @Schema(description = "Season year for the league", example = "2024", minimum = "1900", maximum = "9999")
    @NotNull(message = "Season is required")
    @Min(value = 1900, message = "Season year must be after 1900")
    @Max(value = 9999, message = "Season year must be before 9999")
    private Integer season;

    @Schema(description = "ID of the league administrator", example = "1")
    @NotNull(message = "Admin ID is required")
    private Long adminId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
