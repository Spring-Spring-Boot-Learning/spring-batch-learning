package com.muti.spring.batch.scheduled.sample;

import com.muti.spring.batch.scheduled.sample.config.ScheduledSpringBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
public class SampleScheduledBatchApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleScheduledBatchApp.class);

    public static void main(String[] args) {

        // Spring Java config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ScheduledSpringBatch.class);
        context.refresh();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("myScheduledBatchJob");
        LOGGER.info("Starting the scheduled batch job");

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