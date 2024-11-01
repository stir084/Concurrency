package com.example.concurrency;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class VolatileTestController {

  private volatile int volatileCounter = 0; // volatile 변수
  private int normalCounter = 0; // 일반 변수

  @GetMapping("/testVolatile")
  public String testVolatile() throws InterruptedException {
    Thread threadA = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        volatileCounter++;
        try {
          Thread.sleep(1); // 1ms 지연
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    Thread threadB = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        volatileCounter++;
        try {
          Thread.sleep(1); // 1ms 지연
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    return "Final volatileCounter: " + volatileCounter;
  }

  @GetMapping("/testNormal")
  public String testNormal() throws InterruptedException {
    Thread threadA = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        normalCounter++;
        try {
          Thread.sleep(1); // 1ms 지연
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    Thread threadB = new Thread(() -> {
      for (int i = 0; i < 1000; i++) {
        normalCounter++;
        try {
          Thread.sleep(1); // 1ms 지연
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    return "Final normalCounter: " + normalCounter;
  }
}