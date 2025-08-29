package com.dag.productservice.conf;

import com.bucket4j.Bandwidth;
import com.bucket4j.BucketConfiguration;
import com.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * Rate Limiting Configuration using Bucket4j with Redis backend
 * Provides distributed rate limiting across multiple service instances
 */
@Configuration
public class RateLimitConfig {

    /**
     * Default bucket configuration for API rate limiting
     * Allows 100 requests per minute and 1000 requests per hour
     */
    @Bean
    public BucketConfiguration defaultBucketConfiguration() {
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
     */
    @Bean
    public LettuceBasedProxyManager<String> lettuceBasedProxyManager(RedisConnectionFactory connectionFactory) {
        // Get Redis connection details from the connection factory
        String redisUri = "redis://" + connectionFactory.getConnection().getServerCommands().info().get("tcp_port");

        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        return LettuceBasedProxyManager.builderFor(connection)
            .withExpirationStrategy(com.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager.ExpirationAfterWriteStrategy
                .basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1)))
            .build();
    }
}
