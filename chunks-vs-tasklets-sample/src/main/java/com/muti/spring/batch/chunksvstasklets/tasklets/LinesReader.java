package com.muti.spring.batch.chunksvstasklets.tasklets;

import com.muti.spring.batch.chunksvstasklets.model.Line;
import com.muti.spring.batch.chunksvstasklets.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * LinesReader Tasklet
 *
 * Our class also implements StepExecutionListener that provides two extra methods: beforeStep and afterStep.
 * We'll use those methods to initialize and close things before and after execute runs.
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 06/01/2021
 */
public class LinesReader implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(LinesReader.class);

    private List<Line> lines;
    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        lines = new ArrayList<>();

        fu = new FileUtils("input/tasklets-vs-chunks.csv");

        logger.debug("Lines Reader initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        Line line = fu.readLine();

        while (line != null) {
            lines.add(line);
            logger.debug("Read line: {}", line.toString());
            line = fu.readLine();
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        fu.closeReader();

        // mettiamo le linee processate nell'execution context per renderle disponibili allo step successivo
        stepExecution.getJobExecution()
                .getExecutionContext()
                .put("lines", this.lines);

        logger.debug("Lines Reader ended.");

        return ExitStatus.COMPLETED;
    }
}