package com.example.concurrency.view_count;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewCountInitializer {

  @Bean
  public ApplicationRunner initializer(ViewCountRepository repository) {
    return args -> {
      ViewCount viewCount = new ViewCount();
      repository.save(viewCount);
    };
  }
}
