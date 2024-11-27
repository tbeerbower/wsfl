package org.tbeerbower.wsfl_backend.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary information about a league")
@Relation(collectionRelation = "leagues", itemRelation = "league")
public class LeagueSummaryDto extends RepresentationModel<LeagueSummaryDto> {
    @Schema(description = "Unique identifier of the league")
    private Long id;

    @Schema(description = "Name of the league")
    private String name;

    @Schema(description = "Current season number")
    private Integer season;

    public LeagueSummaryDto(Long id, String name, Integer season) {
        this.id = id;
        this.name = name;
        this.season = season;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getSeason() { return season; }
}