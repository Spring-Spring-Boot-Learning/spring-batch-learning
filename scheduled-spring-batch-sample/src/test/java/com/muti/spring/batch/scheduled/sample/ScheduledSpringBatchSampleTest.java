package com.muti.spring.batch.scheduled.sample;

import com.muti.spring.batch.scheduled.sample.config.ScheduledSpringBatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

/**
 * ScheduledSpringBatchSampleTest
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 03/01/2021
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduledSpringBatch.class)
public class ScheduledSpringBatchSampleTest {

    @Autowired
    private ApplicationContext context;


    @Test
    public void stopJobsWhenSchedulerDisabled() {

        ScheduledSpringBatch schedulerBean = context.getBean(ScheduledSpringBatch.class);

        await().untilAsserted(() -> Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get()));

        schedulerBean.stop();

        await().atLeast(3, SECONDS);

        Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get());
    }

    /**
     * Since we're scheduling a method by using @Scheduled annotation,
     * a bean post processor ScheduledAnnotationBeanPostProcessor would've been registered first.
     *
     * We can explicitly call the postProcessBeforeDestruction() to destroy the given scheduled bean
     */
    @Test
    public void stopJobSchedulerWhenSchedulerDestroyed() {

        ScheduledAnnotationBeanPostProcessor bean = context.getBean(ScheduledAnnotationBeanPostProcessor.class);
        ScheduledSpringBatch schedulerBean = context.getBean(ScheduledSpringBatch.class);

        await().untilAsserted(() -> Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get()));

        bean.postProcessBeforeDestruction(schedulerBean, "ScheduledSpringBatch");

        await().atLeast(3, SECONDS);

        Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get());
    }

    /**
     * Another way to stop the scheduler would be manually canceling its Future.
     *
     * Here's a custom task scheduler for capturing Future map
     */
    @Test
    public void stopJobSchedulerWhenFutureTasksCancelled() {

        ScheduledSpringBatch schedulerBean = context.getBean(ScheduledSpringBatch.class);

        await().untilAsserted(() -> Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get()));

        schedulerBean.cancelFutureSchedulerTasks();

        await().atLeast(3, SECONDS);

        Assert.assertEquals(2, schedulerBean.getBatchRunCounter().get());
    }
}