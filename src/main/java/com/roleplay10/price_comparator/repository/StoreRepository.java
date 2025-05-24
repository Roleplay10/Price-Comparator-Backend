package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Short> {
    Optional<Store> findByNameIgnoreCase(String name);
}