package com.muti.spring.batch.conditional.flow.step;

import com.muti.spring.batch.conditional.flow.model.NumberInfo;
import org.springframework.batch.item.ItemReader;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 06/01/2021
 */
public class NumberInfoGenerator implements ItemReader<NumberInfo> {

    private int[] values;
    private int counter;

    public NumberInfoGenerator(int[] values) {
        this.values = values;
        counter = 0;
    }

    @Override
    public NumberInfo read() {

        if (counter == values.length) {
            return null;
        }
        else {
            return new NumberInfo(values[counter++]);
        }
    }
}