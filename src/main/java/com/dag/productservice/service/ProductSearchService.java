package com.dag.productservice.service;

import com.dag.productservice.models.elasticsearch.ProductDocument;
import com.dag.productservice.repository.elasticsearch.ProductSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for Elasticsearch-based product search operations
 */
@Service
@Slf4j
public class ProductSearchService {

    @Autowired
    private ProductSearchRepository productSearchRepository;

    /**
     * Search products by query string
     */
    public Page<ProductDocument> searchProducts(String query, Pageable pageable) {
        log.info("Searching products with query: {} (page: {}, size: {})",
                query, pageable.getPageNumber(), pageable.getPageSize());

        if (query == null || query.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        return productSearchRepository.searchByQuery(query.trim(), pageable);
    }

    /**
     * Search products by query and category
     */
    public Page<ProductDocument> searchProductsByCategory(String query, String categoryId, Pageable pageable) {
        log.info("Searching products with query: {} in category: {} (page: {}, size: {})",
                query, categoryId, pageable.getPageNumber(), pageable.getPageSize());

        if (query == null || query.trim().isEmpty()) {
            return productSearchRepository.findByCategoryId(categoryId, pageable);
        }

        return productSearchRepository.searchByQueryAndCategory(query.trim(), categoryId, pageable);
    }

    /**
     * Search products by query and price range
     */
    public Page<ProductDocument> searchProductsByPriceRange(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Searching products with query: {} in price range: {}-{} (page: {}, size: {})",
                query, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize());

        if (query == null || query.trim().isEmpty()) {
            return productSearchRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        }

        return productSearchRepository.searchByQueryAndPriceRange(query.trim(), minPrice, maxPrice, pageable);
    }

    /**
     * Advanced search with multiple filters
     */
    public Page<ProductDocument> advancedSearch(String query, List<String> tags, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Advanced search - query: {}, tags: {}, price range: {}-{} (page: {}, size: {})",
                query, tags, minPrice, maxPrice, pageable.getPageNumber(), pageable.getPageSize());

        if (query == null || query.trim().isEmpty()) {
            if (tags != null && !tags.isEmpty()) {
                return productSearchRepository.findByTagsIn(tags, pageable);
            } else if (minPrice != null && maxPrice != null) {
                return productSearchRepository.findByPriceBetween(minPrice, maxPrice, pageable);
            } else {
                return Page.empty(pageable);
            }
        }

        return productSearchRepository.searchByQueryTagsAndPrice(query.trim(), tags, minPrice, maxPrice, pageable);
    }

    /**
     * Get search suggestions/auto-complete
     */
    public List<ProductDocument> getSearchSuggestions(String prefix) {
        log.info("Getting search suggestions for prefix: {}", prefix);

        if (prefix == null || prefix.trim().length() < 2) {
            return List.of();
        }

        return productSearchRepository.findSuggestions(prefix.trim());
    }

    /**
     * Get popular products
     */
    public Page<ProductDocument> getPopularProducts(Pageable pageable) {
        log.info("Getting popular products (page: {}, size: {})",
                pageable.getPageNumber(), pageable.getPageSize());

        return productSearchRepository.findByOrderByPopularityScoreDesc(pageable);
    }

    /**
     * Get featured/high-priority products
     */
    public Page<ProductDocument> getFeaturedProducts(Pageable pageable) {
        log.info("Getting featured products (page: {}, size: {})",
                pageable.getPageNumber(), pageable.getPageSize());

        return productSearchRepository.findByOrderBySearchPriorityDesc(pageable);
    }

    /**
     * Index a product document
     */
    public ProductDocument indexProduct(ProductDocument productDocument) {
        log.info("Indexing product: {} ({})", productDocument.getName(), productDocument.getId());

        return productSearchRepository.save(productDocument);
    }

    /**
     * Remove a product from search index
     */
    public void removeProductFromIndex(String productId) {
        log.info("Removing product from search index: {}", productId);

        productSearchRepository.deleteById(productId);
    }

    /**
     * Bulk index products
     */
    public Iterable<ProductDocument> indexProducts(Iterable<ProductDocument> products) {
        log.info("Bulk indexing products");

        return productSearchRepository.saveAll(products);
    }

    /**
     * Check if product exists in search index
     */
    public boolean productExistsInIndex(String productId) {
        return productSearchRepository.existsById(productId);
    }

    /**
     * Get product from search index
     */
    public ProductDocument getProductFromIndex(String productId) {
        log.info("Getting product from search index: {}", productId);

        return productSearchRepository.findById(productId).orElse(null);
    }
}
