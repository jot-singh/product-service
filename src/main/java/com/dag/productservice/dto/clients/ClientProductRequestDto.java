package com.dag.productservice.dto.clients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientProductRequestDto {
    private Long id;
    private String title;
    private String description;
    private String image;
    private String category;
    private double price;
}
