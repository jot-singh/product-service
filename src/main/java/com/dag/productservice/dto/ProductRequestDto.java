package com.dag.productservice.dto;

import lombok.Getter;
import lombok.Setter;

/* To be used by our service to send the response to our clients.  */

@Setter
@Getter
public class ProductRequestDto {
    String name;
    String title;
    Double price;
    String category;
    String description;
}
