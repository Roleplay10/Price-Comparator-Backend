package com.roleplay10.price_comparator.service.cart;

import com.roleplay10.price_comparator.dto.request.cart.AddItemRequest;
import com.roleplay10.price_comparator.dto.response.cart.CartResponse;
import com.roleplay10.price_comparator.dto.response.cart.OptimizeItem;

import java.util.List;
import java.util.Map;

public interface CartService {
    CartResponse getOrCreateCart(String username);
    CartResponse addItem(String username, AddItemRequest req);
    CartResponse removeItem(String username, Long itemId);
    CartResponse clearCart(String username);
    Map<String, List<OptimizeItem>> optimizeCart(String username);
}
