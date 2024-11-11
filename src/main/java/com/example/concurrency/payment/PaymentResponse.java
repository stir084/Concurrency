package com.example.concurrency.payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
  private boolean success;
  private String message;
}