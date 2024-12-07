package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Schema(description = "Summary information about a draft")
@Relation(collectionRelation = "drafts", itemRelation = "draft")
public class UserDraftDetailsDto {
    @Schema(description = "Unique identifier of the draft")
    private Long id;

    @Schema(description = "Draft name")
    private String name;

    @Schema(description = "Season of the draft")
    private UserSeasonDetailsDto season;

    @Schema(description = "Number of rounds in the draft")
    private Integer numberOfRounds;

    @Schema(description = "Draft status")
    private String status;

    @Schema(description = "Current round number")
    private Integer currentRound;

    @Schema(description = "Current pick number within the round")
    private Integer currentPick;

    @Schema(description = "List of standings for the draft")
    private List<UserStandingDto> standings;

    @Schema(description = "List of matchups for the draft")
    private List<UserTeamMatchupsDetailsDto> teams;

    public UserDraftDetailsDto(Long id, String name, UserSeasonDetailsDto season,
                               Integer numberOfRounds, String status,
                               Integer currentRound, Integer currentPick,
                               List<UserStandingDto> standings,
                               List<UserTeamMatchupsDetailsDto> teams) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.numberOfRounds = numberOfRounds;
        this.status = status;
        this.currentRound = currentRound;
        this.currentPick = currentPick;
        this.standings = standings;
        this.teams = teams;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public UserSeasonDetailsDto getSeason() { return season; }
    public Integer getNumberOfRounds() { return numberOfRounds; }
    public String getStatus() { return status; }
    public Integer getCurrentRound() { return currentRound; }
    public Integer getCurrentPick() { return currentPick; }

    public List<UserStandingDto> getStandings() {
        return standings;
    }
    public List<UserTeamMatchupsDetailsDto> getTeams() {
        return teams;
    }
}