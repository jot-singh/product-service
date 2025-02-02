package com.dag.productservice.dto;

import com.dag.productservice.models.Price;

import lombok.Getter;
import lombok.Setter;

/* To be used by our service to send the response to our clients.  */

@Setter
@Getter
public class ProductRequestDto {
    String name;
    String title;
    Price price;
    String category;
    String description;
}
