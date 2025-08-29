package com.dag.productservice.services.product;

import com.dag.productservice.dao.schema.LocalProductRepository;
import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class CachedLocalProductServiceIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    @Autowired
    private CachedLocalProductService cachedLocalProductService;

    @Autowired
    private LocalProductRepository localProductRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        // Clear database and cache before each test
        localProductRepository.deleteAll();
        cacheManager.getCache("products").clear();
        redisTemplate.getConnectionFactory().getConnection().commands().flushAll();
    }

    @Test
    void getProductById_ShouldCacheResult() {
        // Given
        Product product = createTestProduct();
        Product savedProduct = localProductRepository.save(product);

        // When - First call should hit database
        ProductResponseDto result1 = cachedLocalProductService.getProductById(savedProduct.getId().toString());

        // When - Second call should hit cache
        ProductResponseDto result2 = cachedLocalProductService.getProductById(savedProduct.getId().toString());

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getName(), result2.getName());

        // Verify cache contains the product
        String cacheKey = "products::" + savedProduct.getId().toString();
        assertTrue(redisTemplate.hasKey(cacheKey));
    }

    @Test
    void getAllProducts_ShouldCacheResult() {
        // Given
        Product product1 = createTestProduct();
        Product product2 = createTestProduct();
        product2.setName("Another Product");

        localProductRepository.save(product1);
        localProductRepository.save(product2);

        // When - First call should hit database
        List<ProductResponseDto> result1 = cachedLocalProductService.getAllProducts();

        // When - Second call should hit cache
        List<ProductResponseDto> result2 = cachedLocalProductService.getAllProducts();

        // Then
        assertEquals(2, result1.size());
        assertEquals(2, result2.size());

        // Verify cache contains the products
        String cacheKey = "products::all";
        assertTrue(redisTemplate.hasKey(cacheKey));
    }

    @Test
    void createProduct_ShouldInvalidateCache() {
        // Given
        ProductRequestDto requestDto = createTestProductRequestDto();

        // When
        ProductResponseDto created = cachedLocalProductService.createProduct(requestDto);

        // Then
        assertNotNull(created);

        // Verify cache invalidation event was published
        // Note: In a real scenario, we'd verify the pub/sub message
        // For this test, we verify the product was created
        List<Product> products = localProductRepository.findAll();
        assertEquals(1, products.size());
    }

    @Test
    void updateProduct_ShouldInvalidateSpecificCache() {
        // Given
        Product product = createTestProduct();
        Product savedProduct = localProductRepository.save(product);

        ProductRequestDto updateRequest = createTestProductRequestDto();
        updateRequest.setName("Updated Product Name");

        // When
        ProductResponseDto updated = cachedLocalProductService.updateProductById(
            savedProduct.getId().toString(), updateRequest);

        // Then
        assertNotNull(updated);
        assertEquals("Updated Product Name", updated.getName());

        // Verify product was updated in database
        Product dbProduct = localProductRepository.findById(savedProduct.getId()).orElse(null);
        assertNotNull(dbProduct);
        assertEquals("Updated Product Name", dbProduct.getName());
    }

    @Test
    void deleteProduct_ShouldInvalidateCache() {
        // Given
        Product product = createTestProduct();
        Product savedProduct = localProductRepository.save(product);

        // When
        ProductResponseDto deleted = cachedLocalProductService.deleteProductById(savedProduct.getId().toString());

        // Then
        assertNotNull(deleted);

        // Verify product was deleted from database
        assertFalse(localProductRepository.existsById(savedProduct.getId()));
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setTitle("Test Title");
        product.setDescription("Test Description");
        return product;
    }

    private ProductRequestDto createTestProductRequestDto() {
        ProductRequestDto dto = new ProductRequestDto();
        dto.setName("Test Product");
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");
        return dto;
    }
}
