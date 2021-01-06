package com.muti.spring.batch.conditional.flow.step;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 06/01/2021
 */
public class PrependingStdoutWriter<T> implements ItemWriter<T> {

    private String prependText;

    public PrependingStdoutWriter(String prependText) {
        this.prependText = prependText;
    }

    @Override
    public void write(List<? extends T> list) {
        for (T listItem : list) {
            System.out.println("["+prependText + "] item: " + listItem.toString());
        }
    }
}