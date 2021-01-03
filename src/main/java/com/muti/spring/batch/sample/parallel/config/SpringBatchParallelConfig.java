package com.muti.spring.batch.sample.parallel.config;

import com.muti.spring.batch.sample.model.Transaction;
import com.muti.spring.batch.sample.parallel.partitioner.CustomMultiResourcePartitioner;
import com.muti.spring.batch.sample.service.RecordFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
@Configuration
@EnableBatchProcessing
public class SpringBatchParallelConfig {

    private static final String RESOURCE_PATH_PATTERN = "file:src/main/resources/input/partitioner/*.csv";

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    // --- The JOB ---
    @Bean(name = "partitionerJob")
    public Job partitionerJob() throws UnexpectedInputException {

        return jobs.get("partitioningJob")
                .start(partitionStep())
                .build();
    }

    // --- The PARTITIONING STEP (i.e. the MASTER) ---
    @Bean
    public Step partitionStep() throws UnexpectedInputException {

        return steps.get("partitionStep")                            // get a Partitioning Step
                .partitioner("slaveStep", partitioner())    // that used the provided partitioner
                .step(slaveStep())                                   // to distribute the workload among a set of slaveStep
                .taskExecutor(taskExecutor())                        // with parallelism defined by the provided task executor
                .build();
    }

    // --- The PARTITIONER ---
    @Bean
    public CustomMultiResourcePartitioner partitioner() {

        Resource[] resources;

        try {
            resources = resourcePatternResolver.getResources(RESOURCE_PATH_PATTERN);
        }
        catch (IOException e) {
            throw new RuntimeException("I/O problems when resolving the input file pattern.", e);
        }

        CustomMultiResourcePartitioner partitioner = new CustomMultiResourcePartitioner();
        partitioner.setResources(resources);

        return partitioner;
    }

    // --- The SLAVE STEP ---
    @Bean
    public Step slaveStep() throws UnexpectedInputException {

        return steps.get("slaveStep").<Transaction, Transaction>chunk(1)
                .reader(itemReader(null))
                .writer(itemWriter(marshaller(), null))
                .build();
    }

    // --- The ITEM READER [csv file line -> Transaction Object] ---
    @StepScope
    @Bean
    public FlatFileItemReader<Transaction> itemReader(@Value("#{stepExecutionContext[fileName]}") String filename) throws UnexpectedInputException {

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        String[] tokens = {"username", "userid", "transactiondate", "amount"};
        tokenizer.setNames(tokens);

        DefaultLineMapper<Transaction> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new RecordFieldSetMapper());

        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("input/partitioner/" + filename));
        reader.setLinesToSkip(1);           // skip the first line as it contains csv headers
        reader.setLineMapper(lineMapper);

        return reader;
    }

    // --- The ITEM WRITER [Transaction Object -> XML Element in output file (using marshaller)] ---
    @StepScope
    @Bean(destroyMethod="")
    public StaxEventItemWriter<Transaction> itemWriter(Marshaller marshaller, @Value("#{stepExecutionContext[opFileName]}") String filename) {

        FileSystemResource resource = new FileSystemResource("xml/partitioner/" + filename);

        StaxEventItemWriter<Transaction> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setMarshaller(marshaller);
        itemWriter.setRootTagName("transactionRecord");
        itemWriter.setResource(resource);

        return itemWriter;
    }

    // --- Marshaller for Transaction Object to XML transformation ---
    @Bean
    public Marshaller marshaller() {

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Transaction.class);

        return marshaller;
    }

    // --- The "parallelism" configuration ---
    // A Thread Pool of 5 threads
    @Bean
    public TaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setQueueCapacity(5);
        taskExecutor.afterPropertiesSet();

        return taskExecutor;
    }
}