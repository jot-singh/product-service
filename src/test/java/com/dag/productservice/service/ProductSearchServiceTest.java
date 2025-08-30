package com.dag.productservice.service;

import com.dag.productservice.service.search.ProductSearchService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.dag.productservice.models.elasticsearch.ProductDocument;
import com.dag.productservice.repository.elasticsearch.ProductSearchRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit test for Elasticsearch product search functionality
 * Uses mocking to avoid external dependencies
 */
@ExtendWith(MockitoExtension.class)
public class ProductSearchServiceTest {

    @Mock
    private ProductSearchRepository productSearchRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    @Test
    public void testSearchProducts_EmptyQuery_ShouldReturnEmptyPage() {
        // Given
        String query = "";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ProductDocument> results = productSearchService.searchProducts(query, pageable);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).isEmpty();
        assertThat(results.getTotalElements()).isZero();
    }

    @Test
    public void testSearchProducts_NullQuery_ShouldReturnEmptyPage() {
        // Given
        String query = null;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ProductDocument> results = productSearchService.searchProducts(query, pageable);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).isEmpty();
        assertThat(results.getTotalElements()).isZero();
    }

    @Test
    public void testSearchProducts_ValidQuery_ShouldReturnResults() {
        // Given
        String query = "laptop";
        Pageable pageable = PageRequest.of(0, 10);

        ProductDocument product1 = createTestProduct("1", "Gaming Laptop", "High-performance gaming laptop");
        ProductDocument product2 = createTestProduct("2", "Business Laptop", "Professional business laptop");
        List<ProductDocument> products = Arrays.asList(product1, product2);
        Page<ProductDocument> productPage = new PageImpl<>(products, pageable, 2);

        when(productSearchRepository.searchByQuery(query, pageable)).thenReturn(productPage);

        // When
        Page<ProductDocument> results = productSearchService.searchProducts(query, pageable);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);
        assertThat(results.getContent().get(0).getName()).isEqualTo("Gaming Laptop");
    }

    @Test
    public void testGetSearchSuggestions_ValidPrefix_ShouldReturnList() {
        // Given
        String prefix = "lap";

        ProductDocument suggestion1 = createTestProduct("1", "laptop", "Portable computer");
        ProductDocument suggestion2 = createTestProduct("2", "laptop bag", "Carrying case for laptop");
        ProductDocument suggestion3 = createTestProduct("3", "laptop stand", "Elevated platform for laptop");
        List<ProductDocument> suggestions = Arrays.asList(suggestion1, suggestion2, suggestion3);

        when(productSearchRepository.findSuggestions(prefix)).thenReturn(suggestions);

        // When
        var result = productSearchService.getSearchSuggestions(prefix);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("laptop");
        assertThat(result.get(1).getName()).isEqualTo("laptop bag");
        assertThat(result.get(2).getName()).isEqualTo("laptop stand");
    }

    @Test
    public void testGetPopularProducts_ShouldReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        ProductDocument product1 = createTestProduct("1", "Popular Product 1", "Most popular product");
        ProductDocument product2 = createTestProduct("2", "Popular Product 2", "Second most popular");
        List<ProductDocument> products = Arrays.asList(product1, product2);
        Page<ProductDocument> productPage = new PageImpl<>(products, pageable, 2);

        when(productSearchRepository.findByOrderByPopularityScoreDesc(pageable)).thenReturn(productPage);

        // When
        Page<ProductDocument> results = productSearchService.getPopularProducts(pageable);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testGetFeaturedProducts_ShouldReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        ProductDocument product1 = createTestProduct("1", "Featured Product 1", "Featured product");
        List<ProductDocument> products = Arrays.asList(product1);
        Page<ProductDocument> productPage = new PageImpl<>(products, pageable, 1);

        when(productSearchRepository.findByOrderBySearchPriorityDesc(pageable)).thenReturn(productPage);

        // When
        Page<ProductDocument> results = productSearchService.getFeaturedProducts(pageable);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getTotalElements()).isEqualTo(1);
    }

    private ProductDocument createTestProduct(String id, String name, String description) {
        ProductDocument product = new ProductDocument();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setCategoryId("electronics");
        product.setCategoryName("Electronics");
        return product;
    }
}
