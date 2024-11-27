package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.time.LocalTime;

@Schema(description = "Detailed representation of a race result")
@Relation(collectionRelation = "raceResults", itemRelation = "raceResult")
public class RaceResultDetailsDto extends RepresentationModel<RaceResultDetailsDto> {
    
    @Schema(description = "Race result's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Runner's place within their gender category", example = "3")
    private Integer genderPlace;

    @Schema(description = "Runner's finish time", example = "01:23:45")
    private LocalTime finishTime;
    
    @Schema(description = "Race information")
    private RaceSummaryDto race;
    
    @Schema(description = "Runner information")
    private RunnerSummaryDto runner;

    public RaceResultDetailsDto(Long id, Integer genderPlace, LocalTime finishTime, RaceSummaryDto race, RunnerSummaryDto runner) {
        this.id = id;
        this.genderPlace = genderPlace;
        this.finishTime = finishTime;
        this.race = race;
        this.runner = runner;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public Integer getGenderPlace() { return genderPlace; }
    public LocalTime getFinishTime() { return finishTime; }
    public RaceSummaryDto getRace() { return race; }
    public RunnerSummaryDto getRunner() { return runner; }
}