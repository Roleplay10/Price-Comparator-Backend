package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
