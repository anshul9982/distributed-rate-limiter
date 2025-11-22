package com.anshul.rateLimiter.repository;

import com.anshul.rateLimiter.entity.ClientRateLimitConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ClientRateLimitConfigRepository extends JpaRepository<ClientRateLimitConfig, Long> {

    Optional<ClientRateLimitConfig> findByClientKey(String clientKey);
}
