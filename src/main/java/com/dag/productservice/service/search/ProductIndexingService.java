package com.dag.productservice.service.search;

import com.dag.productservice.dao.schema.LocalProductRepository;
import com.dag.productservice.models.Product;
import com.dag.productservice.models.elasticsearch.ProductDocument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.UUID;
import java.util.Optional;

/**
 * Service for synchronizing product data between MySQL and Elasticsearch
 */
@Service
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true")
@Slf4j
public class ProductIndexingService {

    @Autowired
    private LocalProductRepository productRepository;

    @Autowired(required = false)
    private ProductSearchService productSearchService;

    /**
     * Index a single product
     */
    @Transactional(readOnly = true)
    public void indexProduct(String productId) {
        log.info("Indexing product: {}", productId);

        Optional<Product> productOpt = productRepository.findById(UUID.fromString(productId));
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            ProductDocument document = convertToDocument(product);
            productSearchService.indexProduct(document);
            log.info("Successfully indexed product: {}", productId);
        } else {
            log.warn("Product not found for indexing: {}", productId);
        }
    }

    /**
     * Remove product from index
     */
    public void removeProductFromIndex(String productId) {
        log.info("Removing product from index: {}", productId);
        productSearchService.removeProductFromIndex(productId);
    }

    /**
     * Reindex all products (full sync)
     */
    @Transactional(readOnly = true)
    public void reindexAllProducts() {
        log.info("Starting full product reindexing...");

        List<Product> products = productRepository.findAll();
        List<ProductDocument> documents = new ArrayList<>();

        for (Product product : products) {
            if (!product.getIsDeleted()) {
                ProductDocument document = convertToDocument(product);
                documents.add(document);
            }
        }

        if (!documents.isEmpty()) {
            productSearchService.indexProducts(documents);
            log.info("Successfully reindexed {} products", documents.size());
        } else {
            log.info("No products to reindex");
        }
    }

    /**
     * Scheduled job to sync products (runs every 30 minutes)
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void scheduledProductSync() {
        log.info("Running scheduled product sync...");
        try {
            reindexAllProducts();
        } catch (Exception e) {
            log.error("Error during scheduled product sync", e);
        }
    }

    /**
     * Initialize index on startup
     */
    @PostConstruct
    public void initializeIndex() {
        log.info("Initializing Elasticsearch index...");
        try {
            reindexAllProducts();
        } catch (Exception e) {
            log.error("Error initializing Elasticsearch index", e);
        }
    }

    /**
     * Convert Product entity to ProductDocument
     */
    private ProductDocument convertToDocument(Product product) {
        ProductDocument document = new ProductDocument();

        document.setId(product.getId().toString());
        document.setName(product.getName());
        document.setTitle(product.getTitle());
        document.setDescription(product.getDescription());
        document.setCategoryId(product.getCategory() != null ? product.getCategory().getId().toString() : null);
        document.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        document.setPrice(product.getPrice() != null ? BigDecimal.valueOf(product.getPrice().getPrice()) : null);
        document.setCurrency(product.getPrice() != null ? product.getPrice().getCurrency() : null);
        document.setImage(product.getImage());
        document.setIsDeleted(product.getIsDeleted());
        document.setCreatedOn(product.getCreatedOn());
        document.setModifiedOn(product.getModifiedOn());
        document.setCreatedBy(product.getCreatedBy());
        document.setModifiedBy(product.getModifiedBy());

        // Create searchable content (combination of name, title, description)
        StringBuilder searchableContent = new StringBuilder();
        if (product.getName() != null) searchableContent.append(product.getName()).append(" ");
        if (product.getTitle() != null) searchableContent.append(product.getTitle()).append(" ");
        if (product.getDescription() != null) searchableContent.append(product.getDescription()).append(" ");
        if (product.getCategory() != null && product.getCategory().getName() != null) {
            searchableContent.append(product.getCategory().getName()).append(" ");
        }
        document.setSearchableContent(searchableContent.toString().trim());

        // Set default values for search-specific fields
        document.setPopularityScore(0.0); // Can be updated based on sales data
        document.setSearchPriority(0); // Can be set based on business rules

        // Create tags for filtering
        List<String> tags = new ArrayList<>();
        if (product.getCategory() != null) {
            tags.add("category:" + product.getCategory().getId());
            if (product.getCategory().getName() != null) {
                tags.add("category-name:" + product.getCategory().getName().toLowerCase());
            }
        }
        if (product.getPrice() != null) {
            BigDecimal priceValue = BigDecimal.valueOf(product.getPrice().getPrice());
            if (priceValue.compareTo(BigDecimal.valueOf(50)) < 0) {
                tags.add("price-range:under-50");
            } else if (priceValue.compareTo(BigDecimal.valueOf(100)) < 0) {
                tags.add("price-range:50-100");
            } else if (priceValue.compareTo(BigDecimal.valueOf(500)) < 0) {
                tags.add("price-range:100-500");
            } else {
                tags.add("price-range:over-500");
            }
        }
        document.setTags(tags);

        return document;
    }
}
