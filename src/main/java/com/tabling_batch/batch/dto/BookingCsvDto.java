package com.tabling_batch.batch.dto;

import com.tabling_batch.domain.entity.BookingType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingCsvDto {

  private Long userId;
  private Long shopId;
  private Long ticketNumber;
  private BookingType state;
  private LocalDateTime reservedDatetime;
  private Integer reservedParty;
}