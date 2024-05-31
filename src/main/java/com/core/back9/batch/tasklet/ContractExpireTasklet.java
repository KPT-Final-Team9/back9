package com.core.back9.batch.tasklet;

import com.core.back9.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
public class ContractExpireTasklet implements Tasklet {

    private final ContractRepository contractRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters(); // 미리 설정해둔 jobParameter에서 빼옴
        LocalDate time = jobParameters.getLocalDateTime("time").toLocalDate();

        log.info("========== Start Change Contract Status ==========");
        log.info("--- 실행 일자 : {} ---", time);
        // 이행 조건이 충족된 경우 이행 상태로 업데이트
        int result = contractRepository.updateContractComplete(time); // 당일 만료 데이터만 변경 -> 테스트 결과에 따라 지연된 변경도 처리 할 지 고려해야함

        if (result == 0) {
            log.info("========== 변경할 이행 상태인 계약 내용이 없습니다. ==========");
        }
        log.info("===== result : {} =====", result);

        return RepeatStatus.FINISHED;

    }
}
