package com.example.concurrency.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;
  private LocalDateTime paidAt;
  private boolean isSuccess;

  public static PaymentEvent create(Long userId) {
    PaymentEvent event = new PaymentEvent();
    event.userId = userId;
    event.paidAt = LocalDateTime.now();
    event.isSuccess = true;
    return event;
  }
}