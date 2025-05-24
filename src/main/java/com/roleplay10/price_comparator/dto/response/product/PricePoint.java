package com.roleplay10.price_comparator.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PricePoint {
    private LocalDate date;
    private String store;
    private BigDecimal price;
}
