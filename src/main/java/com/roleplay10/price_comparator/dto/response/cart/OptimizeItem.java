package com.roleplay10.price_comparator.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OptimizeItem {
    private String productName;
    private BigDecimal unitPrice;
}
