package com.dag.productservice.models;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends V0 {
    @Column
    @NonNull
    String name;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private String image;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column
    private double price;
}
