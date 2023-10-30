package com.dag.productservice.dto;

import com.dag.productservice.models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    String id;
    String name;
    String title;
    Double price;
    String category;
    String description;

    //Write Copy Constructor of Existing class, which will take Product as input and return ProductResponseDto as output.
    public ProductResponseDto(Product product) {
        this.id = String.valueOf(product.getId());
        this.name = product.getName();
        this.title = product.getTitle();
        this.price = product.getPrice();
        this.category = product.getCategory() != null? product.getCategory().getName():null;
        this.description = product.getDescription();
    }

}
