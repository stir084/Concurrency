package com.example.concurrency.view_count;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class ViewCountService {

  private final ViewCountRepository viewCountRepository;
  private final RedisTemplate<String, Long> redisTemplate;

  private final StringRedisTemplate stringRedisTemplate;

  public ViewCountService(ViewCountRepository viewCountRepository, RedisTemplate<String, Long> redisTemplate, StringRedisTemplate stringRedisTemplate) {
    this.viewCountRepository = viewCountRepository;
    this.redisTemplate = redisTemplate;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  private static final String VIEW_COUNT_KEY_PREFIX = "viewCount:";

  @Transactional
  public void incrementWithSleep(Long id) throws InterruptedException {
    ViewCount viewCount = viewCountRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("ViewCount not found"));
    Thread.sleep(30000);
    viewCount.incrementCount();
  }

  @Transactional
  public void incrementWithoutSleep(Long id) {
    ViewCount viewCount = viewCountRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("ViewCount not found"));
    viewCount.incrementCount();
  }


  @Transactional
  public void incrementWithSleepAndLock(Long id) throws InterruptedException {
    ViewCount viewCount = viewCountRepository.findWithLockById(id); // 비관적 락으로 데이터 조회
    Thread.sleep(30000);
    viewCount.incrementCount(); // 조회수 증가
  }
  @Transactional
  public void incrementWithoutSleepAndLock(Long id) {
    ViewCount viewCount = viewCountRepository.findWithLockById(id); // 비관적 락으로 데이터 조회
    viewCount.incrementCount(); // 조회수 증가
  }

  public void incrementUsingRedisWithSleep(Long id) throws InterruptedException {
    String key = VIEW_COUNT_KEY_PREFIX + id;
    Long currentValue = redisTemplate.opsForValue().get(key); // 값을 가져와서
    // currentValue가 null이면 0으로 초기화
    if (currentValue == null) {
      currentValue = 0L;
    }
    Thread.sleep(30000); // 지연 발생
    redisTemplate.opsForValue().set(key, currentValue + 1); // 직접 증분한 값을 저장
  }

  public void incrementUsingRedisWithoutSleep(Long id) {
    String key = VIEW_COUNT_KEY_PREFIX + id;
    redisTemplate.opsForValue().increment(key);
  }

  public void incrementUsingRedisWithLock(Long id) throws InterruptedException {
    String key = VIEW_COUNT_KEY_PREFIX + id;
    String lockKey = key + ":lock";

    // 프로비저닝 요청하면 알림
    // 상품 쪽


    // 분산 락 설정
    Boolean lockAcquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "lock", Duration.ofSeconds(35));
    if (lockAcquired != null && lockAcquired) {
      try {
        Long currentValue = stringRedisTemplate.opsForValue().get(key) != null ?
          Long.parseLong(stringRedisTemplate.opsForValue().get(key)) : 0L;
        Thread.sleep(30000); // 지연 발생
        stringRedisTemplate.opsForValue().set(key, String.valueOf(currentValue + 1));
      } finally {
        // 락 해제
        stringRedisTemplate.delete(lockKey);
      }
    } else {
      System.out.println("Unable to acquire lock, another process is handling the increment.");
    }
  }
}
