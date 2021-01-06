package com.muti.spring.batch.chunksvstasklets.tasklets;

import com.muti.spring.batch.chunksvstasklets.model.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 06/01/2021
 */
public class LinesProcessor implements Tasklet, StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(LinesProcessor.class);

    private List<Line> lines;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();

        this.lines = (List<Line>) executionContext.get("lines");

        logger.debug("Lines Processor initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        for (Line line : lines) {

            long age = ChronoUnit.YEARS.between(line.getDob(), LocalDate.now());
            logger.debug("Calculated age {} for line {}", age, line.toString());
            line.setAge(age);
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        // There's no need to put another result list in the context as modifications
        // happen on the same object that comes from the previous step.

        logger.debug("Lines Processor ended.");
        return ExitStatus.COMPLETED;
    }
}