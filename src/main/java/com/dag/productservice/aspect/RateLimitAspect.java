package com.dag.productservice.aspect;

import com.dag.productservice.annotation.RateLimited;
import com.dag.productservice.exceptionhandlers.exceptions.RateLimitExceededException;
import com.dag.productservice.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP Aspect for applying rate limiting to methods annotated with @RateLimited
 * Intercepts method calls and applies rate limiting based on the annotation configuration
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    private final RateLimitService rateLimitService;

    public RateLimitAspect(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    /**
     * Around advice for methods annotated with @RateLimited
     * Applies rate limiting before method execution
     */
    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String bucketKey = generateBucketKey(rateLimited);

        logger.debug("Applying rate limiting for key: {} with strategy: {}", bucketKey, rateLimited.value());

        Bucket bucket = resolveBucket(bucketKey, rateLimited);

        if (!rateLimitService.tryConsume(bucket, rateLimited.tokens())) {
            logger.warn("Rate limit exceeded for key: {}", bucketKey);
            throw new RateLimitExceededException(rateLimited.message());
        }

        logger.debug("Rate limit check passed for key: {}", bucketKey);
        return joinPoint.proceed();
    }

    /**
     * Generate bucket key based on the rate limiting strategy
     */
    private String generateBucketKey(RateLimited rateLimited) {
        // If custom key is provided, use it
        if (!rateLimited.key().isEmpty()) {
            return rateLimited.key();
        }

        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            logger.warn("No HTTP request context available for rate limiting");
            return "unknown";
        }

        String clientIp = getClientIpAddress(request);

        switch (rateLimited.value()) {
            case IP_BASED:
                return rateLimitService.createIpBasedKey(clientIp);

            case USER_BASED:
                String userId = getCurrentUserId();
                return rateLimitService.createUserBasedKey(userId != null ? userId : clientIp);

            case ENDPOINT_BASED:
                return rateLimitService.createEndpointBasedKey(request.getRequestURI());

            case COMBINED:
                return rateLimitService.createCombinedKey(clientIp, request.getRequestURI());

            case CUSTOM:
                throw new IllegalArgumentException("Custom key must be provided when using CUSTOM strategy");

            default:
                return rateLimitService.createIpBasedKey(clientIp);
        }
    }

    /**
     * Resolve the appropriate bucket based on rate limiting configuration
     */
    private Bucket resolveBucket(String key, RateLimited rateLimited) {
        switch (rateLimited.value()) {
            case STRICT:
                return rateLimitService.resolveStrictBucket(key);
            default:
                return rateLimitService.resolveBucket(key);
        }
    }

    /**
     * Get the current HTTP servlet request
     */
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    /**
     * Extract client IP address from HTTP request
     * Handles X-Forwarded-For header for proxy scenarios
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP if multiple are present
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Get current user ID from security context
     * This is a placeholder - implement based on your authentication mechanism
     */
    private String getCurrentUserId() {
        // TODO: Implement based on your authentication mechanism
        // For example, if using Spring Security:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        //     return ((UserDetails) authentication.getPrincipal()).getUsername();
        // }
        return null;
    }
}
