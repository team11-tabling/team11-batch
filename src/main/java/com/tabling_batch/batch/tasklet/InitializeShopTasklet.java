package com.tabling_batch.batch.tasklet;

import com.tabling_batch.domain.entity.Shop;
import com.tabling_batch.domain.repository.ShopRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InitializeShopTasklet implements Tasklet {

  private final ShopRepository shopRepository;
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    List<Shop> shopList = shopRepository.findAll();

    for (Shop shop : shopList) {
      shop.popularShopUpdate(false);
    }

    return RepeatStatus.FINISHED;
  }
}
