package com.core.back9.batch.job;

import com.core.back9.batch.property.BatchProperty;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

public interface BatchConfig {

    BatchProperty getBatchProperty();

    String getIdentifier();

    Job createJob(JobRepository jobRepository, PlatformTransactionManager transactionManager);

    Step executeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager);
}
