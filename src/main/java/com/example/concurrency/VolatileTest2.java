package com.example.concurrency;

public class VolatileTest2 {
  private volatile int count = 0; // volatile로 선언

  public static void main(String[] args) {
    new VolatileTest2().test();
  }

  public void test() {
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        count++; // 원자성이 보장되지 않음
      }
      System.out.println("Thread 1 finished.");
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < 10000; i++) {
        count++; // 원자성이 보장되지 않음
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