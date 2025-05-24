package com.roleplay10.price_comparator.service.discount;

import com.roleplay10.price_comparator.dto.response.discount.BestDiscount;

import java.util.List;

public interface DiscountService {
    List<BestDiscount> getBestCurrentDiscounts(int limit);
    List<BestDiscount> getNewDiscounts();
}