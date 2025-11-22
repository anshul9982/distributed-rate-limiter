package com.anshul.rateLimiter.controller;

import com.anshul.rateLimiter.annotation.RateLimit;
import com.anshul.rateLimiter.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final RateLimiterService rateLimiterService;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Rate Limiter Service is running");
    }

    @GetMapping("/test/{key}")
    public Mono<ResponseEntity<String>> testRateLimiter(@PathVariable String key){
        return rateLimiterService.isAllowed(key)
                .map(isAllowed->{
                    if(isAllowed){
                        return ResponseEntity.ok("Request is allowed");
                    }else{
                        return ResponseEntity.status(429).body("Request is not allowed");
                    }
                });
    }

    @GetMapping("/test-annotation")
    @RateLimit(key = "#user")
    public Mono<ResponseEntity<String>> testAnnotation(@RequestParam String user) {
        return Mono.just(ResponseEntity.ok("Request Allowed for " + user));
    }

}
