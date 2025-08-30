package com.dag.productservice.controller;

import com.dag.productservice.models.elasticsearch.ProductDocument;
import com.dag.productservice.service.search.ProductSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for Elasticsearch-based product search operations
 */
@RestController
@RequestMapping("/api/search")
@Slf4j
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    /**
     * Basic product search
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDocument>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "relevance") String sortBy) {

        log.info("Search request - query: {}, page: {}, size: {}", query, page, size);

        Sort sort = getSort(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDocument> results = productSearchService.searchProducts(query, pageable);

        log.info("Search completed - found {} results for query: {}", results.getTotalElements(), query);
        return ResponseEntity.ok(results);
    }

    /**
     * Search products by category
     */
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<Page<ProductDocument>> searchProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Category search - categoryId: {}, query: {}, page: {}, size: {}", categoryId, query, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> results;

        if (query != null && !query.trim().isEmpty()) {
            results = productSearchService.searchProductsByCategory(query.trim(), categoryId, pageable);
        } else {
            results = productSearchService.searchProductsByCategory("", categoryId, pageable);
        }

        return ResponseEntity.ok(results);
    }

    /**
     * Search products by price range
     */
    @GetMapping("/products/price")
    public ResponseEntity<Page<ProductDocument>> searchProductsByPriceRange(
            @RequestParam String query,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Price range search - query: {}, minPrice: {}, maxPrice: {}, page: {}, size: {}",
                query, minPrice, maxPrice, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> results = productSearchService.searchProductsByPriceRange(
                query, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * Advanced search with multiple filters
     */
    @PostMapping("/products/advanced")
    public ResponseEntity<Page<ProductDocument>> advancedSearch(
            @RequestParam String query,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Advanced search - query: {}, tags: {}, price range: {}-{}, page: {}, size: {}",
                query, tags, minPrice, maxPrice, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> results = productSearchService.advancedSearch(
                query, tags, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * Get search suggestions/auto-complete
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductDocument>> getSearchSuggestions(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Getting search suggestions for prefix: {}", prefix);

        List<ProductDocument> suggestions = productSearchService.getSearchSuggestions(prefix);

        // Limit the results
        if (suggestions.size() > limit) {
            suggestions = suggestions.subList(0, limit);
        }

        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get popular products
     */
    @GetMapping("/products/popular")
    public ResponseEntity<Page<ProductDocument>> getPopularProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting popular products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> results = productSearchService.getPopularProducts(pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * Get featured products
     */
    @GetMapping("/products/featured")
    public ResponseEntity<Page<ProductDocument>> getFeaturedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting featured products - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDocument> results = productSearchService.getFeaturedProducts(pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * Helper method to determine sort order
     */
    private Sort getSort(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "price_asc":
                return Sort.by(Sort.Direction.ASC, "price");
            case "price_desc":
                return Sort.by(Sort.Direction.DESC, "price");
            case "name":
                return Sort.by(Sort.Direction.ASC, "name");
            case "newest":
                return Sort.by(Sort.Direction.DESC, "createdOn");
            case "oldest":
                return Sort.by(Sort.Direction.ASC, "createdOn");
            case "relevance":
            default:
                return Sort.by(Sort.Direction.DESC, "searchPriority", "popularityScore");
        }
    }
}
