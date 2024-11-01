package com.example.concurrency.view_count;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViewCountService {

  private final ViewCountRepository viewCountRepository;
  private final RedisTemplate<String, Long> redisTemplate;

  public ViewCountService(ViewCountRepository viewCountRepository, RedisTemplate<String, Long> redisTemplate) {
    this.viewCountRepository = viewCountRepository;
    this.redisTemplate = redisTemplate;
  }

  private static final String VIEW_COUNT_KEY_PREFIX = "viewCount:";
  /**
   * Repeatable Read 격리 수준에서 두 트랜잭션 간 수정 충돌이 발생하면, 대부분의 데이터베이스는 충돌을 감지하여 해당 트랜잭션을 실패시키고 롤백하는 방식을 택합니다.
   */
  @Transactional
  public void incrementSleep(Long id) throws InterruptedException {
    ViewCount viewCount = viewCountRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("ViewCount not found"));
    Thread.sleep(30000);
    viewCount.incrementCount();
  }

  @Transactional
  public void incrementViewCount(Long id) {
    ViewCount viewCount = viewCountRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("ViewCount not found"));
    viewCount.incrementCount();
  }

  /**
   * 조회수와 같이 여러명이 동시에 쓰이는 곳은 비관적 락을 사용한다.
   * 위의 코드와 다르게 두번째 메소드를 조회시 첫번째 메소드를 아직 실행중(30초)이니 조회가 되지 않는다.
   * 그래서 정확한 데이터 유지 가능
   */
  @Transactional
  public void incrementViewCountWithLockSleep(Long id) throws InterruptedException {
    ViewCount viewCount = viewCountRepository.findWithLockById(id); // 비관적 락으로 데이터 조회
    Thread.sleep(30000);
    viewCount.incrementCount(); // 조회수 증가
  }
  @Transactional
  public void incrementViewCountWithLock(Long id) {
    ViewCount viewCount = viewCountRepository.findWithLockById(id); // 비관적 락으로 데이터 조회
    viewCount.incrementCount(); // 조회수 증가
  }

  /**
   * redis
   */

  @Transactional
  public void incrementViewCountByRedis(Long id) {
    String key = VIEW_COUNT_KEY_PREFIX + id;
    Long currentValue = redisTemplate.opsForValue().get(key);

    if (currentValue == null) {
      // 키가 없으면 초기값을 1로 설정
      redisTemplate.opsForValue().set(key, 1L);
    } else {
      // 값이 있으면 현재 값에 1을 더하여 저장
      redisTemplate.opsForValue().increment(key);
    }
  }

  @Transactional
  public void incrementViewCountByRedis2(Long id) {
    String key = VIEW_COUNT_KEY_PREFIX + id;
    redisTemplate.opsForValue().increment(key); // Redis에서 조회수 증가
  }

  @Transactional
  public Long getViewCount(Long id) {
    String key = VIEW_COUNT_KEY_PREFIX + id;
    String value = String.valueOf(redisTemplate.opsForValue().get(key)); // Redis에서 조회수 조회
    return (value != null) ? Long.valueOf(value) : 0; // null이면 0 반환
  }
}
