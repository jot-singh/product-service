package com.dag.productservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Service for handling distributed cache invalidation across multiple service instances
 * Listens to Redis pub/sub messages and invalidates local caches accordingly
 */
@Service
@Slf4j
public class CacheInvalidationService implements MessageListener {

    private final CacheManager cacheManager;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

    public CacheInvalidationService(CacheManager cacheManager,
                                    RedisMessageListenerContainer redisMessageListenerContainer) {
        this.cacheManager = cacheManager;
        this.redisMessageListenerContainer = redisMessageListenerContainer;
    }

    @PostConstruct
    public void init() {
        // Subscribe to product cache events
        redisMessageListenerContainer.addMessageListener(this, new ChannelTopic("product-cache-events"));
        log.info("CacheInvalidationService initialized and subscribed to product-cache-events");
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());

        log.debug("Received cache invalidation message - Channel: {}, Body: {}", channel, body);

        try {
            String[] parts = body.split(":");
            if (parts.length == 2) {
                String eventType = parts[0];
                String productId = parts[1];

                handleCacheInvalidation(eventType, productId);
            } else {
                log.warn("Invalid message format: {}", body);
            }
        } catch (Exception e) {
            log.error("Error processing cache invalidation message: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles cache invalidation based on event type
     */
    private void handleCacheInvalidation(String eventType, String productId) {
        Cache productsCache = cacheManager.getCache("products");

        if (productsCache == null) {
            log.warn("Products cache not found");
            return;
        }

        switch (eventType) {
            case "PRODUCT_CREATED":
            case "PRODUCT_UPDATED":
            case "PRODUCT_DELETED":
                // Invalidate specific product cache
                productsCache.evict(productId);
                // Also invalidate the "all" cache to ensure consistency
                productsCache.evict("all");
                log.info("Invalidated cache for product: {} due to event: {}", productId, eventType);
                break;

            default:
                log.warn("Unknown event type: {}", eventType);
                break;
        }
    }

    /**
     * Manually invalidate product cache (useful for administrative operations)
     */
    public void invalidateProductCache(String productId) {
        Cache productsCache = cacheManager.getCache("products");
        if (productsCache != null) {
            productsCache.evict(productId);
            productsCache.evict("all");
            log.info("Manually invalidated cache for product: {}", productId);
        }
    }

    /**
     * Manually invalidate all product caches
     */
    public void invalidateAllProductCache() {
        Cache productsCache = cacheManager.getCache("products");
        if (productsCache != null) {
            productsCache.clear();
            log.info("Manually invalidated all product caches");
        }
    }
}
