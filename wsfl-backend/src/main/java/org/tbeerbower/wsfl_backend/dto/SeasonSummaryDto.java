package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.springframework.hateoas.server.core.Relation;
import org.tbeerbower.wsfl_backend.model.Draft;
import org.tbeerbower.wsfl_backend.model.Race;

import java.util.ArrayList;
import java.util.List;

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