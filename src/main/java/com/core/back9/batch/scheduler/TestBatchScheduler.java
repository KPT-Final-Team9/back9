package com.core.back9.batch.scheduler;

import com.core.back9.batch.job.BatchConfigFactory;
import com.core.back9.batch.job.BatchConfig;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
public class TestBatchScheduler extends BatchScheduler {

    public TestBatchScheduler(JobLauncher jobLauncher, JobRegistry jobRegistry, BatchConfigFactory batchConfigFactory) {
        super(jobLauncher, jobRegistry, batchConfigFactory);

        BatchConfig batchConfig = batchConfigFactory.getBatchConfig("testBatchConfig");

        if (batchConfig.getBatchProperty().isJobEnabled()) {
            startScheduler(batchConfig.getIdentifier());
        }
    }

}
