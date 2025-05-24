package com.roleplay10.price_comparator.controller;

import com.roleplay10.price_comparator.dto.request.cart.AddItemRequest;
import com.roleplay10.price_comparator.dto.response.cart.CartResponse;
import com.roleplay10.price_comparator.dto.response.cart.OptimizeItem;
import com.roleplay10.price_comparator.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    // Helper to get the current username or return null if not authenticated
    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ||
                !auth.isAuthenticated() ||
                "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    @GetMapping
    public ResponseEntity<CartResponse> viewCart() {
        String user = currentUsername();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(cartService.getOrCreateCart(user));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestBody AddItemRequest req) {
        String user = currentUsername();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(cartService.addItem(user, req));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        String user = currentUsername();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(cartService.removeItem(user, itemId));
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart() {
        String user = currentUsername();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(cartService.clearCart(user));
    }
    @GetMapping("/optimize")
    public ResponseEntity<Map<String, List<OptimizeItem>>> optimizeCart() {
        String user = currentUsername();
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(cartService.optimizeCart(user));
    }
}

