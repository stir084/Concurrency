package com.example.concurrency;

public class VolatileTest {
  boolean running = true;

  public static void main(String[] args) {
    new VolatileTest().test();
  }

  public void test() {
    new Thread(() -> {
      int count = 0;
      while (running) { // CPU Cache에서 꺼내므로 아래의 Thread가 값을 바꾸더라도 무한 반복에 빠진다.
        count++;
      }
      System.out.println("Thread 1 finished. Counted up to " + count);
    }
    ).start();

    new Thread(() -> {
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
      }
      System.out.println("Thread 2 finishing");
      running = false;
    }
    ).start();
  }
}