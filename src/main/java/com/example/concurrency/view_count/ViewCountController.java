package com.example.concurrency.view_count;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ViewCountController {

  private final ViewCountService viewCountService;

  public ViewCountController(ViewCountService viewCountService) {
    this.viewCountService = viewCountService;
  }

  /**
   * Repeatable Read 격리 수준에서 두 트랜잭션 간 수정 충돌이 발생하면
   * 대부분의 데이터베이스는 충돌을 감지하여 해당 트랜잭션을 실패시키고 롤백하는 방식을 택한다.
   *
   * 1. With Sleep API 를 실행해서 30초 뒤에 증감한다.
   * 2. 30초 기다리는 동안 Without Sleep API를 실행해서 조회수를 증감시킨다.
   * 3. With Sleep API는 실패한다.
   */
  @GetMapping("/increment-with-sleep")
  public String incrementWithSleep(@RequestParam Long id) throws InterruptedException {
    viewCountService.incrementWithSleep(id);
    return "";
  }

  @GetMapping("/increment-without-sleep")
  public String incrementWithoutSleep(@RequestParam Long id) {
    viewCountService.incrementWithoutSleep(id);
    return "";
  }

  /**
   * 조회수와 같이 여러명이 동시에 쓰이는 곳은 비관적 락을 사용한다.
   * 위의 코드와 다르게 두번째 메소드를 조회시 첫번째 메소드를 아직 실행중(30초)이니 조회가 되지 않는다.
   * 그래서 정확한 데이터 유지 가능
   */
  @GetMapping("/increment-with-sleep-lock")
  public String incrementWithSleepAndLock(@RequestParam Long id) throws InterruptedException {
    viewCountService.incrementWithSleepAndLock(id);
    return "";
  }

  @GetMapping("/increment-without-sleep-lock")
  public String incrementWithoutSleepAndLock(@RequestParam Long id) {
    viewCountService.incrementWithoutSleepAndLock(id);
    return "";
  }


  /**
   * redis에서는 INCR를 사용하면 조회수 증감은 잘 처리할 수 있다.
   * 다만, 복잡한 로직(재고) 처리에서 RDBMS에서의 Repeatable Read 현상과 달리
   * 롤백되지 않고 데이터가 올바르게 수정된다.
   */
  @GetMapping("/increment-using-redis-with-sleep")
  public String incrementUsingRedisWithSleep(@RequestParam Long id) throws InterruptedException {
    viewCountService.incrementUsingRedisWithSleep(id);
    return "";
  }

  @GetMapping("/increment-using-redis-without-sleep")
  public String incrementUsingRedisWithoutSleep(@RequestParam Long id) {
    viewCountService.incrementUsingRedisWithoutSleep(id);
    return "";
  }

  @GetMapping("/increment-using-lock")
  public String incrementUsingLock(@RequestParam Long id) throws InterruptedException {
    viewCountService.incrementUsingLock(id);
    return "";
  }
}