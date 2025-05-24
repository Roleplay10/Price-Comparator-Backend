package com.roleplay10.price_comparator.dto.response.cart;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private String productId;
    private BigDecimal quantity;
}
