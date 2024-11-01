package com.example.concurrency.view_count;

import jakarta.persistence.*;

@Entity
public class ViewCount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long count = 0L;

  @Version
  private Integer version; // 낙관적 잠금을 위한 필드 추가

  // Getters and setters
  public Long getId() {
    return id;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public void incrementCount() {
    this.count++;
  }
}