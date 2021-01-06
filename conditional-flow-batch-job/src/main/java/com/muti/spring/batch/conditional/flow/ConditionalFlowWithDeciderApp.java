package com.muti.spring.batch.conditional.flow;

import com.muti.spring.batch.conditional.flow.config.NumberInfoConfigWithDecider;
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
public class ConditionalFlowWithDeciderApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalFlowWithDeciderApp.class);

    public static void main( String[] args ) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(NumberInfoConfigWithDecider.class);
        context.refresh();

        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("job_with_decider");
        LOGGER.info("Starting the job_with_decider");

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