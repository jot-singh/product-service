package com.dag.productservice.aspect;

import com.dag.productservice.annotation.RateLimited;
import com.dag.productservice.exception.RateLimitExceededException;
import com.dag.productservice.service.RateLimitService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP Aspect for applying rate limiting to methods annotated with @RateLimited
 * Intercepts method calls and applies rate limiting based on the annotation configuration
 * Gracefully handles cases when Redis is not available by skipping rate limiting
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    private RateLimitService rateLimitService;
    private boolean rateLimitingEnabled = true;

    public RateLimitAspect() {
        // Default constructor for when RateLimitService is not available
        this.rateLimitingEnabled = false;
        logger.warn("RateLimitService not available, rate limiting will be disabled");
    }

    @Autowired(required = false)
    public RateLimitAspect(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
        if (rateLimitService != null) {
            this.rateLimitingEnabled = true;
            logger.info("Rate limiting enabled with Redis support");
        } else {
            this.rateLimitingEnabled = false;
            logger.warn("RateLimitService not available, rate limiting will be disabled");
        }
    }

    /**
     * Around advice for methods annotated with @RateLimited
     * Applies rate limiting before method execution
     */
    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        // If rate limiting is disabled (Redis not available), skip rate limiting
        if (!rateLimitingEnabled || rateLimitService == null) {
            logger.debug("Rate limiting disabled, proceeding without rate limit check");
            return joinPoint.proceed();
        }

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

        if (rateLimitService == null) {
            // Fallback when rate limiting service is not available
            return "fallback:" + clientIp;
        }

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
        if (rateLimitService == null) {
            // This should not happen since we check in enforceRateLimit, but fallback just in case
            throw new IllegalStateException("Rate limiting service is not available");
        }

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
     * Extracts user ID from Spring Security authentication
     */
    private String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    return ((UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    return (String) principal;
                }
            }
        } catch (Exception e) {
            logger.debug("Could not extract user ID from security context: {}", e.getMessage());
        }
        return null;
    }
}
