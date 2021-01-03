package com.muti.spring.batch.sample.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 02/01/2021
 */
@SuppressWarnings("restriction")
@XmlRootElement(name = "transactionRecord")
public class Transaction {

    private String username;
    private int userId;
    private LocalDateTime transactionDate;
    private double amount;

    /* getters and setters for the attributes */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction [username=" + username + ", userId=" + userId
                + ", transactionDate=" + transactionDate + ", amount=" + amount
                + "]";
    }
}