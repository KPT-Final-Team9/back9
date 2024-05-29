package com.core.back9.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@RequiredArgsConstructor
@Slf4j
public class testTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("***** hello test! *****");
        // 이행 조건이 충족된 경우 이행 상태로 업데이트

        // 만료 조건이 충족된 경우 만료 상태로 업데이트

        return RepeatStatus.FINISHED;
    }
}