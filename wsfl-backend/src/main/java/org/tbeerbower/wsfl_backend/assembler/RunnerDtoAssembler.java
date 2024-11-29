package org.tbeerbower.wsfl_backend.assembler;

import org.springframework.stereotype.Component;
import org.tbeerbower.wsfl_backend.dto.RunnerSummaryDto;
import org.tbeerbower.wsfl_backend.model.Runner;

@Component
public class RunnerDtoAssembler {

    public RunnerSummaryDto toSummaryDto(Runner runner) {
        return new RunnerSummaryDto(
            runner.getId(),
            runner.getName(),
            runner.getGender()
        );
    }
}
