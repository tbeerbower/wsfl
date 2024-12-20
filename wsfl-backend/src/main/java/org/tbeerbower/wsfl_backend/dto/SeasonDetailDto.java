package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Summary information about a season")
public class SeasonDetailDto {
    @Schema(description = "Unique identifier of the season")
    private Long id;

    @Schema(description = "Name of the season")
    private String name;


    @Schema(description = "Is the season complete")
    private boolean isComplete;

    @Schema(description = "The season drafts")

    private List<DraftSummaryDto> drafts = new ArrayList<>();

    @Schema(description = "The season races")

    private List<RaceSummaryDto> races = new ArrayList<>();



    public SeasonDetailDto(Long id, String name, boolean isComplete,
                           List<DraftSummaryDto> drafts, List<RaceSummaryDto> races) {
        this.id = id;
        this.name = name;
        this.isComplete = isComplete;
        this.drafts = drafts;
        this.races =races;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<DraftSummaryDto> getDrafts() { return drafts; }
    public List<RaceSummaryDto> getRaces() { return races; }
    public boolean isComplete() { return isComplete; }
}