package com.muti.spring.batch.sample.service;

import com.muti.spring.batch.sample.model.Transaction;
import org.springframework.batch.item.ItemProcessor;

/**
 * Very basic processor: do-nothing
 * It passes the original object coming from reader to the writer:
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 02/01/2021
 */
public class CustomItemProcessor implements ItemProcessor<Transaction, Transaction> {

    public Transaction process(Transaction item) {
        return item;
    }
}