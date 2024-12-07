package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed representation of a league")
public class UserLeagueDetailsDto {
    @Schema(description = "Unique identifier of the league", example = "1")
    private Long id;

    @Schema(description = "Name of the league", example = "Fantasy Premier League 2024")
    private String name;

    @Schema(description = "The league administrator", example = "1")
    private UserSummaryDto admin;

    @Schema(description = "List of drafts for the league")
    private List<UserDraftDetailsDto> drafts;

    public UserLeagueDetailsDto(Long id, String name, UserSummaryDto admin,
                                List<UserDraftDetailsDto> drafts) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.drafts = drafts;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public UserSummaryDto getAdmin() { return admin; }
    public List<UserDraftDetailsDto> getDrafts() { return drafts; }
}