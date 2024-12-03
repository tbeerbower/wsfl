package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.DraftSummaryDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.dto.SeasonSummaryDto;
import org.tbeerbower.wsfl_backend.model.Draft;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class DraftDtoAssembler {

    private final LeagueDtoAssembler leagueDtoAssembler;
    private final SeasonDtoAssembler seasonDtoAssembler;

    public DraftDtoAssembler(LeagueDtoAssembler leagueDtoAssembler, SeasonDtoAssembler seasonDtoAssembler) {
        this.leagueDtoAssembler = leagueDtoAssembler;
        this.seasonDtoAssembler = seasonDtoAssembler;
    }

    public DraftSummaryDto toModel(Draft draft) {
        DraftSummaryDto dto = createDraftSummaryDto(draft);

        return dto;
    }

    private DraftSummaryDto createDraftSummaryDto(Draft draft) {
        LeagueSummaryDto leagueDto = leagueDtoAssembler.toSummaryDto(draft.getLeague());
        SeasonSummaryDto seasonDto = seasonDtoAssembler.toModel(draft.getSeason());

        return new DraftSummaryDto(
            draft.getId(),
            leagueDto,
            draft.getName(),
            seasonDto,
            draft.getNumberOfRounds(),
            draft.getSnakeOrder(),
            draft.getStartTime(),
            draft.isStarted(),
            draft.isComplete(),
            draft.getStatus(),
            draft.getCurrentRound(),
            draft.getCurrentPick(),
            draft.getDraftOrder()
        );
    }
}
