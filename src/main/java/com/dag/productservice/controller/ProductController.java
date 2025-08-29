package com.dag.productservice.controller;

import com.dag.productservice.annotation.RateLimited;
import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.services.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @RateLimited(value = RateLimited.RateLimitType.IP_BASED,
                 message = "Too many product list requests. Please try again later.")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @RateLimited(value = RateLimited.RateLimitType.IP_BASED,
                 message = "Too many product detail requests. Please try again later.")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    @RateLimited(value = RateLimited.RateLimitType.USER_BASED,
                 message = "Too many delete operations. Please try again later.")
    public ResponseEntity<ProductResponseDto> deleteproductById(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                productService.deleteProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @RateLimited(value = RateLimited.RateLimitType.STRICT,
                 message = "Too many product creation requests. Please try again later.")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
        return ResponseEntity.ok(productService.createProduct(requestDto));
    }

    @PutMapping("{id}")
    @RateLimited(value = RateLimited.RateLimitType.USER_BASED,
                 message = "Too many product update requests. Please try again later.")
    public ResponseEntity<ProductResponseDto> updateProductById(@PathVariable("id") String id,
                                                                @RequestBody ProductRequestDto requestDto) {
        return new ResponseEntity<>(productService.updateProductById(id, requestDto), HttpStatus.OK);
    }
}
