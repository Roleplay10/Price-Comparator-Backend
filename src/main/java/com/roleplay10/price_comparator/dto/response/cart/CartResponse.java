package com.roleplay10.price_comparator.dto.response.cart;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CartResponse {
    private Long cartId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CartItemResponse> items;
}