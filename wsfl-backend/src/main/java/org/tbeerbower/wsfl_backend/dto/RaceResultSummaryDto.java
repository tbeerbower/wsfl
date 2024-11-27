package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of a runner's result in a race")
public class RaceResultSummaryDto {
    @Schema(description = "Unique identifier of the race result", example = "1")
    private final Long id;

    @Schema(description = "Runner's finishing position within their gender category", example = "3")
    private final Integer genderPlace;

    @Schema(description = "Details of the runner who achieved this result")
    private final RunnerSummaryDto runner;

    public RaceResultSummaryDto(Long id, Integer genderPlace, RunnerSummaryDto runner) {
        this.id = id;
        this.genderPlace = genderPlace;
        this.runner = runner;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public Integer getGenderPlace() { return genderPlace; }
    public RunnerSummaryDto getRunner() { return runner; }
}