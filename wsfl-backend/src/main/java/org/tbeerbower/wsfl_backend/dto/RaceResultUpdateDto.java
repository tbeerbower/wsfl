package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

@Schema(description = "Data transfer object for updating a race result")
public class RaceResultUpdateDto {

    @Schema(description = "The gender-specific place of the runner in the race", example = "5")
    @NotNull(message = "Gender place is required")
    @Min(value = 1, message = "Gender place must be greater than 0")
    private Integer genderPlace;

    @Schema(description = "The overall place of the runner in the race", example = "5")
    @NotNull(message = "Overall place is required")
    @Min(value = 1, message = "Overall place must be greater than 0")
    private Integer overallPlace;

    @Schema(description = "Runner's finish time", example = "01:23:45")
    @NotNull(message = "Finish time is required")
    private String time;

    // Getters and Setters
    public Integer getGenderPlace() {
        return genderPlace;
    }

    public void setGenderPlace(Integer genderPlace) {
        this.genderPlace = genderPlace;
    }

    public Integer getOverallPlace() {
        return overallPlace;
    }

    public void setOverallPlace(Integer overallPlace) {
        this.overallPlace = overallPlace;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
