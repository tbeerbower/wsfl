package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Detailed representation of a race")
@Relation(collectionRelation = "races", itemRelation = "race")
public class RaceDetailsDto {
    
    @Schema(description = "Race's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Name of the race", example = "Spring Championship")
    private String name;
    
    @Schema(description = "Date of the race", example = "2024-06-15")
    private LocalDate date;
    
    @Schema(description = "Whether this is a playoff race", example = "false")
    private Boolean isPlayoff;
    
    @Schema(description = "League this race belongs to")
    private LeagueSummaryDto league;
    
    @Schema(description = "List of race results")
    private List<RaceResultSummaryDto> results;

    public RaceDetailsDto(Long id, String name, LocalDate date, Boolean isPlayoff, 
                         LeagueSummaryDto league, List<RaceResultSummaryDto> results) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isPlayoff = isPlayoff;
        this.league = league;
        this.results = results;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public Boolean getIsPlayoff() { return isPlayoff; }
    public LeagueSummaryDto getLeague() { return league; }
    public List<RaceResultSummaryDto> getResults() { return results; }
}