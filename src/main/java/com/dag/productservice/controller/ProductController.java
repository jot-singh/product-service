package com.dag.productservice.controller;

import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.services.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteproductById(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                productService.deleteProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
        return ResponseEntity.ok(productService.createProduct(requestDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductResponseDto> updateProductById(@PathVariable("id") String id,
                                                                @RequestBody ProductRequestDto requestDto) {
        return new ResponseEntity<>(productService.updateProductById(id, requestDto), HttpStatus.OK);
    }
}
