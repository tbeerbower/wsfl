package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Data transfer object for creating a new draft")
public class DraftUpdateDto {

    @Schema(description = "Name of the draft", example = "Winter 2023")
    @NotNull(message = "Name is required")
    private String name;

    @Schema(description = "Number of rounds in the draft", example = "15")
    @NotNull(message = "Number of rounds is required")
    @Min(value = 1, message = "Number of rounds must be greater than 0")
    private Integer numberOfRounds;

    @Schema(description = "Whether the draft order should snake (reverse) each round", example = "true")
    @NotNull(message = "Snake order preference is required")
    private Boolean snakeOrder;

    @Schema(description = "Scheduled start time for the draft")
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    // Constructor
    public DraftUpdateDto(String name, Integer numberOfRounds,
                          Boolean snakeOrder, LocalDateTime startTime) {
        this.name = name;
        this.numberOfRounds = numberOfRounds;
        this.snakeOrder = snakeOrder;
        this.startTime = startTime;
    }

    // Getters
    public String getName() { return name; }
    public Integer getNumberOfRounds() { return numberOfRounds; }
    public Boolean getSnakeOrder() { return snakeOrder; }
    public LocalDateTime getStartTime() { return startTime; }
}
