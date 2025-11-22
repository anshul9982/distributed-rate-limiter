package com.anshul.rateLimiter.service;

import com.anshul.rateLimiter.dto.CreateRateLimitDTO;
import com.anshul.rateLimiter.entity.ClientRateLimitConfig;
import com.anshul.rateLimiter.repository.ClientRateLimitConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitConfigService {
    private final ClientRateLimitConfigRepository repository;

    public ClientRateLimitConfig saveOrUpdate(CreateRateLimitDTO dto){
        return repository.findByClientKey(dto.getClientKey()).map(existing -> {
            existing.setLimit(dto.getLimit());
            existing.setWindowSeconds(dto.getWindowSeconds());
            return repository.save(existing);
        })
                .orElseGet(()-> repository.save(ClientRateLimitConfig.builder().clientKey(dto.getClientKey()).limit(dto.getLimit()).windowSeconds(dto.getWindowSeconds()).build()));
    }
    public ClientRateLimitConfig getConfig(String clientKey){
        return repository.findByClientKey(clientKey).orElseGet(()->{
            return ClientRateLimitConfig.builder().clientKey(clientKey).limit(10).windowSeconds(60).build();
        });
    }
}
