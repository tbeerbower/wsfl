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

    public DraftDtoAssembler(LeagueDtoAssembler leagueDtoAssembler) {
        this.leagueDtoAssembler = leagueDtoAssembler;
    }

    public DraftSummaryDto toModel(Draft draft) {
        DraftSummaryDto dto = createDraftSummaryDto(draft);
        return dto;
    }

    private DraftSummaryDto createDraftSummaryDto(Draft draft) {
        LeagueSummaryDto leagueDto = leagueDtoAssembler.toSummaryDto(draft.getLeague());

        SeasonSummaryDto seasonDto = new SeasonSummaryDto(
                draft.getSeason().getId(),
                draft.getSeason().getName(),
                draft.getSeason().isComplete()
        );

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
