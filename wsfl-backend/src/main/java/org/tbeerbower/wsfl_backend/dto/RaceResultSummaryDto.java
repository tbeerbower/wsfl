package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of a runner's result in a race")
public class RaceResultSummaryDto {
    @Schema(description = "Unique identifier of the race result", example = "1")
    private final Long id;

    @Schema(description = "Runner's finishing position within their gender category", example = "3")
    private final Integer genderPlace;

    @Schema(description = "Runner's finishing position overall", example = "3")
    private final Integer overallPlace;

    @Schema(description = "Runner's finish time", example = "01:23:45")
    private String time;

    @Schema(description = "Details of the runner who achieved this result")
    private final RunnerSummaryDto runner;

    public RaceResultSummaryDto(Long id, Integer genderPlace, Integer overallPlace, String time, RunnerSummaryDto runner) {
        this.id = id;
        this.genderPlace = genderPlace;
        this.overallPlace = overallPlace;
        this.time = time;
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

    public RunnerSummaryDto getRunner() { return runner; }
}