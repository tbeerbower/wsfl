package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Summary information about a season")
public class UserSeasonDetailsDto {
    @Schema(description = "Unique identifier of the season")
    private Long id;

    @Schema(description = "Name of the season")
    private String name;

    @Schema(description = "Is the season complete")
    private boolean isComplete;

    @Schema(description = "The season status")
    private String status;



    public UserSeasonDetailsDto(Long id, String name, boolean isComplete, String status) {
        this.id = id;
        this.name = name;
        this.isComplete = isComplete;
        this.status = status;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isComplete() { return isComplete; }
    public String getStatus() { return status; }
}