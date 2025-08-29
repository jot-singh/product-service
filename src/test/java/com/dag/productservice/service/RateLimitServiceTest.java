package com.dag.productservice.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private LettuceBasedProxyManager proxyManager;

    @Mock
    private BucketConfiguration defaultBucketConfiguration;

    @Mock
    private BucketConfiguration strictBucketConfiguration;

    @Mock
    private Bucket mockBucket;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(proxyManager, defaultBucketConfiguration, strictBucketConfiguration);
    }

    @Test
    void createIpBasedKey_ShouldReturnCorrectFormat() {
        // Given
        String clientIp = "192.168.1.1";

        // When
        String result = rateLimitService.createIpBasedKey(clientIp);

        // Then
        assertEquals("ip:192.168.1.1", result);
    }

    @Test
    void createUserBasedKey_ShouldReturnCorrectFormat() {
        // Given
        String userId = "user123";

        // When
        String result = rateLimitService.createUserBasedKey(userId);

        // Then
        assertEquals("user:user123", result);
    }

    @Test
    void createEndpointBasedKey_ShouldReturnCorrectFormat() {
        // Given
        String endpoint = "/api/products";

        // When
        String result = rateLimitService.createEndpointBasedKey(endpoint);

        // Then
        assertEquals("endpoint:/api/products", result);
    }

    @Test
    void createCombinedKey_ShouldReturnCorrectFormat() {
        // Given
        String clientIp = "192.168.1.1";
        String endpoint = "/api/products";

        // When
        String result = rateLimitService.createCombinedKey(clientIp, endpoint);

        // Then
        assertEquals("combined:192.168.1.1:/api/products", result);
    }

    @Test
    void tryConsume_ShouldDelegateToBucket() {
        // Given
        when(mockBucket.tryConsume(1)).thenReturn(true);

        // When
        boolean result = rateLimitService.tryConsume(mockBucket);

        // Then
        assertTrue(result);
        verify(mockBucket).tryConsume(1);
    }

    @Test
    void tryConsume_WithTokens_ShouldDelegateToBucket() {
        // Given
        long tokens = 5;
        when(mockBucket.tryConsume(tokens)).thenReturn(false);

        // When
        boolean result = rateLimitService.tryConsume(mockBucket, tokens);

        // Then
        assertFalse(result);
        verify(mockBucket).tryConsume(tokens);
    }

    @Test
    void getAvailableTokens_ShouldDelegateToBucket() {
        // Given
        long expectedTokens = 100;
        when(mockBucket.getAvailableTokens()).thenReturn(expectedTokens);

        // When
        long result = rateLimitService.getAvailableTokens(mockBucket);

        // Then
        assertEquals(expectedTokens, result);
        verify(mockBucket).getAvailableTokens();
    }

    @Test
    void clearCache_ShouldClearLocalCache() {
        // Given - cache is initially empty
        assertEquals(0, rateLimitService.getCacheSize());

        // When
        rateLimitService.clearCache();

        // Then
        assertEquals(0, rateLimitService.getCacheSize());
    }

    @Test
    void getCacheSize_ShouldReturnCorrectSize() {
        // When - cache is empty
        int size = rateLimitService.getCacheSize();

        // Then
        assertEquals(0, size);
    }
}
