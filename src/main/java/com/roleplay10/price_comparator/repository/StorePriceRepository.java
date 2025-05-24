package com.roleplay10.price_comparator.repository;

import com.roleplay10.price_comparator.domain.entity.StorePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StorePriceRepository extends JpaRepository<StorePrice, Long> {
    @Query("""
      SELECT sp
      FROM StorePrice sp
      WHERE sp.product.id = :pid
        AND sp.effectiveAt = (
          SELECT MAX(s2.effectiveAt)
          FROM StorePrice s2
          WHERE s2.product.id = :pid AND s2.store = sp.store
        )
    """)
    List<StorePrice> findLatestPerStore(@Param("pid") String productId);
    List<StorePrice> findByProductId(String productId);

}
