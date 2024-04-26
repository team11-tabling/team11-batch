package com.tabling_batch.batch;

import com.tabling_batch.batch.dto.UserBookingCountDto;
import com.tabling_batch.batch.dto.UserGradeDto;
import com.tabling_batch.batch.tasklet.InitializeUserTasklet;
import com.tabling_batch.domain.entity.User;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class UserGradeBatch {

  private final EntityManager entityManager;
  private final InitializeUserTasklet initializeUserTasklet;
  private final int chunkSize = 100;

  @Bean
  public Job userGradeJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
    return new JobBuilder("userGradeJob", jobRepository)
        .start(initializeUserStep(null, jobRepository, transactionManager))
//        .next(userGradeStep(jobRepository, transactionManager,dataSource))
        .build();
  }

  @Bean
  @JobScope
  public Step initializeUserStep(@Value("#{jobParameters[date]}") String date, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("initializeUserStep", jobRepository)
        .tasklet(initializeUserTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step userGradeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) {
    return new StepBuilder("userGradeStep", jobRepository)
        .<UserBookingCountDto, User>chunk(chunkSize, transactionManager)
        .reader(userGradeItemReader(dataSource))
        .processor(userGradeCompositeProcessor())
        .writer(userGradeItemWriter())
        .build();
  }

  @Bean
  public JdbcCursorItemReader<UserBookingCountDto> userGradeItemReader(DataSource dataSource) {
    return new JdbcCursorItemReaderBuilder<UserBookingCountDto>()
        .name("userGradeReader")
        .dataSource(dataSource)
        .sql("select user_id, count(user_id) as user_booking_count "
            + "from booking "
            + "where state='DONE' "
            + "group by user_id")
        .rowMapper(new BeanPropertyRowMapper<>(UserBookingCountDto.class))
        .build();
  }

  @Bean
  public CompositeItemProcessor<UserBookingCountDto,User> userGradeCompositeProcessor() {
    List<ItemProcessor<?,?>> delegates = new ArrayList<>(2);
    delegates.add(userGradeItemProcessor1());
    delegates.add(userGradeItemProcessor2());

    CompositeItemProcessor<UserBookingCountDto,User> processor = new CompositeItemProcessor<>();

    processor.setDelegates(delegates);

    return processor;
  }

  @Bean
  public ItemProcessor<UserBookingCountDto, UserGradeDto> userGradeItemProcessor1() {
    return item -> {
      String grade;
      if (item.getUserBookingCount() >= 3) {
        grade = "Gold";
      } else if (item.getUserBookingCount() >= 1) {
        grade = "Silver";
      } else {
        grade = "Bronze";
      }
      return new UserGradeDto(item.getUserId(), grade);
    };
  }

  @Bean
  public ItemProcessor<UserGradeDto, User> userGradeItemProcessor2() {
    return item -> {
      User user = entityManager.find(User.class, item.getUserId());

      if (user != null) {
        user.updateGrade(item.getGrade());
      }

      return user;
    };
  }

  @Bean
  public JpaItemWriter<User> userGradeItemWriter() {
    return new JpaItemWriterBuilder<User>()
        .entityManagerFactory(entityManager.getEntityManagerFactory())
        .build();
  }
}

