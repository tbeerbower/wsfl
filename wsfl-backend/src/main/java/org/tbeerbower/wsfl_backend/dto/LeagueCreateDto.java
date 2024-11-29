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

    @Schema(description = "Max number of teams in league", example = "8")
    @NotBlank(message = "Max teams is required")
    private Integer maxTeams;

    @Schema(description = "ID of the league administrator", example = "1")
    @NotNull(message = "Admin ID is required")
    private Long adminId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
