package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object for creating a new draft pick")
public class DraftPickCreateDto {

    @Schema(description = "ID of the runner for this draft pick", example = "1")
    @NotNull(message = "Runner ID is required")
    private Long runnerId;


    // Constructor

    public DraftPickCreateDto() {
    }

    public DraftPickCreateDto(Long runnerId) {
        this.runnerId = runnerId;

    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
    }

    // Getters
    public Long getRunnerId() { return runnerId; }
}
