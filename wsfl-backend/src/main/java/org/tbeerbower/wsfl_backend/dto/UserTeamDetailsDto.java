package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed representation of a team")
public class UserTeamDetailsDto {
    @Schema(description = "Unique identifier of the team", example = "1")
    private Long id;

    @Schema(description = "Name of the team", example = "Speed Demons")
    private String name;

    @Schema(description = "Summary of the team owner/manager")
    private UserSummaryDto owner;

    public UserTeamDetailsDto(Long id, String name,
                              UserSummaryDto owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public UserSummaryDto getOwner() { return owner; }
}