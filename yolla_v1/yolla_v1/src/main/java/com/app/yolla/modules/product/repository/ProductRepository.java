package com.app.yolla.modules.product.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {



}
