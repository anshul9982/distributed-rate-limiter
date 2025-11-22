package com.anshul.rateLimiter.strategy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Component("tokenBucket")
public class TokenBucketStrategy implements RateLimiterStrategy {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisScript<List> script;

    public TokenBucketStrategy(ReactiveRedisTemplate<String, String> redisTemplate,@Qualifier("tokenBucketScript") RedisScript<List> script) {
        this.redisTemplate = redisTemplate;
        this.script = script;
    }
    @Override
    public Mono<Boolean> isAllowed(String key, int limit, int windowSeconds){

        double refillRate = (double) limit/windowSeconds;
        long now = Instant.now().getEpochSecond();
        int requestedTokens = 1;

        return redisTemplate.execute(script, List.of(key), List.of(String.valueOf(limit),String.valueOf(refillRate),String.valueOf(now), String.valueOf(requestedTokens))).next()
                .map(result->{
                    Long allowed = (Long) result.get(0);
                    return allowed == 1;
                });
    }
}
