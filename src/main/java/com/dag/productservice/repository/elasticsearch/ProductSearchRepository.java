package com.dag.productservice.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.dag.productservice.models.elasticsearch.ProductDocument;

import java.math.BigDecimal;
import java.util.List;

/**
 * Elasticsearch repository for product search operations
 */
@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    /**
     * Search products by name or description
     */
    Page<ProductDocument> findByNameOrDescription(String name, String description, Pageable pageable);

    /**
     * Search products by category
     */
    Page<ProductDocument> findByCategoryId(String categoryId, Pageable pageable);

    /**
     * Search products by price range
     */
    Page<ProductDocument> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Search products by tags
     */
    Page<ProductDocument> findByTagsIn(List<String> tags, Pageable pageable);

    /**
     * Full-text search across searchable content
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^2\", \"searchableContent\"], \"fuzziness\": \"AUTO\"}}")
    Page<ProductDocument> searchByQuery(String query, Pageable pageable);

    /**
     * Advanced search with filters
     */
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^2\", \"searchableContent\"]}}], \"filter\": [{\"term\": {\"categoryId\": \"?1\"}}]}}")
    Page<ProductDocument> searchByQueryAndCategory(String query, String categoryId, Pageable pageable);

    /**
     * Search with price range filter
     */
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^2\", \"searchableContent\"]}}], \"filter\": [{\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}]}}")
    Page<ProductDocument> searchByQueryAndPriceRange(String query, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Find products by multiple criteria
     */
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description^2\", \"searchableContent\"]}}], \"filter\": [{\"terms\": {\"tags\": ?1}}, {\"range\": {\"price\": {\"gte\": ?2, \"lte\": ?3}}}]}}")
    Page<ProductDocument> searchByQueryTagsAndPrice(String query, List<String> tags, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Search suggestions/auto-complete
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"title\"], \"type\": \"phrase_prefix\"}}")
    List<ProductDocument> findSuggestions(String prefix);

    /**
     * Find popular products (by popularity score)
     */
    Page<ProductDocument> findByOrderByPopularityScoreDesc(Pageable pageable);

    /**
     * Find products by search priority
     */
    Page<ProductDocument> findByOrderBySearchPriorityDesc(Pageable pageable);
}
