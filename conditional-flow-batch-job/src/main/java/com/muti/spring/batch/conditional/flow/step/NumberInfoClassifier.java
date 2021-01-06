package com.muti.spring.batch.conditional.flow.step;

import com.muti.spring.batch.conditional.flow.model.NumberInfo;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.ItemProcessor;

import static com.muti.spring.batch.conditional.flow.decider.NumberInfoDecider.NOTIFY;
import static com.muti.spring.batch.conditional.flow.decider.NumberInfoDecider.QUIET;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 05/01/2021
 */
public class NumberInfoClassifier extends ItemListenerSupport<NumberInfo, Integer> implements ItemProcessor<NumberInfo, Integer> {

    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {

        this.stepExecution = stepExecution;
        this.stepExecution.setExitStatus(new ExitStatus(QUIET));
    }

    @Override
    public Integer process(NumberInfo numberInfo) {

        return Integer.valueOf(numberInfo.getNumber());
    }

    @Override
    public void afterProcess(NumberInfo item, Integer result) {

        super.afterProcess(item, result);

        if (item.isPositive()) {
            stepExecution.setExitStatus(new ExitStatus(NOTIFY));
        }
    }
}