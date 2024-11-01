package com.example.concurrency.view_count;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ViewCountRepository extends JpaRepository<ViewCount, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 락을 설정
  @Query("SELECT v FROM ViewCount v WHERE v.id = ?1")
  ViewCount findWithLockById(Long id);
}