package com.muti.spring.batch.sample.service.exception;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
public class NegativeAmountException extends RuntimeException {

    private double amount;

    public NegativeAmountException(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}