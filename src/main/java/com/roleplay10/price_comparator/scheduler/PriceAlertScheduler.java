package com.roleplay10.price_comparator.scheduler;

import com.roleplay10.price_comparator.domain.entity.Discount;
import com.roleplay10.price_comparator.domain.entity.PriceAlert;
import com.roleplay10.price_comparator.repository.DiscountRepository;
import com.roleplay10.price_comparator.repository.PriceAlertRepository;
import com.roleplay10.price_comparator.repository.StorePriceRepository;
import com.roleplay10.price_comparator.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;

@Component
@RequiredArgsConstructor
public class PriceAlertScheduler {
    private final PriceAlertRepository    alertRepo;
    private final StorePriceRepository  storeRepo;
    private final DiscountRepository discountRepo;
    private final NotificationService     notifier;

    @Scheduled(fixedRateString = "${alerts.check.interval}")
    @Transactional
    public void checkAlerts() {
        Instant now = Instant.now();
        for (PriceAlert a : alertRepo.findByActiveTrue()) {
            String pid = a.getProduct().getId();
            BigDecimal bestPrice = storeRepo.findByProductId(pid).stream()
                    .map(sp -> {
                        BigDecimal price = sp.getPrice();
                        Discount disc = discountRepo
                                .findActiveForStoreAndProduct(sp.getStore().getId(), pid, now)
                                .orElse(null);
                        if (disc != null) {
                            BigDecimal pct = BigDecimal.valueOf(disc.getPercentage())
                                    .divide(BigDecimal.valueOf(100));
                            price = price.multiply(BigDecimal.ONE.subtract(pct));
                        }
                        return price;
                    })
                    .min(Comparator.naturalOrder())
                    .orElse(null);

            if (bestPrice != null && bestPrice.compareTo(a.getTargetPrice()) <= 0) {
                notifier.notifyTargetReached(a, bestPrice);
                a.setActive(false);
                alertRepo.save(a);
            }
        }
    }
}