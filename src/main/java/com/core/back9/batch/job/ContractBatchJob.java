package com.core.back9.batch.job;

import com.core.back9.batch.tasklet.ContractExpireTasklet;
import com.core.back9.batch.tasklet.ContractInProgressTasklet;
import com.core.back9.batch.property.BatchProperty;
import com.core.back9.repository.ContractRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Getter
@Slf4j
public class ContractBatchJob extends DefaultBatchConfiguration implements BatchConfig {

    private final BatchProperty batchProperty;
    private final ContractRepository contractRepository;

    public ContractBatchJob(@Qualifier("contractBatchProperty") BatchProperty batchProperty, ContractRepository contractRepository) {
        this.batchProperty = batchProperty;
        this.contractRepository = contractRepository;
    }

    @Override
    public String getIdentifier() { // scheduler에서 알맞은 객체 value를 꺼내기 위한 식별자 지정
        return "contractBatchConfig";
    }

    @Bean("contract")
    @Override
    public Job createJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Job job = new JobBuilder(batchProperty.getJobName(), jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeStep(jobRepository, transactionManager))
                .next(lastStep(jobRepository, transactionManager))
                .build();
        return job;
    }

    @Override
    public Step executeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("contractStep : update ContractStatus COMPLETE --> IN_PROGRESS", jobRepository)
                .tasklet(new ContractInProgressTasklet(contractRepository), transactionManager) // 생성한 tasklet 부착
                .build();
    }

    public Step lastStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("contractStep : update ContractStatus IN_PROGRESS --> EXPIRED", jobRepository)
                .tasklet(new ContractExpireTasklet(contractRepository), transactionManager) // 생성한 tasklet 부착
                .build();
    }

}
