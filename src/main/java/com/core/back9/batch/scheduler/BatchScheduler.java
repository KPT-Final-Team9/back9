package com.core.back9.batch.scheduler;

import com.core.back9.batch.job.BatchConfigFactory;
import com.core.back9.batch.job.BatchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@DependsOn(value = {"batchPropertyConfig"})
@Slf4j
public abstract class BatchScheduler {

    protected ThreadPoolTaskScheduler scheduler;

    private final JobLauncher jobLauncher;

    private final JobRegistry jobRegistry;

    private final BatchConfigFactory batchConfigFactory;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobProcessor = new JobRegistryBeanPostProcessor();
        jobProcessor.setJobRegistry(jobRegistry);
        return jobProcessor;
    }

    protected void startScheduler(String batchConfigIdentifier) {

        BatchConfig batchConfig = batchConfigFactory.getBatchConfig(batchConfigIdentifier);

        log.info("================================================");
        log.info(">>> [{}] START", batchConfig.getBatchProperty().getJobName());
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        log.info("[{}] cron: {}", batchConfig.getBatchProperty().getJobName(), batchConfig.getBatchProperty().getCronExpression());
        log.info("================================================");
        CronTrigger cronTrigger = new CronTrigger(batchConfig.getBatchProperty().getCronExpression());
        scheduler.schedule(runJob(batchConfig), cronTrigger);
    }

    private Runnable runJob(BatchConfig batchConfig) {
        return () -> {
            launch(batchConfig.getBatchProperty().getJobName());
        };
    }

    public void launch(String jobName) {
        String time = LocalDateTime.now().toString();
        try {

            Job job = jobRegistry.getJob(jobName); // 등록한 job 꺼내옴 -> 다를시 NoSuchJobException
            JobParametersBuilder jobParam = new JobParametersBuilder().addString("time", time);
            jobLauncher.run(job, jobParam.toJobParameters());

        } catch (NoSuchJobException |
                 JobInstanceAlreadyCompleteException |
                 JobExecutionAlreadyRunningException |
                 JobParametersInvalidException |
                 JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}
