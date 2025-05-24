package com.roleplay10.price_comparator.dto.response.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Recommendation {
    private String productName;
    private String store;
    private BigDecimal unitPrice;
    private BigDecimal packageQty;
    private String packageUnit;
}
