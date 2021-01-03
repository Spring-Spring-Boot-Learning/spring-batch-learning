package com.muti.spring.batch.sample.service;


import com.muti.spring.batch.sample.SampleParallelApp;
import com.muti.spring.batch.sample.model.Transaction;
import com.muti.spring.batch.sample.service.exception.MissingUsernameException;
import com.muti.spring.batch.sample.service.exception.NegativeAmountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
public class SkippingItemProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkippingItemProcessor.class);

    @Override
    public Transaction process(Transaction transaction) {

        LOGGER.debug("Processing {}", transaction);

        if (transaction.getUsername() == null || transaction.getUsername().isEmpty()) {

            LOGGER.warn("Current transaction has missing username!", transaction);
            throw new MissingUsernameException();
        }

        double txAmount = transaction.getAmount();
        if (txAmount < 0) {

            LOGGER.warn("Current transaction has negative amount!", transaction);
            throw new NegativeAmountException(txAmount);
        }

        return transaction;
    }
}