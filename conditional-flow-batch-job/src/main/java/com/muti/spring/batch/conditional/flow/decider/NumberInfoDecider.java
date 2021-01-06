package com.muti.spring.batch.conditional.flow.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 05/01/2021
 */
public class NumberInfoDecider implements JobExecutionDecider {

    public static final String NOTIFY = "NOTIFY";
    public static final String QUIET = "QUIET";

    /**
     * Method that determines notification status of job
     * @return true if notifications should be sent.
     */
    private boolean shouldNotify() {

        // qui torniamo sempre true, ma per esempio potremmo usare questo metodo per controllare
        // delle condizioni esterne che non dipendono strettamente dai dati che si stanno processando
        // (quindi non lo decido nel reader/processor/writer) ad esempio una config su un DB

        return true;
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

        if (shouldNotify()) {
            return new FlowExecutionStatus(NOTIFY);
        }
        else {
            return new FlowExecutionStatus(QUIET);
        }
    }
}