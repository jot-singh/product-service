package com.dag.productservice.services.category;

import com.dag.productservice.dao.schema.CategoryRepository;
import com.dag.productservice.dto.CategoryRequestDto;
import com.dag.productservice.dto.CategoryResponseDto;
import com.dag.productservice.dto.ProductResponseDto;
import com.dag.productservice.exceptionhandlers.exceptions.NotFoundException;
import com.dag.productservice.models.Category;
import com.dag.productservice.models.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LocalCategoriesService implements CategoryService {

    private final CategoryRepository categoryRepository;

    public LocalCategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public CategoryResponseDto findCategoryById(String id) {
        UUID uuid = getUuidFromString(id);

        Category category = categoryRepository.findById(uuid).orElse(null);

        assert category != null;

        return serialistToCategoryDto(category);
    }

    /**
     * @return
     */
    @Override
    public CategoryResponseDto[] findCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().
                map(this::serialistToCategoryDto).toArray(CategoryResponseDto[]::new);
    }


    /**
     * @param ids
     * @return
     */
    @Override
    public CategoryResponseDto[] findCategoriesIn(List<String> ids) {
        List<UUID> uuids = new ArrayList<>();
        ids.forEach(id -> uuids.add(getUuidFromString(id)));
        List<Category> categories = categoryRepository.findAllById(uuids);
        return categories.stream().
                map(this::serialistToCategoryDto).toArray(CategoryResponseDto[]::new);
    }

    /**
     * @param categoryRequestDto
     * @return
     */
    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRequestDto == null)
            return null;
        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        category.setDescription(categoryRequestDto.getDescription());
        category.setProducts(new ArrayList<>());
        categoryRepository.save(category);
        return serialistToCategoryDto(category);
    }

    /**
     * @param id
     * @param categoryRequestDto
     * @return
     */
    @Override
    public CategoryResponseDto updateCategoryById(String id, CategoryRequestDto categoryRequestDto) {
        if (id == null || categoryRequestDto == null)
            throw new NotFoundException("Category for requested Id not found");
        UUID uuid = getUuidFromString(id);
        Category category = categoryRepository.findById(uuid).orElse(null);
        if (category == null)
            throw new NotFoundException("Category for requested Id not found");
        category.setName(categoryRequestDto.getName() != null ?
                categoryRequestDto.getName() : category.getName());
        category.setDescription(categoryRequestDto.getDescription() != null ?
                categoryRequestDto.getDescription() : category.getDescription());
        categoryRepository.save(category);
        return serialistToCategoryDto(category);
    }


    /**
     * @param id
     * @return
     */
    @Override
    @Transactional
    public CategoryResponseDto deleteCategoryById(String id) {
        if (id == null)
            throw new NotFoundException("Category for requested Id not found");
        UUID uuid = getUuidFromString(id);
        Category category = categoryRepository.findById(uuid).orElse(null);
        if (category == null)
            throw new NotFoundException("Category for requested Id not found");
        categoryRepository.delete(category);
        return serialistToCategoryDto(category);
    }

    private ProductResponseDto[] serialiseToProductsDto(List<Product> products) {
        if (products == null)
            return null;
        return products.stream().map(ProductResponseDto::new).toArray(ProductResponseDto[]::new);
    }

    private CategoryResponseDto serialistToCategoryDto(Category category) {
        return new CategoryResponseDto(category.getId().toString(), category.getName(), category.getDescription(),
                serialiseToProductsDto(category.getProducts()));
    }

    private UUID getUuidFromString(String id) {
        return UUID.fromString(id);
    }
}
