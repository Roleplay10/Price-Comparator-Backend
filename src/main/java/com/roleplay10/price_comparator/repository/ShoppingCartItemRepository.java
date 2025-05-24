package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {
    List<ShoppingCartItem> findByCartId(Long cartId);
}
