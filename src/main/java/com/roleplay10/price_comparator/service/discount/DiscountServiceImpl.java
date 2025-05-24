package com.roleplay10.price_comparator.service.discount;

import com.roleplay10.price_comparator.domain.entity.Discount;
import com.roleplay10.price_comparator.dto.response.discount.BestDiscount;
import com.roleplay10.price_comparator.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepo;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional(readOnly = true)
    public List<BestDiscount> getBestCurrentDiscounts(int limit) {
        Instant now = Instant.now();
        return discountRepo.findAll().stream()
                // filter currently active
                .filter(d -> !d.getStartAt().isAfter(now) && !d.getEndAt().isBefore(now))
                // sort by percentage desc
                .sorted(Comparator.comparingInt(Discount::getPercentage).reversed())
                .limit(limit)
                .map(d -> new BestDiscount(
                        d.getProduct().getName(),
                        d.getStore().getName(),
                        d.getPercentage(),
                        FMT.format(d.getStartAt().atZone(ZoneOffset.UTC).toLocalDate()),
                        FMT.format(d.getEndAt().  atZone(ZoneOffset.UTC).toLocalDate())
                ))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<BestDiscount> getNewDiscounts() {
        Instant since = Instant.now().minus(Duration.ofHours(24));
        return discountRepo.findNewSince(since).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private BestDiscount toDto(Discount d) {
        return new BestDiscount(
                d.getProduct().getName(),
                d.getStore().getName(),
                d.getPercentage(),
                FMT.format(d.getStartAt().atZone(ZoneOffset.UTC).toLocalDate()),
                FMT.format(d.getEndAt().  atZone(ZoneOffset.UTC).toLocalDate())
        );
    }
}
