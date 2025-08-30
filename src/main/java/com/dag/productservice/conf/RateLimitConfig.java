package com.dag.productservice.conf;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.Duration;

/**
 * Rate Limiting Configuration using Bucket4j with Redis backend
 * Provides distributed rate limiting across multiple service instances
 */
@Configuration
public class RateLimitConfig {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitConfig.class);

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * Default bucket configuration for API rate limiting
     * Allows 100 requests per minute and 1000 requests per hour
     */
    @Bean
    public io.github.bucket4j.BucketConfiguration defaultBucketConfiguration() {
        return BucketConfiguration.builder()
            .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1)))  // 100 requests per minute
            .addLimit(Bandwidth.simple(1000, Duration.ofHours(1)))   // 1000 requests per hour
            .build();
    }

    /**
     * Strict bucket configuration for sensitive operations
     * Allows 10 requests per minute and 100 requests per hour
     */
    @Bean
    public BucketConfiguration strictBucketConfiguration() {
        return BucketConfiguration.builder()
            .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))   // 10 requests per minute
            .addLimit(Bandwidth.simple(100, Duration.ofHours(1)))    // 100 requests per hour
            .build();
    }

    /**
     * Bucket4j Proxy Manager for Redis-based distributed rate limiting
     * Uses Lettuce Redis client for connection management
     * Only creates the bean if Redis is available
     */
    @Bean
    @ConditionalOnProperty(name = "redis.enabled", havingValue = "true")
    public LettuceBasedProxyManager lettuceBasedProxyManager() {
        try {
            // Create Redis client with proper configuration
            RedisClient redisClient = RedisClient.create(String.format("redis://%s:%d", redisHost, redisPort));
            logger.info("Connecting to Redis at: {}:{}", redisHost, redisPort);

            // Test the connection before creating the proxy manager
            try (var connection = redisClient.connect()) {
                connection.sync().ping();
                logger.info("Redis connection test successful");
            }

            // Use the RedisClient builder method
            return LettuceBasedProxyManager.builderFor(redisClient)
                .build();
        } catch (RedisConnectionException e) {
            logger.warn("Redis is not available at {}:{}, falling back to local rate limiting", redisHost, redisPort);
            // Let the absence of the bean be handled by @ConditionalOnProperty; do not rethrow
            return null;
        } catch (Exception e) {
            logger.error("Failed to create LettuceBasedProxyManager", e);
            throw new RuntimeException("Failed to initialize rate limiting proxy manager", e);
        }
    }
}
