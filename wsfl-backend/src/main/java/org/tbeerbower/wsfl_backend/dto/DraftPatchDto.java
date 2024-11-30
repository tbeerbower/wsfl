package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object for creating a new draft")
public class DraftPatchDto {

    @Schema(description = "Name of the draft", example = "Winter 2023")
    private String name;

    @Schema(description = "Number of rounds in the draft", example = "15")
    private Integer numberOfRounds;

    @Schema(description = "Whether the draft order should snake (reverse) each round", example = "true")
    private Boolean snakeOrder;

    @Schema(description = "Scheduled start time for the draft")
    private LocalDateTime startTime;

    @Schema(description = "Flag to indicate whether or not the draft is started", example = "true")
    private Boolean isStarted;

    @Schema(description = "Flag to indicate whether or not the draft is complete", example = "false")
    private Boolean isComplete;

    // Constructor
    public DraftPatchDto(String name, Integer numberOfRounds,
                         Boolean snakeOrder, LocalDateTime startTime,
                         Boolean started, Boolean complete) {
        this.name = name;
        this.numberOfRounds = numberOfRounds;
        this.snakeOrder = snakeOrder;
        this.startTime = startTime;
        this.isStarted = started;
        this.isComplete = complete;
    }

    // Getters
    public String getName() { return name; }
    public Integer getNumberOfRounds() { return numberOfRounds; }
    public Boolean getSnakeOrder() { return snakeOrder; }
    public LocalDateTime getStartTime() { return startTime; }
    public Boolean isStarted() { return isStarted; }
    public Boolean isComplete() { return isComplete; }
}
