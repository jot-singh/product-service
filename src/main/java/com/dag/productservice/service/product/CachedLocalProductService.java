package com.dag.productservice.service.product;

import com.dag.productservice.dao.schema.CategoryRepository;
import com.dag.productservice.dao.schema.LocalProductRepository;
import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.exception.NotFoundException;
import com.dag.productservice.models.Category;
import com.dag.productservice.models.Product;
import com.dag.productservice.validators.Validators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cached implementation of ProductService using Redis
 * Provides caching for frequently accessed product data to improve performance
 * and reduce database load
 */
@Service
@Primary
@Slf4j
public class CachedLocalProductService implements ProductService {

    private final LocalProductRepository localProductRepository;
    private final CategoryRepository categoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public CachedLocalProductService(LocalProductRepository localProductRepository,
                                     CategoryRepository categoryRepository,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.localProductRepository = localProductRepository;
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
        log.info("CachedLocalProductService initialized with Redis caching");
    }

    @Override
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public ProductResponseDto getProductById(String id) {
        log.debug("Fetching product from database for ID: {}", id);
        Optional<Product> product = this.localProductRepository.findById(UUID.fromString(id));
        if (product.isEmpty()) {
            log.warn("Product not found for ID: {}", id);
            throw new NotFoundException("Product not found");
        }
        log.debug("Product found and cached for ID: {}", id);
        return new ProductResponseDto(product.get());
    }

    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<ProductResponseDto> getAllProducts() {
        log.debug("Fetching all products from database");
        List<ProductResponseDto> products = this.localProductRepository.findAll().stream()
                .map(ProductResponseDto::new)
                .toList();
        log.debug("Retrieved {} products and cached", products.size());
        return products;
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        log.debug("Creating new product: {}", requestDto.getName());

        if (Validators.NON_NULL_CHECK.get().isValid(requestDto.getName()) &&
                Validators.NON_NULL_CHECK.get().isValid(requestDto.getDescription()) &&
                Validators.NON_NULL_CHECK.get().isValid(requestDto.getTitle())) {

            Product product = new Product();
            product.setName(requestDto.getName());
            product.setPrice(requestDto.getPrice());
            product.setDescription(requestDto.getDescription());
            product.setTitle(requestDto.getTitle());

            if (requestDto.getCategory() != null && !requestDto.getCategory().isEmpty()) {
                if (!Validators.UUID_VALIDATOR.get().isValid(requestDto.getCategory()))
                    throw new IllegalArgumentException("Invalid input");
                Category category = this.categoryRepository.findById(
                        UUID.fromString(requestDto.getCategory())).orElse(null);
                product.setCategory(category);
            }

            Product createdProduct = this.localProductRepository.save(product);
            log.info("Product created and cache invalidated: {}", createdProduct.getId());

            // Publish cache invalidation event
            publishCacheInvalidationEvent("PRODUCT_CREATED", createdProduct.getId().toString());

            return new ProductResponseDto(createdProduct);
        }
        throw new IllegalArgumentException("Invalid input");
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ProductResponseDto deleteProductById(String id) {
        log.debug("Deleting product with ID: {}", id);

        if (!Validators.UUID_VALIDATOR.get().isValid(id))
            throw new IllegalArgumentException("Invalid input");

        UUID uuid = UUID.fromString(id);
        if (this.localProductRepository.existsById(uuid)) {
            Product product = this.localProductRepository.findById(uuid).get();
            this.localProductRepository.deleteById(uuid);

            log.info("Product deleted and cache invalidated: {}", id);

            // Publish cache invalidation event
            publishCacheInvalidationEvent("PRODUCT_DELETED", id);

            return new ProductResponseDto(product);
        }
        throw new NotFoundException("Product not found");
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ProductResponseDto updateProductById(String id, ProductRequestDto requestDto) {
        log.debug("Updating product with ID: {}", id);

        if (Validators.NON_NULL_CHECK.get().isValid(requestDto.getName()) &&
                Validators.NON_NULL_CHECK.get().isValid(requestDto.getDescription()) &&
                Validators.NON_NULL_CHECK.get().isValid(requestDto.getTitle()) &&
                Validators.UUID_VALIDATOR.get().isValid(id)) {

            UUID uuid = UUID.fromString(id);
            if (this.localProductRepository.existsById(uuid)) {
                Product product = this.localProductRepository.findById(uuid).orElse(null);
                product.setTitle(requestDto.getTitle());
                product.setName(requestDto.getName());
                product.setDescription(requestDto.getDescription());
                product.setPrice(requestDto.getPrice());

                Product updatedProduct = this.localProductRepository.save(product);
                log.info("Product updated and cache invalidated: {}", id);

                // Publish cache invalidation event
                publishCacheInvalidationEvent("PRODUCT_UPDATED", id);

                return new ProductResponseDto(updatedProduct);
            } else {
                throw new NotFoundException("Product not found");
            }
        }
        throw new IllegalArgumentException("Invalid input");
    }

    /**
     * Publishes cache invalidation events to Redis pub/sub
     * This allows other services or instances to react to cache changes
     */
    private void publishCacheInvalidationEvent(String eventType, String productId) {
        try {
            String message = eventType + ":" + productId;
            redisTemplate.convertAndSend("product-cache-events", message);
            log.debug("Published cache invalidation event: {}", message);
        } catch (Exception e) {
            log.warn("Failed to publish cache invalidation event: {}", e.getMessage());
        }
    }
}
