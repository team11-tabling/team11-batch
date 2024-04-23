package com.tabling_batch.domain.entity;

import com.tabling_batch.domain.util.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "booking")
public class Booking extends Timestamp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long shopId;

  @Column(nullable = false)
  private Long ticketNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingType state;

  @Column(nullable = false)
  private LocalDateTime reservedDatetime;

  @Column(nullable = false)
  private Integer reservedParty;
}
