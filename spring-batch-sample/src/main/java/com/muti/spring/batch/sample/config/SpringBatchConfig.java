package com.muti.spring.batch.sample.config;

import com.muti.spring.batch.sample.model.Transaction;
import com.muti.spring.batch.sample.service.CustomItemProcessor;
import com.muti.spring.batch.sample.service.CustomSkipPolicy;
import com.muti.spring.batch.sample.service.RecordFieldSetMapper;
import com.muti.spring.batch.sample.service.SkippingItemProcessor;
import com.muti.spring.batch.sample.service.exception.MissingUsernameException;
import com.muti.spring.batch.sample.service.exception.NegativeAmountException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.text.ParseException;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 02/01/2021
 */
public class SpringBatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("input/record.csv")
    private Resource inputCsv;

    @Value("input/recordWithInvalidData.csv")
    private Resource invalidInputCsv;

    @Value("file:xml/output.xml")
    private Resource outputXml;

    @Bean
    public ItemReader<Transaction> itemReader() throws UnexpectedInputException {

        return itemReader(inputCsv);
    }

    @Bean
    public ItemReader<Transaction> itemReader(Resource resource) throws UnexpectedInputException {

        String[] tokens = { "username", "userid", "transactiondate", "amount" };

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(tokens);

        DefaultLineMapper<Transaction> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());

        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
        reader.setResource(resource);
        reader.setLinesToSkip(1);           // skip the first line as it contains csv headers
        reader.setLineMapper(lineMapper);

        return reader;
    }

    @Bean
    public ItemProcessor<Transaction, Transaction> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Transaction> itemWriter(Marshaller marshaller) {

        StaxEventItemWriter<Transaction> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setMarshaller(marshaller);
        itemWriter.setRootTagName("transactionRecord");
        itemWriter.setResource(outputXml);

        return itemWriter;
    }

    @Bean
    public Marshaller marshaller() {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Transaction.class);

        return marshaller;
    }

    @Bean
    protected Step step1(ItemReader<Transaction> reader,
                         @Qualifier("itemProcessor") ItemProcessor<Transaction, Transaction> processor,
                         ItemWriter<Transaction> writer) {

        return steps.get("step1")
                .<Transaction, Transaction> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean(name = "firstBatchJob")
    public Job job(@Qualifier("step1") Step step1) {

        return jobs.get("firstBatchJob").start(step1).build();
    }

    // ----- all beans related to Skip Logic example ----

    // 1. Skipping Job - using a Step that explicitly defines skipping logic

    @Bean(name = "skippingBatchJob")
    public Job skippingBatchJob(@Qualifier("skippingStep") Step skippingStep) {

        return jobs.get("skippingBatchJob")
                .start(skippingStep)
                .build();
    }

    @Bean
    public Step skippingStep(@Qualifier("skippingItemProcessor") ItemProcessor<Transaction, Transaction> processor,
                             ItemWriter<Transaction> writer) {

        return steps.get("skippingStep")
                .<Transaction, Transaction>chunk(10)
                .reader(itemReader(invalidInputCsv))
                .processor(processor)
                .writer(writer)
                .faultTolerant()                        // questo abilita la Skip Logic
                .skipLimit(2)                           // numero massimo di eccezioni che è consentito saltare
                .skip(MissingUsernameException.class)   // eccezioni che sono consentite
                .skip(NegativeAmountException.class)
                .build();
    }

    // 2. Skip Policy Job - using a Step that relies on a custom Skipping Policy

    @Bean(name = "skipPolicyBatchJob")
    public Job skipPolicyBatchJob(@Qualifier("skipPolicyStep") Step skipPolicyStep) {

        return jobs.get("skipPolicyBatchJob")
                .start(skipPolicyStep)
                .build();
    }

    @Bean
    public Step skipPolicyStep(@Qualifier("skippingItemProcessor") ItemProcessor<Transaction, Transaction> processor,
                               ItemWriter<Transaction> writer) {

        return steps.get("skipPolicyStep")
                .<Transaction, Transaction>chunk(10)
                .reader(itemReader(invalidInputCsv))
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipPolicy(new CustomSkipPolicy())
                .build();
    }

    @Bean
    public ItemProcessor<Transaction, Transaction> skippingItemProcessor() {
        return new SkippingItemProcessor();
    }
}