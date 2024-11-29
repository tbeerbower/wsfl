package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Schema(description = "DTO for updating league properties")
public class LeaguePatchDto {
    @Schema(description = "Name of the league", example = "Fantasy Premier League 2024", maxLength = 100)
    @Size(min = 1, max = 100, message = "League name must be between 1 and 100 characters")
    private String name;

    @Schema(description = "ID of the league administrator", example = "1", nullable = true)
    private Long adminId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
