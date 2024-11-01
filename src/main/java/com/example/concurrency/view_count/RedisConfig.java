package com.example.concurrency.view_count;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer()); // 키는 문자열로 직렬화
    redisTemplate.setValueSerializer(new StringRedisSerializer()); // 값도 문자열로 직렬화
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }
}