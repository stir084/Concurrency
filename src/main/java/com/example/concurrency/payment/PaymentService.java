package com.example.concurrency.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
  private final RedisTemplate<String, String> redisTemplate;
  private final RedissonClient redissonClient;
  private final PaymentEventRepository paymentEventRepository;

  private static final String SUCCESS_COUNT_KEY = "payment:success:count";
  private static final String PROCESSING_COUNT_KEY = "payment:processing:count";
  private static final String PAYMENT_LOCK_KEY = "payment:lock";
  private static final long MAX_PAYMENT_COUNT = 10000;
  private static final long PAYMENT_AMOUNT = 10;

  @Transactional
  public PaymentResponse processPayment(Long userId) {
    // 1. 처리중 카운터 증가
    Long processingCount = redisTemplate.opsForValue().increment(PROCESSING_COUNT_KEY);
    Long successCount = getLongValue(SUCCESS_COUNT_KEY);

    try {
      // 2. 이미 완료된 수 + 처리중인 수가 제한을 초과하는지 체크
      if ((successCount + processingCount) > MAX_PAYMENT_COUNT) {
        return PaymentResponse.builder()
          .success(false)
          .message("선착순 마감되었습니다.")
          .build();
      }

      // 3. 성공 카운트가 제한에 근접한 경우 (마지막 100건 정도)
      if (successCount >= (MAX_PAYMENT_COUNT - 100)) {
        return processWithLock(userId);
      }

      // 4. 그 외의 경우 일반적인 처리
      return processNormalPayment(userId);

    } finally {
      // 5. 처리중 카운터 감소
      redisTemplate.opsForValue().decrement(PROCESSING_COUNT_KEY);
    }
  }

  private PaymentResponse processWithLock(Long userId) {
    RLock lock = redissonClient.getLock(PAYMENT_LOCK_KEY);
    try {
      // 5초 동안 락 획득 시도, 획득 후 10초 동안 락 유지
      if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
        return PaymentResponse.builder()
          .success(false)
          .message("일시적인 처리 지연, 다시 시도해주세요.")
          .build();
      }

      try {
        // 락 획득 후 다시 한번 체크
        Long currentSuccessCount = getLongValue(SUCCESS_COUNT_KEY);
        if (currentSuccessCount >= MAX_PAYMENT_COUNT) {
          return PaymentResponse.builder()
            .success(false)
            .message("선착순 마감되었습니다.")
            .build();
        }

        return processNormalPayment(userId);

      } finally {
        lock.unlock();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return PaymentResponse.builder()
        .success(false)
        .message("처리 중 오류가 발생했습니다.")
        .build();
    }
  }

  private PaymentResponse processNormalPayment(Long userId) {
    try {
      // 실제 결제 처리
      boolean paymentResult = processActualPayment(userId, PAYMENT_AMOUNT);

      if (!paymentResult) {
        return PaymentResponse.builder()
          .success(false)
          .message("결제 처리 실패")
          .build();
      }

      // 성공 카운트 증가 및 결제 이력 저장
      redisTemplate.opsForValue().increment(SUCCESS_COUNT_KEY);
      PaymentEvent event = PaymentEvent.create(userId);
      paymentEventRepository.save(event);

      return PaymentResponse.builder()
        .success(true)
        .message("결제 성공")
        .build();

    } catch (Exception e) {
      log.error("Payment processing error", e);
      return PaymentResponse.builder()
        .success(false)
        .message("시스템 오류")
        .build();
    }
  }

  private boolean processActualPayment(Long userId, long amount) {
    // 실제 결제 로직 구현
    return true;
  }

  private Long getLongValue(String key) {
    String value = redisTemplate.opsForValue().get(key);
    return value != null ? Long.parseLong(value) : 0L;
  }
}