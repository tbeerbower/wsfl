package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.time.LocalTime;

@Schema(description = "Detailed representation of a race result")
@Relation(collectionRelation = "raceResults", itemRelation = "raceResult")
public class RaceResultDetailsDto {
    
    @Schema(description = "Race result's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Runner's place within their gender category", example = "3")
    private Integer genderPlace;

    @Schema(description = "Runner's place overall", example = "3")
    private Integer overallPlace;

    @Schema(description = "Runner's finish time", example = "01:23:45")
    private String time;
    
    @Schema(description = "Race information")
    private RaceSummaryDto race;
    
    @Schema(description = "Runner information")
    private RunnerSummaryDto runner;

    public RaceResultDetailsDto(Long id, Integer genderPlace, Integer overallPlace, String time, RaceSummaryDto race, RunnerSummaryDto runner) {
        this.id = id;
        this.genderPlace = genderPlace;
        this.overallPlace = overallPlace;
        this.time = time;
        this.race = race;
        this.runner = runner;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public Integer getGenderPlace() { return genderPlace; }

    public Integer getOverallPlace() {
        return overallPlace;
    }

    public String getTime() {
        return time;
    }

    public RaceSummaryDto getRace() { return race; }
    public RunnerSummaryDto getRunner() { return runner; }
}