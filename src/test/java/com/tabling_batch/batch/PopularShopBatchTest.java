//package com.tabling_batch.batch;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.test.JobLauncherTestUtils;
//import org.springframework.batch.test.context.SpringBatchTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBatchTest
//@SpringBootTest
//@EnableBatchProcessing
//class PopularShopBatchTest {
//
//  @Autowired
//  private JobLauncherTestUtils jobLauncherTestUtils;
//
//  @Autowired
//  private Job popularShopJob;
//
//  @Test
//  public void testPopularShopJob() throws Exception {
//    // Given
//    JobParameters jobParameters = new JobParametersBuilder()
//        .addString("date", "2024-05-09")
//        .toJobParameters();
//
//    // When
//    jobLauncherTestUtils.setJob(popularShopJob);
//    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
//
//    // Then
//    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
//  }
//}