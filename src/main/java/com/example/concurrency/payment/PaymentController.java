package com.example.concurrency.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("/payments")
  public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
    PaymentResponse response = paymentService.processPayment(request.getUserId());
    return ResponseEntity.ok(response);
  }
}
