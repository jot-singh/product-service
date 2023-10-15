package com.dag.productservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dag.productservice.dto.schema.RequestDto;
import com.dag.productservice.dto.schema.ResponseDto;
import com.dag.productservice.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto[]> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteproductById(@PathVariable("id") Integer id) {
        ResponseEntity<ResponseDto> responseEntity = ResponseEntity.ok(
                productService.deleteproductById(id));
        return responseEntity;
    }

    @PostMapping
    public ResponseEntity<ResponseDto> createProduct(@RequestBody RequestDto requestDto) {
        return ResponseEntity.ok(productService.createProduct(requestDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<ResponseDto> updateProductById(@PathVariable("id") Long id,
            @RequestBody RequestDto requestDto) {
        return new ResponseEntity<ResponseDto>(productService.updateProductById(id, requestDto), HttpStatus.OK);
    }

}