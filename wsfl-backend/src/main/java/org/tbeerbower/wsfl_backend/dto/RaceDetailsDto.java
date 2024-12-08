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
    
    @Schema(description = "Season this race belongs to")
    private SeasonSummaryDto season;
    
    @Schema(description = "List of race results")
    private List<RaceResultSummaryDto> results;

    public RaceDetailsDto(Long id, String name, LocalDate date,
                         SeasonSummaryDto season, List<RaceResultSummaryDto> results) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.season = season;
        this.results = results;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public SeasonSummaryDto getSeason() { return season; }
    public List<RaceResultSummaryDto> getResults() { return results; }
}