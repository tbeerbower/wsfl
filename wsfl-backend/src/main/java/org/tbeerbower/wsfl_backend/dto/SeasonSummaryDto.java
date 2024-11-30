package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

@Schema(description = "Summary information about a season")
public class SeasonSummaryDto {
    @Schema(description = "Unique identifier of the season")
    private Long id;

    @Schema(description = "Name of the season")
    private String name;


    public SeasonSummaryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
}