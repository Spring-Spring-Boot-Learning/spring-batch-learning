package com.muti.spring.batch.sample;

import com.muti.spring.batch.sample.config.SpringBatchConfig;
import com.muti.spring.batch.sample.config.SpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * single-threaded, single process job execution.
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 02/01/2021
 */
public class SampleApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleApp.class);

    public static void main(String[] args) {

        // Spring Java config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringConfig.class);
        context.register(SpringBatchConfig.class);
        context.refresh();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("firstBatchJob");
        LOGGER.info("Starting the batch job");

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