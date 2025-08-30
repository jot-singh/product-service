package com.dag.productservice.services.product;

import com.dag.productservice.dao.schema.CategoryRepository;
import com.dag.productservice.dao.schema.LocalProductRepository;
import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.exception.NotFoundException;
import com.dag.productservice.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.dag.productservice.service.product.CachedLocalProductService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedLocalProductServiceTest {

    @Mock
    private LocalProductRepository localProductRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private CachedLocalProductService cachedLocalProductService;

    private Product testProduct;
    private ProductRequestDto testRequestDto;
    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testProduct = new Product();
        testProduct.setId(testProductId);
        testProduct.setName("Test Product");
        testProduct.setTitle("Test Title");
        testProduct.setDescription("Test Description");

        testRequestDto = new ProductRequestDto();
        testRequestDto.setName("Test Product");
        testRequestDto.setTitle("Test Title");
        testRequestDto.setDescription("Test Description");
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Given
        when(localProductRepository.findById(testProductId))
            .thenReturn(Optional.of(testProduct));

        // When
        ProductResponseDto result = cachedLocalProductService.getProductById(testProductId.toString());

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(localProductRepository, times(1)).findById(testProductId);
    }

    @Test
    void getProductById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Given
        when(localProductRepository.findById(testProductId))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
            cachedLocalProductService.getProductById(testProductId.toString()));
        verify(localProductRepository, times(1)).findById(testProductId);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(localProductRepository.findAll()).thenReturn(products);

        // When
        List<ProductResponseDto> result = cachedLocalProductService.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getName(), result.get(0).getName());
        verify(localProductRepository, times(1)).findAll();
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct_WhenValidRequest() {
        // Given
        when(localProductRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponseDto result = cachedLocalProductService.createProduct(testRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(localProductRepository, times(1)).save(any(Product.class));
        verify(redisTemplate, times(1)).convertAndSend(eq("product-cache-events"), anyString());
    }

    @Test
    void createProduct_ShouldThrowIllegalArgumentException_WhenInvalidRequest() {
        // Given
        testRequestDto.setName(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
            cachedLocalProductService.createProduct(testRequestDto));
        verify(localProductRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProductById_ShouldDeleteAndReturnProduct_WhenProductExists() {
        // Given
        when(localProductRepository.existsById(testProductId)).thenReturn(true);
        when(localProductRepository.findById(testProductId))
            .thenReturn(Optional.of(testProduct));

        // When
        ProductResponseDto result = cachedLocalProductService.deleteProductById(testProductId.toString());

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(localProductRepository, times(1)).deleteById(testProductId);
        verify(redisTemplate, times(1)).convertAndSend(eq("product-cache-events"), anyString());
    }

    @Test
    void deleteProductById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Given
        when(localProductRepository.existsById(testProductId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () ->
            cachedLocalProductService.deleteProductById(testProductId.toString()));
        verify(localProductRepository, never()).deleteById(any());
    }

    @Test
    void updateProductById_ShouldUpdateAndReturnProduct_WhenValidRequest() {
        // Given
        when(localProductRepository.existsById(testProductId)).thenReturn(true);
        when(localProductRepository.findById(testProductId))
            .thenReturn(Optional.of(testProduct));
        when(localProductRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponseDto result = cachedLocalProductService.updateProductById(
            testProductId.toString(), testRequestDto);

        // Then
        assertNotNull(result);
        verify(localProductRepository, times(1)).save(any(Product.class));
        verify(redisTemplate, times(1)).convertAndSend(eq("product-cache-events"), anyString());
    }

    @Test
    void updateProductById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Given
        when(localProductRepository.existsById(testProductId)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () ->
            cachedLocalProductService.updateProductById(testProductId.toString(), testRequestDto));
        verify(localProductRepository, never()).save(any(Product.class));
    }
}
