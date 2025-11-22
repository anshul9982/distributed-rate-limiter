package com.anshul.rateLimiter.aspect;

import com.anshul.rateLimiter.annotation.RateLimit;
import com.anshul.rateLimiter.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimiterService rateLimiterService;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable{
        String key = parseKey(rateLimit.key(), joinPoint);

        return rateLimiterService.isAllowed(key)
                .flatMap(isAllowed->{
                    if(isAllowed){
                        try {
                            return (Mono<?>)joinPoint.proceed();
                        } catch (Throwable e) {
                            return Mono.error(e);
                        }
                    }else{
                        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded"));
                    }
                });

    }

    private String parseKey(String spel, ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        EvaluationContext context = new StandardEvaluationContext();

        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();

        for(int i=0;i<args.length;i++){
            context.setVariable(paramNames[i], args[i]);
        }
        return parser.parseExpression(spel).getValue(context, String.class);
    }
}
