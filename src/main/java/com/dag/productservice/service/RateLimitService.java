package com.dag.productservice.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing rate limiting buckets using Bucket4j with Redis backend
 * Provides distributed rate limiting across multiple service instances
 */
@Service
public class RateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);

    private final LettuceBasedProxyManager proxyManager;
    private final BucketConfiguration defaultBucketConfiguration;
    private final BucketConfiguration strictBucketConfiguration;

    // Local cache for buckets to improve performance
    private final ConcurrentHashMap<String, Bucket> localBucketCache = new ConcurrentHashMap<>();

    public RateLimitService(LettuceBasedProxyManager proxyManager,
                          BucketConfiguration defaultBucketConfiguration,
                          BucketConfiguration strictBucketConfiguration) {
        this.proxyManager = proxyManager;
        this.defaultBucketConfiguration = defaultBucketConfiguration;
        this.strictBucketConfiguration = strictBucketConfiguration;
    }

    /**
     * Get or create a bucket for the given key using default configuration
     * @param key Unique identifier for the rate limit bucket
     * @return Bucket instance for rate limiting
     */
    public Bucket resolveBucket(String key) {
        return resolveBucket(key, defaultBucketConfiguration);
    }

    /**
     * Get or create a bucket for the given key with custom configuration
     * @param key Unique identifier for the rate limit bucket
     * @param configuration Bucket configuration to use
     * @return Bucket instance for rate limiting
     */
    public Bucket resolveBucket(String key, BucketConfiguration configuration) {
        return localBucketCache.computeIfAbsent(key, k -> {
            try {
                logger.debug("Creating new bucket for key: {}", k);
                return proxyManager.builder().build(k.getBytes(), () -> configuration);
            } catch (Exception e) {
                logger.error("Failed to create bucket for key: {}", k, e);
                throw new RuntimeException("Failed to create rate limit bucket", e);
            }
        });
    }

    /**
     * Get or create a bucket for the given key using strict configuration
     * @param key Unique identifier for the rate limit bucket
     * @return Bucket instance for rate limiting
     */
    public Bucket resolveStrictBucket(String key) {
        return resolveBucket(key, strictBucketConfiguration);
    }

    /**
     * Create a bucket key based on client IP address
     * @param clientIp Client IP address
     * @return Bucket key for IP-based rate limiting
     */
    public String createIpBasedKey(String clientIp) {
        return "ip:" + clientIp;
    }

    /**
     * Create a bucket key based on user ID
     * @param userId User identifier
     * @return Bucket key for user-based rate limiting
     */
    public String createUserBasedKey(String userId) {
        return "user:" + userId;
    }

    /**
     * Create a bucket key based on API endpoint
     * @param endpoint API endpoint path
     * @return Bucket key for endpoint-based rate limiting
     */
    public String createEndpointBasedKey(String endpoint) {
        return "endpoint:" + endpoint;
    }

    /**
     * Create a bucket key based on client IP and endpoint combination
     * @param clientIp Client IP address
     * @param endpoint API endpoint path
     * @return Bucket key for combined rate limiting
     */
    public String createCombinedKey(String clientIp, String endpoint) {
        return "combined:" + clientIp + ":" + endpoint;
    }

    /**
     * Check if a request should be allowed based on the bucket
     * @param bucket Rate limiting bucket
     * @return true if request is allowed, false if rate limited
     */
    public boolean tryConsume(Bucket bucket) {
        return bucket.tryConsume(1);
    }

    /**
     * Check if a request should be allowed and consume a token if allowed
     * @param bucket Rate limiting bucket
     * @param tokens Number of tokens to consume
     * @return true if request is allowed, false if rate limited
     */
    public boolean tryConsume(Bucket bucket, long tokens) {
        return bucket.tryConsume(tokens);
    }

    /**
     * Get the number of available tokens in the bucket
     * @param bucket Rate limiting bucket
     * @return Number of available tokens
     */
    public long getAvailableTokens(Bucket bucket) {
        return bucket.getAvailableTokens();
    }

    /**
     * Clear the local bucket cache
     * Useful for testing or when Redis connection changes
     */
    public void clearCache() {
        localBucketCache.clear();
        logger.info("Local bucket cache cleared");
    }

    /**
     * Get cache size for monitoring
     * @return Number of cached buckets
     */
    public int getCacheSize() {
        return localBucketCache.size();
    }
}
