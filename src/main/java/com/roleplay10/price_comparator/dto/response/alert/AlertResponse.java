package com.roleplay10.price_comparator.dto.response.alert;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlertResponse {
    private Long id;
    private String productId;
    private String productName;
    private BigDecimal targetPrice;
    private Boolean isActive;
}
