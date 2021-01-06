package com.muti.spring.batch.conditional.flow.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 05/01/2021
 */
public class NotifierTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierTasklet.class);

    // uno step basato su Tasklet esegue il metodo execute ripetutamente finchè
    // non viene ritornato lo status FINISHED

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        LOGGER.info("[{}] contains interesting data!!", chunkContext.getStepContext().getJobName());

        return RepeatStatus.FINISHED;
    }
}