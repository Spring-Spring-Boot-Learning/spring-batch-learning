package com.muti.spring.batch.sample.service;

import com.muti.spring.batch.sample.model.Transaction;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
public class RetryItemProcessor implements ItemProcessor<Transaction, Transaction> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryItemProcessor.class);

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    @Override
    public Transaction process(Transaction transaction) throws IOException, JSONException {

        LOGGER.info("RetryItemProcessor, attempting to process: {}", transaction);

        HttpResponse response = fetchMoreUserDetails(transaction.getUserId());

        //parse user's age and postCode from response and update transaction
        String result = EntityUtils.toString(response.getEntity());
        JSONObject userObject = new JSONObject(result);
        transaction.setAge(Integer.parseInt(userObject.getString("age")));
        transaction.setPostCode(userObject.getString("postCode"));

        return transaction;
    }

    private HttpResponse fetchMoreUserDetails(int id) throws IOException {

        final HttpGet request = new HttpGet("http://www.baeldung.com:81/user/" + id);
        return closeableHttpClient.execute(request);
    }
}