package com.dag.productservice.services;

import com.dag.productservice.service.cache.CacheInvalidationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheInvalidationServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Mock
    private Cache productsCache;

    @InjectMocks
    private CacheInvalidationService cacheInvalidationService;

    @Test
    void onMessage_ShouldInvalidateCache_WhenProductCreatedEvent() {
        // Given
        when(cacheManager.getCache("products")).thenReturn(productsCache);
        String productId = "test-product-id";
        String messageBody = "PRODUCT_CREATED:" + productId;
        Message message = mock(Message.class);

        when(message.getChannel()).thenReturn("product-cache-events".getBytes());
        when(message.getBody()).thenReturn(messageBody.getBytes());

        // When
        cacheInvalidationService.onMessage(message, null);

        // Then
        verify(productsCache, times(1)).evict(productId);
        verify(productsCache, times(1)).evict("all");
    }

    @Test
    void onMessage_ShouldInvalidateCache_WhenProductUpdatedEvent() {
        // Given
        when(cacheManager.getCache("products")).thenReturn(productsCache);
        String productId = "test-product-id";
        String messageBody = "PRODUCT_UPDATED:" + productId;
        Message message = mock(Message.class);

        when(message.getChannel()).thenReturn("product-cache-events".getBytes());
        when(message.getBody()).thenReturn(messageBody.getBytes());

        // When
        cacheInvalidationService.onMessage(message, null);

        // Then
        verify(productsCache, times(1)).evict(productId);
        verify(productsCache, times(1)).evict("all");
    }

    @Test
    void onMessage_ShouldInvalidateCache_WhenProductDeletedEvent() {
        // Given
        when(cacheManager.getCache("products")).thenReturn(productsCache);
        String productId = "test-product-id";
        String messageBody = "PRODUCT_DELETED:" + productId;
        Message message = mock(Message.class);

        when(message.getChannel()).thenReturn("product-cache-events".getBytes());
        when(message.getBody()).thenReturn(messageBody.getBytes());

        // When
        cacheInvalidationService.onMessage(message, null);

        // Then
        verify(productsCache, times(1)).evict(productId);
        verify(productsCache, times(1)).evict("all");
    }

    @Test
    void onMessage_ShouldHandleInvalidMessageFormat() {
        // Given
        String invalidMessageBody = "INVALID_FORMAT";
        Message message = mock(Message.class);

        when(message.getChannel()).thenReturn("product-cache-events".getBytes());
        when(message.getBody()).thenReturn(invalidMessageBody.getBytes());

        // When
        cacheInvalidationService.onMessage(message, null);

        // Then - No cache operations should be performed
        verify(productsCache, never()).evict(any());
    }

    @Test
    void onMessage_ShouldHandleUnknownEventType() {
        // Given
        String productId = "test-product-id";
        String messageBody = "UNKNOWN_EVENT:" + productId;
        Message message = mock(Message.class);

        when(message.getChannel()).thenReturn("product-cache-events".getBytes());
        when(message.getBody()).thenReturn(messageBody.getBytes());

        // When
        cacheInvalidationService.onMessage(message, null);

        // Then
        verify(productsCache, never()).evict(any());
    }

    @Test
    void invalidateProductCache_ShouldEvictSpecificProductAndAllCache() {
        // Given
        when(cacheManager.getCache("products")).thenReturn(productsCache);
        String productId = "test-product-id";

        // When
        cacheInvalidationService.invalidateProductCache(productId);

        // Then
        verify(productsCache, times(1)).evict(productId);
        verify(productsCache, times(1)).evict("all");
    }

    @Test
    void invalidateAllProductCache_ShouldClearEntireCache() {
        // Given
        when(cacheManager.getCache("products")).thenReturn(productsCache);

        // When
        cacheInvalidationService.invalidateAllProductCache();

        // Then
        verify(productsCache, times(1)).clear();
    }

    @Test
    void invalidateProductCache_ShouldHandleNullCache() {
        // Given
        when(cacheManager.getCache("products")).thenReturn(null);
        String productId = "test-product-id";

        // When
        cacheInvalidationService.invalidateProductCache(productId);

        // Then
        verify(productsCache, never()).evict(any());
    }
}
