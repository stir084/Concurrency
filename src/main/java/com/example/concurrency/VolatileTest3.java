package com.example.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class VolatileTest3 {
  private AtomicInteger count = new AtomicInteger(0);; // volatile로 선언

  public static void main(String[] args) {
    new VolatileTest3().test();
  }

  public void test() {
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 990000; i++) {
        count.incrementAndGet();
      }
      System.out.println("Thread 1 finished.");
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < 990000; i++) {
        count.incrementAndGet();
      }
      System.out.println("Thread 2 finished.");
    });

    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Final count: " + count);
  }
}