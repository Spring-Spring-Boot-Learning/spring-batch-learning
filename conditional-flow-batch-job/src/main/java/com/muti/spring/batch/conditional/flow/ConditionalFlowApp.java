package com.muti.spring.batch.conditional.flow;

import com.muti.spring.batch.conditional.flow.config.NumberInfoConfig;
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
 * @since 05/01/2021
 */
public class ConditionalFlowApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalFlowApp.class);

    public static void main( String[] args ) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(NumberInfoConfig.class);
        context.refresh();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("first_job");
        LOGGER.info("Starting the first_job");

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