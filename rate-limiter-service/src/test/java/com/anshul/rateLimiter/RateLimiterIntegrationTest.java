package com.anshul.rateLimiter;

import com.anshul.rateLimiter.config.RedisConfig;
import com.anshul.rateLimiter.entity.ClientRateLimitConfig;
import com.anshul.rateLimiter.service.RateLimitConfigService;
import com.anshul.rateLimiter.service.RateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest
public class RateLimiterIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @MockitoBean
    private RateLimitConfigService rateLimitConfigService;

    private static final String TEST_KEY = "test-client";

    @BeforeEach
    void setupMockConfig() {
        ClientRateLimitConfig mockConfig = ClientRateLimitConfig.builder()
                .clientKey(TEST_KEY)
                .limit(5)
                .windowSeconds(60)
                .build();
        when(rateLimitConfigService.getConfig(TEST_KEY)).thenReturn(mockConfig);
    }

    @Test
    void testRateLimiterBlockRequestsInBurst(){
        // 1. Send a burst of 5 requests
        Flux<Boolean> allowedRequests = Flux.range(1,5)
                .flatMap(i -> rateLimiterService.isAllowed(TEST_KEY));

        // Verify that all 5 requests are ALLOWED (true)
        StepVerifier.create(allowedRequests)
                .expectNextCount(5)
                .verifyComplete();

        // 2. Send the 6th request (should be BLOCKED)
        StepVerifier.create(rateLimiterService.isAllowed(TEST_KEY))
                .expectNext(false) // Expect 'false' (Blocked)
                .verifyComplete();

        // 3. Send the 7th request (still BLOCKED)
        StepVerifier.create(rateLimiterService.isAllowed(TEST_KEY))
                .expectNext(false) // Expect 'false' (Blocked)
                .verifyComplete();

    }
}
