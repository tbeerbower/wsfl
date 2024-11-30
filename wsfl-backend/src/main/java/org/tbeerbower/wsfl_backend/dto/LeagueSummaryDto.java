package org.tbeerbower.wsfl_backend.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary information about a league")
@Relation(collectionRelation = "leagues", itemRelation = "league")
public class LeagueSummaryDto {
    @Schema(description = "Unique identifier of the league")
    private Long id;

    @Schema(description = "Name of the league")
    private String name;

    public LeagueSummaryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
}