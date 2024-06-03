package com.core.back9.batch.scheduler;

import com.core.back9.batch.job.BatchConfig;
import com.core.back9.batch.job.BatchConfigFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
public class ManagementScoreBatchScheduler extends BatchScheduler {

    public ManagementScoreBatchScheduler(JobLauncher jobLauncher, JobRegistry jobRegistry, BatchConfigFactory batchConfigFactory) {
        super(jobLauncher, jobRegistry, batchConfigFactory);

        BatchConfig batchConfig = batchConfigFactory.getBatchConfig("managementScoreBatchConfig");

        if (batchConfig.getBatchProperty().isJobEnabled()) {
            startScheduler(batchConfig.getIdentifier());
        }
    }

}
