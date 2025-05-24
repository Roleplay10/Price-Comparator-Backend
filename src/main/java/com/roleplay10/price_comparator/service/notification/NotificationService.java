package com.roleplay10.price_comparator.service.notification;

import com.roleplay10.price_comparator.domain.entity.PriceAlert;

import java.math.BigDecimal;

public interface NotificationService {
    void notifyTargetReached(PriceAlert alert, BigDecimal currentPrice);
}
