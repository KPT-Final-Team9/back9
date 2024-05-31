package com.core.back9.batch.scheduler;

import com.core.back9.batch.job.BatchConfigFactory;
import com.core.back9.batch.job.BatchConfig;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ContractBatchScheduler extends BatchScheduler {

    public ContractBatchScheduler(JobLauncher jobLauncher, JobRegistry jobRegistry, BatchConfigFactory batchConfigFactory) {
        super(jobLauncher, jobRegistry, batchConfigFactory);

        BatchConfig batchConfig = batchConfigFactory.getBatchConfig("contractBatchConfig");

        if (batchConfig.getBatchProperty().isJobEnabled()) {
            startScheduler(batchConfig.getIdentifier());
        }
    }

}
