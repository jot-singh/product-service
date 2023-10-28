package com.dag.productservice.dao.schema;

import com.dag.productservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
