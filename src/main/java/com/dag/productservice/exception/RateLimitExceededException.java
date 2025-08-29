package com.dag.productservice.exceptionhandlers.exceptions;

/**
 * Exception thrown when rate limit is exceeded
 * Provides information about the rate limiting violation
 */
public class RateLimitExceededException extends RuntimeException {

    private final String bucketKey;
    private final long retryAfterSeconds;

    public RateLimitExceededException(String message) {
        super(message);
        this.bucketKey = null;
        this.retryAfterSeconds = 60; // Default retry after 60 seconds
    }

    public RateLimitExceededException(String message, String bucketKey) {
        super(message);
        this.bucketKey = bucketKey;
        this.retryAfterSeconds = 60;
    }

    public RateLimitExceededException(String message, String bucketKey, long retryAfterSeconds) {
        super(message);
        this.bucketKey = bucketKey;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String getBucketKey() {
        return bucketKey;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
