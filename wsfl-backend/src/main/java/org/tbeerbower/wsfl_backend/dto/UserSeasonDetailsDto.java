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

    @Schema(description = "List of matchups for the season")
    private List<UserMatchupDetailsDto> matchups;


    public UserSeasonDetailsDto(Long id, String name, boolean isComplete) {
        this.id = id;
        this.name = name;
        this.isComplete = isComplete;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isComplete() { return isComplete; }
}