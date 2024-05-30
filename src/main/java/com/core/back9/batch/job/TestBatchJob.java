package com.core.back9.batch.job;

import com.core.back9.batch.property.BatchProperty;
import com.core.back9.batch.tasklet.TestTasklet;
import com.core.back9.repository.ContractRepository;
import lombok.*;
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
public class TestBatchJob extends DefaultBatchConfiguration implements BatchConfig {

    private final BatchProperty batchProperty;
    private final ContractRepository contractRepository;

    public TestBatchJob(@Qualifier("testBatchProperty") BatchProperty batchProperty, ContractRepository contractRepository) {
        this.batchProperty = batchProperty;
        this.contractRepository = contractRepository;
    }

    @Override
    public String getIdentifier() {
        return "testBatchConfig";
    }

    @Bean("test")
    @Override
    public Job createJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Job job = new JobBuilder(batchProperty.getJobName(), jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeStep(jobRepository, transactionManager))
                .build();
        return job;
    }

    @Override
    public Step executeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step step = new StepBuilder("testStep", jobRepository)
                .tasklet(new TestTasklet(), transactionManager)
                .build();
        return step;
    }

}
