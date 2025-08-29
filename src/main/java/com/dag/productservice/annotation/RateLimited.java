package com.dag.productservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for applying rate limiting to methods
 * Uses Bucket4j with Redis backend for distributed rate limiting
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /**
     * Rate limiting strategy to use
     */
    RateLimitType value() default RateLimitType.IP_BASED;

    /**
     * Custom bucket key (optional)
     * If not specified, key will be generated based on the strategy
     */
    String key() default "";

    /**
     * Number of tokens to consume per request
     */
    int tokens() default 1;

    /**
     * Custom error message when rate limit is exceeded
     */
    String message() default "Rate limit exceeded. Please try again later.";

    /**
     * Rate limiting strategies
     */
    enum RateLimitType {
        /**
         * Rate limit based on client IP address
         */
        IP_BASED,

        /**
         * Rate limit based on authenticated user ID
         */
        USER_BASED,

        /**
         * Rate limit based on API endpoint
         */
        ENDPOINT_BASED,

        /**
         * Rate limit based on combination of IP and endpoint
         */
        COMBINED,

        /**
         * Use strict rate limiting configuration
         */
        STRICT,

        /**
         * Custom rate limiting with provided key
         */
        CUSTOM
    }
}
