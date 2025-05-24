package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
    List<PriceAlert> findByUserUsername(String username);
    List<PriceAlert> findByActiveTrue();
    Optional<PriceAlert> findByUserUsernameAndProductId(String username, String productId);
}
