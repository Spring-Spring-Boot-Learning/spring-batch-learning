package com.muti.spring.batch.chunksvstasklets.chunks;

import com.muti.spring.batch.chunksvstasklets.model.Line;
import com.muti.spring.batch.chunksvstasklets.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 06/01/2021
 */
public class LinesWriter implements ItemWriter<Line>, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(LinesWriter.class);

    private FileUtils fu;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        fu = new FileUtils("output.csv");

        logger.debug("Line Writer initialized.");
    }

    @Override
    public void write(List<? extends Line> lines) throws Exception {

        for (Line line : lines) {

            fu.writeLine(line);
            logger.debug("Wrote line " + line.toString());
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        fu.closeWriter();

        logger.debug("Line Writer ended.");

        return ExitStatus.COMPLETED;
    }
}