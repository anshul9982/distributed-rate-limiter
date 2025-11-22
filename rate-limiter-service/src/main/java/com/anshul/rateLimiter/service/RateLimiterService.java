package com.anshul.rateLimiter.service;

import com.anshul.rateLimiter.strategy.RateLimiterStrategy;
import org.slf4j.Logger;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RateLimiterService {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    private final RateLimiterStrategy strategy;
    private final RateLimitConfigService config;



    public RateLimiterService(@Qualifier("tokenBucket") RateLimiterStrategy strategy, RateLimitConfigService config){
        this.strategy = strategy;
        this.config = config;
    }

    @CircuitBreaker(name = "redisLimitBreaker", fallbackMethod = "fallbackAllowRequest")
    public Mono<Boolean> isAllowed(String clientKey) {
        return Mono.fromCallable(() -> config.getConfig(clientKey))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(config -> {
                    String redisKey = "rate_limit:" + clientKey;
                    return strategy.isAllowed(redisKey, config.getLimit(), config.getWindowSeconds());
                });

    }

    // FALLBACK METHOD ("Fail Open")
    // This is called when Redis is down OR the Circuit is Open
    public Mono<Boolean> fallbackAllowRequest(String clientKey, Throwable t) {
        logger.error("Redis unavailable/Circuit Open for client: {}. allowing request. Error: {}", clientKey, t.getMessage());
        // Fail Open strategy: Return TRUE (Allowed)
        return Mono.just(true);
    }
}
