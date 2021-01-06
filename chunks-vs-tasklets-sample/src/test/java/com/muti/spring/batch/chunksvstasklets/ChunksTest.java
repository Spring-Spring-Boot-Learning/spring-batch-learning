package com.muti.spring.batch.chunksvstasklets;

import com.muti.spring.batch.chunksvstasklets.config.ChunksConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for Chucnk Based Job
 *
 * @author Andrea Muti <muti.andrea@gmail.com>
 * @since 06/01/2021
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ChunksConfig.class)
public class ChunksTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenChunksJob_whenJobEnds_thenStatusCompleted() throws Exception {

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}