package com.dag.productservice.controller;

import com.dag.productservice.dto.CategoryResponseDto;
import com.dag.productservice.dto.CategoryRequestDto;
import com.dag.productservice.services.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto CategoryRequestDto) {
        return ResponseEntity.ok(categoryService.createCategory(CategoryRequestDto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategoryById(@PathVariable("id") String id,
            @RequestBody CategoryRequestDto CategoryRequestDto) {
        return new ResponseEntity<>(categoryService.updateCategoryById(id, CategoryRequestDto), HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<CategoryResponseDto[]> getAllCategories() {
        return ResponseEntity.ok(categoryService.findCategories());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.findCategoryById(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> deleteCategoryById(@PathVariable("id") String id) {
        return ResponseEntity.ok(
                categoryService.deleteCategoryById(id));
    }
}
