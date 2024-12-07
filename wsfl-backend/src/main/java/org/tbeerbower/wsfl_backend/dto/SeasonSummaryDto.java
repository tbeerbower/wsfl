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

    @Schema(description = "Is the season complete")
    private boolean isComplete;

    public SeasonSummaryDto(Long id, String name, boolean isComplete) {
        this.id = id;
        this.name = name;
        this.isComplete = isComplete;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isComplete() { return isComplete; }
}