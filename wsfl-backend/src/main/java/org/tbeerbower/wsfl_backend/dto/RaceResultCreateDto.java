package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Schema(description = "DTO for creating or updating a race result")
public class RaceResultCreateDto {
    @NotNull(message = "Race ID is required")
    @Min(value = 1, message = "Race ID must be positive")
    @Schema(description = "ID of the race", example = "1", required = true)
    private Long raceId;

    @NotNull(message = "Runner ID is required")
    @Min(value = 1, message = "Runner ID must be positive")
    @Schema(description = "ID of the runner", example = "1", required = true)
    private Long runnerId;

    @NotNull(message = "Gender place is required")
    @Min(value = 1, message = "Gender place must be positive")
    @Schema(description = "Runner's place within their gender category", example = "3", required = true)
    private Integer genderPlace;

    @Schema(description = "Runner's finish time", example = "01:23:45")
    private LocalTime finishTime;

    public RaceResultCreateDto() {
    }

    public RaceResultCreateDto(Long raceId, Long runnerId, Integer genderPlace, LocalTime finishTime) {
        this.raceId = raceId;
        this.runnerId = runnerId;
        this.genderPlace = genderPlace;
        this.finishTime = finishTime;
    }

    // Getters and Setters
    public Long getRaceId() { return raceId; }
    public void setRaceId(Long raceId) { this.raceId = raceId; }
    
    public Long getRunnerId() { return runnerId; }
    public void setRunnerId(Long runnerId) { this.runnerId = runnerId; }
    
    public Integer getGenderPlace() { return genderPlace; }
    public void setGenderPlace(Integer genderPlace) { this.genderPlace = genderPlace; }

    public LocalTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalTime finishTime) { this.finishTime = finishTime; }
}