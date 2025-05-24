package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("""
        SELECT d FROM Discount d
        WHERE d.createdAt >= :since
        ORDER BY d.createdAt DESC
        """)
    List<Discount> findNewSince(@Param("since") Instant since);
    @Query("""
      SELECT d FROM Discount d
      WHERE d.store.id     = :storeId
        AND d.product.id   = :productId
        AND d.startAt      <= :now
        AND d.endAt        >= :now
    """)
    Optional<Discount> findActiveForStoreAndProduct(
            @Param("storeId") Short storeId,
            @Param("productId") String productId,
            @Param("now") Instant now
    );
}
