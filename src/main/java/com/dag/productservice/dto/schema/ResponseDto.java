package com.dag.productservice.dto.schema;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/* To be used by our service to send the response to our clients.  */

@Setter
@Getter
@ToString
public class ResponseDto {
    Integer id;
    String title;
    Double price;
    String category;
    String description;
}
