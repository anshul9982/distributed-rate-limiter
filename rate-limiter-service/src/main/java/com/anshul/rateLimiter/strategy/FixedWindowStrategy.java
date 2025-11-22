package com.anshul.rateLimiter.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component("fixedWindow")
@RequiredArgsConstructor
public class FixedWindowStrategy implements RateLimiterStrategy {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    @Override
    public Mono<Boolean> isAllowed(String key, int limit, int windowSeconds) {
        return redisTemplate.opsForValue().increment(key)
                .flatMap(count -> {
                    Mono<Long> afterExpire = (count != null && count == 1L)
                            ? redisTemplate.expire(key, Duration.ofSeconds(windowSeconds)).thenReturn(count)
                            : Mono.just(count);
                    return afterExpire;
                })
                .map(count -> count != null && count <= limit);
    }
}
