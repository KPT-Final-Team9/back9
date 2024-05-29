package com.core.back9.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig extends DefaultBatchConfiguration {

    @Bean
    public Job testJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Job job = new JobBuilder("testJob",jobRepository)
                .start(testStep(jobRepository,transactionManager))
                .build();
        return job;
    }

    public Step testStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step step = new StepBuilder("testStep",jobRepository)
                .tasklet(testTasklet(),transactionManager)
                .build();
        return step;
    }

    public Tasklet testTasklet(){
        return ((contribution, chunkContext) -> {
            System.out.println("***** 배치 테스트 *****");
            return RepeatStatus.FINISHED;
        });
    }

}
