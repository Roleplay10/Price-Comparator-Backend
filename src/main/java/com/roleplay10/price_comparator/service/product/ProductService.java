package com.roleplay10.price_comparator.service.product;

import com.roleplay10.price_comparator.dto.response.product.PricePoint;
import com.roleplay10.price_comparator.dto.response.recommendation.Recommendation;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<PricePoint> getHistory(
            String productId,
            Optional<String> store,
            Optional<String> category,
            Optional<String> brand
    );
    List<Recommendation> getRecommendations(Optional<String> category,
                                            Optional<String> brand);
}
