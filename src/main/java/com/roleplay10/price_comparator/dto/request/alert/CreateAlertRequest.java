package com.roleplay10.price_comparator.dto.request.alert;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;


import java.math.BigDecimal;

@Data
public class CreateAlertRequest {
    @NotNull
    private String productId;
    @NotNull
    private BigDecimal targetPrice;
}
