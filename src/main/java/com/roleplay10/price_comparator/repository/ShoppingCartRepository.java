package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUserId(Long userId);
}