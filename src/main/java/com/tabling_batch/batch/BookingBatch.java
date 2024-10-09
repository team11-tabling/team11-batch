package com.tabling_batch.batch;

import com.tabling_batch.batch.dto.BookingCsvDto;
import com.tabling_batch.batch.tasklet.DeleteBookingTasklet;
import com.tabling_batch.domain.entity.Booking;
import com.tabling_batch.domain.entity.BookingType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BookingBatch {

  private final DataSource dataSource;
  private final DeleteBookingTasklet deleteBookingTasklet;

  private final int chunkSize = 1000;

  @Bean
  public Job bookingJob(JobRepository jobRepository,PlatformTransactionManager transactionManager) {
    return new JobBuilder("bookingJob", jobRepository)
        .start(bookingStep(null, jobRepository, transactionManager))
        .next(deleteBookingStep(jobRepository,transactionManager))
        .build();
  }

  @Bean
  @JobScope
  public Step bookingStep(@Value("#{jobParameters[date]}") String date, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("bookingStep", jobRepository)
        .<Booking, BookingCsvDto>chunk(chunkSize, transactionManager)
        .reader(bookingItemReader())
        .processor(bookingItemProcessor())
        .writer(bookingItemWriter())
        .build();
  }

  @Bean
  public Step deleteBookingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("deleteBookingStep", jobRepository)
        .tasklet(deleteBookingTasklet, transactionManager)
        .build();
  }

  @Bean
  public JdbcPagingItemReader<Booking> bookingItemReader() {
    MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
    queryProvider.setSelectClause("SELECT *");
    queryProvider.setFromClause("FROM booking");

    Map<String, Order> sortKeys = new HashMap<>();
    sortKeys.put("id", Order.ASCENDING);
    queryProvider.setSortKeys(sortKeys);

    return new JdbcPagingItemReaderBuilder<Booking>()
        .name("bookingItemReader")
        .dataSource(dataSource)
        .pageSize(chunkSize)
        .queryProvider(queryProvider)
        .rowMapper((rs, rowNum) -> new Booking(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("shop_id"),
            rs.getLong("ticket_number"),
            BookingType.valueOf(rs.getString("state")),
            rs.getTimestamp("reserved_datetime").toLocalDateTime(),
            rs.getInt("reserved_party")
        ))
        .build();
  }

  @Bean
  public ItemProcessor<Booking, BookingCsvDto> bookingItemProcessor() {
    return Booking -> new BookingCsvDto(Booking.getUserId(),Booking.getShopId(),Booking.getTicketNumber(),Booking.getState(),Booking.getReservedDatetime(),Booking.getReservedParty());
  }

  @Bean
  public FlatFileItemWriter<BookingCsvDto> bookingItemWriter() {
    BeanWrapperFieldExtractor<BookingCsvDto> beanWrapperFieldExtractor = new BeanWrapperFieldExtractor<>();
    beanWrapperFieldExtractor.setNames(new String[]{"userId","shopId","ticketNumber","state", "reservedDatetime","reservedParty"});

    DelimitedLineAggregator<BookingCsvDto> delimitedLineAggregator = new DelimitedLineAggregator<>();
    delimitedLineAggregator.setDelimiter(",");
    delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);

    return new FlatFileItemWriterBuilder<BookingCsvDto>()
        .name("bookingItemWriter")
        .append(true)
        .resource(new FileSystemResource("src/main/resources/static/booking_output.csv"))
        .lineAggregator(delimitedLineAggregator)
        .headerCallback(writer -> writer.write("userId, shopId, ticketNumber, state, reservedDatetime, reservedParty"))
        .footerCallback(writer -> writer.write(LocalDate.now() + "----------\n"))
        .build();
  }
}
