package com.core.back9.batch.tasklet;

import com.core.back9.entity.Contract;
import com.core.back9.entity.Score;
import com.core.back9.entity.constant.ContractStatus;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.entity.constant.Status;
import com.core.back9.repository.ContractRepository;
import com.core.back9.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ManagementScoreTasklet implements Tasklet {

    private final ContractRepository contractRepository;
    private final ScoreRepository scoreRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Contract> progressContracts = contractRepository.findAllByContractStatus(ContractStatus.IN_PROGRESS);

        progressContracts.stream()
                .flatMap(contract -> contract.getTenant().getMembers().stream()
                        .map(member -> Score.builder()
                                .score(-1)
                                .comment("")
                                .bookmark(false)
                                .ratingType(RatingType.MANAGEMENT)
                                .room(contract.getRoom())
                                .member(member)
                                .status(Status.REGISTER)
                                .build()))
                .forEach(scoreRepository::save);

        return RepeatStatus.FINISHED;
    }
}
