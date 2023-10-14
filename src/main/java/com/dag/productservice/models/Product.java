package com.dag.productservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product extends V0 {
    private String title;
    private String description;
    private String image;
    private Category category;
    private double price;
}
