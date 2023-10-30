package com.dag.productservice.services.category;

import com.dag.productservice.dto.CategoryResponseDto;
import com.dag.productservice.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
    //Write below functions declaration in code here.
    //findCategoryById(UUID) - should return category for specified category id
    //findCategories() - should return all categories
    //findCategoriesIn(List<UUID>) - should return all categories for specified category ids
    //createCategory(CategoryRequestDto) - should create category and return the created category
    //updateCategoryById(UUID, CategoryRequestDto) - should update category for specified category id and return the updated category
    //deleteCategoryById(UUID) - should delete category for specified category id and return the deleted category

    CategoryResponseDto findCategoryById(String id);
    CategoryResponseDto[] findCategories();
    CategoryResponseDto[] findCategoriesIn(List<String> ids);
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto updateCategoryById(String id, CategoryRequestDto categoryRequestDto);
    CategoryResponseDto deleteCategoryById(String id);
}
