package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.DraftSummaryDto;
import org.tbeerbower.wsfl_backend.dto.LeagueSummaryDto;
import org.tbeerbower.wsfl_backend.model.Draft;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class DraftDtoAssembler {

    private final LeagueDtoAssembler leagueDtoAssembler;

    public DraftDtoAssembler(LeagueDtoAssembler leagueDtoAssembler) {
        this.leagueDtoAssembler = leagueDtoAssembler;
    }

    public DraftSummaryDto toModel(Draft draft) {
        DraftSummaryDto dto = createDraftSummaryDto(draft);

        return dto;
    }

    private DraftSummaryDto createDraftSummaryDto(Draft draft) {
        LeagueSummaryDto leagueDto = leagueDtoAssembler.toSummaryDto(draft.getLeague());

        return new DraftSummaryDto(
            draft.getId(),
            leagueDto,
            draft.getName(),
            draft.getSeason(),
            draft.getNumberOfRounds(),
            draft.getSnakeOrder(),
            draft.getStartTime(),
            draft.isStarted(),
            draft.isComplete(),
            draft.getCurrentRound(),
            draft.getCurrentPick(),
            draft.getDraftOrder()
        );
    }
}
