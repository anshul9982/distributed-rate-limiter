package com.anshul.rateLimiter.dto;

import lombok.Data;

@Data
public class CreateRateLimitDTO {
    private String clientKey;
    private Integer limit;
    private Integer windowSeconds;
}
