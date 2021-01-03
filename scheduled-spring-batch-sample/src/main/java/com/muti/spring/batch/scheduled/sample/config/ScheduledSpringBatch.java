package com.muti.spring.batch.scheduled.sample.config;

import com.muti.spring.batch.scheduled.sample.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import javax.sql.DataSource;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class ScheduledSpringBatch {

	private final Logger logger = LoggerFactory.getLogger(ScheduledSpringBatch.class);

	@Value("org/springframework/batch/core/schema-drop-sqlite.sql")
	private Resource dropRepositoryTables;

	@Value("org/springframework/batch/core/schema-sqlite.sql")
	private Resource dataRepositorySchema;

	private final AtomicBoolean enabled = new AtomicBoolean(true);

	private final AtomicInteger batchRunCounter = new AtomicInteger(0);

	/**
	 * Another way to stop the scheduler would be manually canceling its Future.
	 * Here's a custom task scheduler for capturing Future map
	 */
	private final Map<Object, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Scheduled(fixedRate = 2000)
	public void launchJob() throws Exception {

		Date date = new Date();
		logger.debug("scheduler starts at " + date);

		if (enabled.get()) {

			JobExecution jobExecution = jobLauncher().run(
					myScheduledBatchJob(),
					new JobParametersBuilder().addDate("launchDate", date).toJobParameters());

			batchRunCounter.incrementAndGet();
			logger.debug("Batch job ends with status as " + jobExecution.getStatus());
		}

		logger.debug("scheduler ends ");
	}

	public void stop() {

		enabled.set(false);
	}

	public void start() {

		enabled.set(true);
	}

	@Bean
	public TaskScheduler poolScheduler() {

		return new CustomTaskScheduler();
	}

	/**
	 * Then we iterate the Future map and cancel the Future for our batch job scheduler
	 */
	public void cancelFutureSchedulerTasks() {

		scheduledTasks.forEach((k, v) -> {
			if (k instanceof ScheduledSpringBatch) {
				v.cancel(false);
			}
		});
	}

	private class CustomTaskScheduler extends ThreadPoolTaskScheduler {

		private static final long serialVersionUID = -7142624085505040603L;

		@Override
		public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {

			ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);

			ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
			scheduledTasks.put(runnable.getTarget(), future);

			return future;
		}
	}

	@Bean
	public Job myScheduledBatchJob() {

		return jobBuilderFactory.get("myScheduledBatchJob")
				.start(readBooks())
				.build();
	}

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
	protected Step readBooks() {

		return stepBuilderFactory.get("readBooks")
				.<Book, Book> chunk(2)
				.reader(reader())
				.writer(writer())
				.build();
	}

	@Bean
	public FlatFileItemReader<Book> reader() {

		return new FlatFileItemReaderBuilder<Book>().name("bookItemReader")
				.resource(new ClassPathResource("books.csv"))
				.delimited()
				.names("id", "name")
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Book>() {
					{
						setTargetType(Book.class);
					}
				})
				.build();
	}

	@Bean
	public ItemWriter<Book> writer() {

		return items -> {
			logger.debug("# writers to process: {}", items.size());	// this is the chunkSize defined in the Step
			for (Book item : items) {
				logger.debug(" - {}", item.toString());
			}
		};
	}

	public AtomicInteger getBatchRunCounter() {

		return batchRunCounter;
	}
}