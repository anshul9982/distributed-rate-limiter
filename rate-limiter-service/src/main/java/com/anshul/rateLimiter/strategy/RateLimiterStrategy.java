package com.anshul.rateLimiter.strategy;

import com.anshul.rateLimiter.entity.ClientRateLimitConfig;
import reactor.core.publisher.Mono;

public interface RateLimiterStrategy {

    /**
     * Checks if a request is allowed.
     * @param key           The unique ID (User ID)
     * @param limit         Max requests allowed
     * @param windowSeconds Time window in seconds
     * @return true if allowed, false if blocked
     */
    Mono<Boolean> isAllowed(String key, int limit, int windowSeconds);

    }

