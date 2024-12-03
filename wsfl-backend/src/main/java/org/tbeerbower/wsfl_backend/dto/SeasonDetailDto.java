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


    private List<DraftSummaryDto> drafts = new ArrayList<>();

    private List<RaceSummaryDto> races = new ArrayList<>();



    public SeasonDetailDto(Long id, String name, List<DraftSummaryDto> drafts, List<RaceSummaryDto> races) {
        this.id = id;
        this.name = name;
        this.drafts = drafts;
        this.races =races;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<DraftSummaryDto> getDrafts() { return drafts; }
    public List<RaceSummaryDto> getRaces() { return races; }
}