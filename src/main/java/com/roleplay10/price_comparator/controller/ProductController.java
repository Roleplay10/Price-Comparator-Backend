package com.roleplay10.price_comparator.controller;

import com.roleplay10.price_comparator.dto.response.product.PricePoint;
import com.roleplay10.price_comparator.dto.response.recommendation.Recommendation;
import com.roleplay10.price_comparator.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PricePoint>> getHistory(
            @PathVariable("id") String productId,
            @RequestParam Optional<String> store,
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> brand
    ) {
        List<PricePoint> history = productService.getHistory(
                productId, store, category, brand
        );
        return ResponseEntity.ok(history);
    }
    @GetMapping("/recommendations")
    public ResponseEntity<List<Recommendation>> getRecommendations(
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> brand
    ) {
        List<Recommendation> list = productService.getRecommendations(category, brand);
        return ResponseEntity.ok(list);
    }
}