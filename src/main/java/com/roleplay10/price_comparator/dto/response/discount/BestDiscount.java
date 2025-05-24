package com.roleplay10.price_comparator.dto.response.discount;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestDiscount {
    private String productName;
    private String storeName;
    private int percentage;
    private String validFrom;
    private String validTo;
}