package com.muti.spring.batch.conditional.flow.step;

import com.muti.spring.batch.conditional.flow.model.NumberInfo;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 05/01/2021
 */
public class NumberInfoClassifierWithDecider extends ItemListenerSupport<NumberInfo, Integer> implements ItemProcessor<NumberInfo, Integer> {

    @Override
    public Integer process(NumberInfo numberInfo) {
        return Integer.valueOf(numberInfo.getNumber());
    }
}