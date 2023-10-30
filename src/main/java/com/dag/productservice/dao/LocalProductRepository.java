package com.dag.productservice.dao;

import com.dag.productservice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocalProductRepository extends JpaRepository<Product, UUID> {
}
