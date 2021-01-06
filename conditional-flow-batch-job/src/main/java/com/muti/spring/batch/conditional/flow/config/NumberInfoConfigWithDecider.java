package com.muti.spring.batch.conditional.flow.config;

import com.muti.spring.batch.conditional.flow.decider.NumberInfoDecider;
import com.muti.spring.batch.conditional.flow.model.NumberInfo;
import com.muti.spring.batch.conditional.flow.step.NumberInfoClassifierWithDecider;
import com.muti.spring.batch.conditional.flow.step.NumberInfoGenerator;
import com.muti.spring.batch.conditional.flow.step.PrependingStdoutWriter;
import com.muti.spring.batch.conditional.flow.tasklet.NotifierTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

import static com.muti.spring.batch.conditional.flow.decider.NumberInfoDecider.NOTIFY;

/**
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 05/01/2021
 */
@Configuration
@EnableBatchProcessing
public class NumberInfoConfigWithDecider {

    @Value("org/springframework/batch/core/schema-drop-sqlite.sql")
    private Resource dropRepositoryTables;

    @Value("org/springframework/batch/core/schema-sqlite.sql")
    private Resource dataRepositorySchema;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public JobLauncher jobLauncher() throws Exception {

        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        jobLauncher.afterPropertiesSet();

        return jobLauncher;
    }

    @Bean
    public JobRepository jobRepository() throws Exception {

        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource());
        factory.setTransactionManager(new ResourcelessTransactionManager());

        return factory.getObject();
    }

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:repository.sqlite");

        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {

        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(dropRepositoryTables);
        databasePopulator.addScript(dataRepositorySchema);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }

    @Bean
    @Qualifier("NotificationStep")
    public Step notificationStep() {

        return stepBuilderFactory.get("Notify step")
                .tasklet(new NotifierTasklet())
                .build();
    }

    public Step numberGeneratorStepDecider(int[] values, String prepend) {

        return stepBuilderFactory.get("Number generator")
                .<NumberInfo, Integer> chunk(1)
                .reader(new NumberInfoGenerator(values))
                .processor(new NumberInfoClassifierWithDecider())
                .writer(new PrependingStdoutWriter<>(prepend))
                .build();
    }

    @Bean(name = "job_with_decider")
    public Job numberGeneratorNotifierJobWithDecider() {

        int[] billableData = {11, -2, -3 };

        Step dataProviderStep = numberGeneratorStepDecider(billableData, "Dataset Processor");

        return jobBuilderFactory.get("job_with_decider")
                .start(dataProviderStep)
                .next(new NumberInfoDecider())
                    .on(NOTIFY)
                    .to(notificationStep())
                .end()
                .build();
    }
}