package com.roleplay10.price_comparator.service.cart;

import com.roleplay10.price_comparator.domain.entity.*;
import com.roleplay10.price_comparator.dto.request.cart.AddItemRequest;
import com.roleplay10.price_comparator.dto.response.cart.CartItemResponse;
import com.roleplay10.price_comparator.dto.response.cart.CartResponse;
import com.roleplay10.price_comparator.dto.response.cart.OptimizeItem;
import com.roleplay10.price_comparator.repository.*;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final AppUserRepository             userRepo;
    private final ProductRepository             productRepo;
    private final ShoppingCartRepository        cartRepo;
    private final ShoppingCartItemRepository    itemRepo;
    private final StorePriceRepository storePriceRepo;

    private AppUser loadUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public CartResponse getOrCreateCart(String username) {
        AppUser user = loadUser(username);
        ShoppingCart cart = cartRepo.findByUserId(user.getId())
                .orElseGet(() -> {
                    ShoppingCart c = ShoppingCart.builder()
                            .user(user)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return cartRepo.save(c);
                });
        cart.getItems().size();
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(String username, AddItemRequest req) {
        CartResponse dto = getOrCreateCart(username);
        ShoppingCart cart = cartRepo.findById(dto.getCartId())
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + req.getProductId()));
        ShoppingCartItem item = ShoppingCartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(req.getQuantity())
                .build();
        itemRepo.save(item);
        cart.getItems().add(item);
        cart.setUpdatedAt(Instant.now());
        cartRepo.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(String username, Long itemId) {
        CartResponse dto = getOrCreateCart(username);
        ShoppingCart cart = cartRepo.findById(dto.getCartId())
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
        ShoppingCartItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + itemId));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item does not belong to your cart");
        }
        cart.getItems().remove(item);
        itemRepo.delete(item);
        cart.setUpdatedAt(Instant.now());
        cartRepo.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartResponse clearCart(String username) {
        CartResponse dto = getOrCreateCart(username);
        ShoppingCart cart = cartRepo.findById(dto.getCartId())
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));
        itemRepo.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setUpdatedAt(Instant.now());
        cartRepo.save(cart);
        return toDto(cart);
    }
    private CartResponse toDto(ShoppingCart cart) {
        CartResponse resp = new CartResponse();
        resp.setCartId(cart.getId());
        resp.setCreatedAt(cart.getCreatedAt());
        resp.setUpdatedAt(cart.getUpdatedAt());
        resp.setItems(cart.getItems().stream().map(item -> {
            CartItemResponse ir = new CartItemResponse();
            ir.setId(item.getId());
            ir.setProductId(item.getProduct().getId());
            ir.setQuantity(item.getQuantity());
            return ir;
        }).toList());
        return resp;
    }
    @Override
    @Transactional(readOnly = true)
    public Map<String, List<OptimizeItem>> optimizeCart(String username) {
        AppUser user = loadUser(username);
        ShoppingCart cart = cartRepo.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("Cart not found"));

        return cart.getItems().stream()
                .map(ci -> {
                    String pid = ci.getProduct().getId();
                    var sp = storePriceRepo.findLatestPerStore(pid).stream()
                            .min(Comparator.comparing(StorePrice::getPrice))
                            .orElseThrow();
                    return Map.entry(
                            sp.getStore().getName().toLowerCase(),
                            new OptimizeItem(ci.getProduct().getName(), sp.getPrice())
                    );
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }
}
