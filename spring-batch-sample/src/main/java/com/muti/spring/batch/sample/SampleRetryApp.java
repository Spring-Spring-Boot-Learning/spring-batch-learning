package com.muti.spring.batch.sample;

import com.muti.spring.batch.sample.config.SpringBatchRetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Starts the Job with configured Retry Mechanism
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
public class SampleRetryApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleRetryApp.class);

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringBatchRetryConfig.class);
        context.refresh();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("retryBatchJob");
        LOGGER.info("Starting the retry batch job");

        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            LOGGER.info("Job Status : {}", execution.getStatus());
            LOGGER.info("Job completed");
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Job failed");
        }
    }
}