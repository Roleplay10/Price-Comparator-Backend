package com.roleplay10.price_comparator.controller;

import com.roleplay10.price_comparator.dto.response.discount.BestDiscount;
import com.roleplay10.price_comparator.service.discount.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping("/best")
    public ResponseEntity<List<BestDiscount>> getBestDiscounts(
            @RequestParam(defaultValue = "5") int limit) {
        List<BestDiscount> list = discountService.getBestCurrentDiscounts(limit);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/new")
    public ResponseEntity<List<BestDiscount>> getNewDiscounts() {
        return ResponseEntity.ok(discountService.getNewDiscounts());
    }
}
