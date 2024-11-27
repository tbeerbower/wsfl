package org.tbeerbower.wsfl_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.time.LocalDate;

@Schema(description = "Summary representation of a race")
@Relation(collectionRelation = "races", itemRelation = "race")
public class RaceSummaryDto {
    
    @Schema(description = "Race's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Name of the race", example = "Spring Championship")
    private String name;
    
    @Schema(description = "Date of the race", example = "2024-06-15")
    private LocalDate date;
    
    @Schema(description = "Whether this is a playoff race", example = "false")
    private Boolean isPlayoff;

    public RaceSummaryDto(Long id, String name, LocalDate date, Boolean isPlayoff) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isPlayoff = isPlayoff;
    }

    // Getters only for immutability
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public Boolean getIsPlayoff() { return isPlayoff; }
}