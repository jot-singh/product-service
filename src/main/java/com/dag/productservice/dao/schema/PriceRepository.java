package com.dag.productservice.dao.schema;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dag.productservice.models.Price;

import java.util.UUID;

public interface PriceRepository extends JpaRepository<Price, UUID> {
}
