package com.tabling_batch.batch.tasklet;

import com.tabling_batch.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeleteBookingTasklet implements Tasklet {

  private final BookingRepository bookingRepository;
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    bookingRepository.deleteAll();
    return RepeatStatus.FINISHED;
  }
}
