package com.muti.spring.batch.sample;

import com.muti.spring.batch.sample.config.SpringBatchConfig;
import com.muti.spring.batch.sample.config.SpringConfig;
import com.muti.spring.batch.sample.parallel.config.SpringBatchParallelConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single-Process, multi-threaded job execution.
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
public class SampleParallelApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleParallelApp.class);

    public static void main(String[] args) {

        // Spring Java config
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringBatchParallelConfig.class);
        context.refresh();

        final JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        final Job job = (Job) context.getBean("partitionerJob");

        LOGGER.info("Starting the parallel batch job");

        try {
            final JobExecution execution = jobLauncher.run(job, new JobParameters());
            LOGGER.info("Job Status : {}", execution.getStatus());
        }
        catch (final Exception e) {
            e.printStackTrace();
            LOGGER.error("Job failed {}", e.getMessage());
        }
    }
}