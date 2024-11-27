package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Schema(description = "Detailed representation of a runner")
@Relation(collectionRelation = "runners", itemRelation = "runner")
public class RunnerDetailsDto extends RepresentationModel<RunnerDetailsDto> {
    
    @Schema(description = "Runner's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Runner's full name", example = "John Doe")
    private String name;
    
    @Schema(description = "Runner's gender (M or F)", example = "M")
    private String gender;
    
    @Schema(description = "Teams the runner belongs to")
    private List<TeamSummaryDto> teams;

    public RunnerDetailsDto(Long id, String name, String gender, List<TeamSummaryDto> teams) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.teams = teams;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public List<TeamSummaryDto> getTeams() { return teams; }
}