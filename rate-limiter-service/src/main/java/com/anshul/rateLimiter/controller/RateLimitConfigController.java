package com.anshul.rateLimiter.controller;

import com.anshul.rateLimiter.dto.CreateRateLimitDTO;
import com.anshul.rateLimiter.entity.ClientRateLimitConfig;
import com.anshul.rateLimiter.service.RateLimitConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ratelimit")
@RequiredArgsConstructor
public class RateLimitConfigController {
    private final RateLimitConfigService service;

    @PostMapping("/config")
    public Mono<ResponseEntity<ClientRateLimitConfig>> createConfig(@RequestBody CreateRateLimitDTO dto){
        return Mono.fromCallable(()-> service.saveOrUpdate(dto))
                .map(ResponseEntity::ok);
    }

}
