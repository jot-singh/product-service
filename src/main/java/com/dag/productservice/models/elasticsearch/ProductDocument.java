package com.dag.productservice.models.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Elasticsearch document for product search functionality
 */
@Document(indexName = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "english", searchAnalyzer = "english")
    private String description;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Text)
    private String categoryName;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Text)
    private String image;

    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;

    @Field(type = FieldType.Date)
    private LocalDateTime createdOn;

    @Field(type = FieldType.Date)
    private LocalDateTime modifiedOn;

    @Field(type = FieldType.Text)
    private String createdBy;

    @Field(type = FieldType.Text)
    private String modifiedBy;

    // Search-specific fields
    @Field(type = FieldType.Text, analyzer = "english", searchAnalyzer = "english")
    private String searchableContent; // Combined searchable text

    @Field(type = FieldType.Keyword)
    private List<String> tags; // For filtering and faceting

    @Field(type = FieldType.Double)
    private Double popularityScore; // For search result ranking

    @Field(type = FieldType.Integer)
    private Integer searchPriority; // For boosting certain products in search results
}
