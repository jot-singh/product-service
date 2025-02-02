package com.dag.productservice.controller;

import com.dag.productservice.security.JwtObject;
import com.dag.productservice.security.TokenValidator;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dag.productservice.dto.ProductRequestDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.services.product.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private TokenValidator tokenValidator;

    ProductController(ProductService productService, TokenValidator tokenValidator) {
        this.productService = productService;
        this.tokenValidator = tokenValidator;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@Nullable @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken,
                                                             @PathVariable("id") String id, HttpServletRequest request) {

        System.out.println(authToken);
        Optional<JwtObject> authTokenObjOptional;
        JwtObject authTokenObj = null;

        if (authToken != null) {
            authTokenObjOptional = tokenValidator.validateToken(authToken);
            if (authTokenObjOptional.isEmpty()) {
                // ignore
            }

            authTokenObj = authTokenObjOptional.get();
        }

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteproductById(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                productService.deleteProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
        return ResponseEntity.ok(productService.createProduct(requestDto));
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductResponseDto> updateProductById(@PathVariable("id") String id,
                                                                @RequestBody ProductRequestDto requestDto) {
        return new ResponseEntity<>(productService.updateProductById(id, requestDto), HttpStatus.OK);
    }
}
