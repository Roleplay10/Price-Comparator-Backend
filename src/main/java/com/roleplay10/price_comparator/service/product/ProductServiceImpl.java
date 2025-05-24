package com.roleplay10.price_comparator.service.product;

import com.roleplay10.price_comparator.domain.entity.Product;
import com.roleplay10.price_comparator.domain.entity.StorePrice;
import com.roleplay10.price_comparator.dto.response.product.PricePoint;
import com.roleplay10.price_comparator.dto.response.recommendation.Recommendation;
import com.roleplay10.price_comparator.repository.ProductRepository;
import com.roleplay10.price_comparator.repository.StorePriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final StorePriceRepository priceRepo;
    private final ProductRepository productRepo;

    @Override
    @Transactional(readOnly = true)
    public List<PricePoint> getHistory(
            String productId,
            Optional<String> store,
            Optional<String> category,
            Optional<String> brand
    ) {
        var p = productRepo.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productId));
        category.ifPresent(c -> {
            if (!c.equalsIgnoreCase(p.getCategory()))
                throw new NoSuchElementException("No data for category " + c);
        });
        brand.ifPresent(b -> {
            if (!b.equalsIgnoreCase(p.getBrand()))
                throw new NoSuchElementException("No data for brand " + b);
        });

        return priceRepo.findByProductId(productId).stream()
                .filter(sp -> store.map(s ->
                        s.equalsIgnoreCase(sp.getStore().getName())
                ).orElse(true))
                .sorted(Comparator.comparing(StorePrice::getEffectiveAt))
                .map(sp -> new PricePoint(
                        sp.getEffectiveAt().atZone(ZoneOffset.UTC).toLocalDate(),
                        sp.getStore().getName(),
                        sp.getPrice()
                ))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<Recommendation> getRecommendations(Optional<String> category,
                                                   Optional<String> brand) {

        List<Product> products = productRepo.findAll().stream()
                .filter(p -> category.map(c -> c.equalsIgnoreCase(p.getCategory())).orElse(true))
                .filter(p -> brand   .map(b -> b.equalsIgnoreCase(p.getBrand()))   .orElse(true))
                .toList();


        List<Recommendation> recs = new ArrayList<>();
        for (Product p : products) {
            String pid = p.getId();
            var latest = priceRepo.findLatestPerStore(pid);
            if (latest.isEmpty()) continue;

            var best = latest.stream()
                    .min(Comparator.comparing(sp ->
                            sp.getPrice().divide(p.getPackageQty(), 8, RoundingMode.HALF_UP)))
                    .get();
            BigDecimal unitPrice = best.getPrice()
                    .divide(p.getPackageQty(), 8, RoundingMode.HALF_UP);

            recs.add(new Recommendation(
                    p.getName(),
                    best.getStore().getName(),
                    unitPrice,
                    p.getPackageQty(),
                    p.getPackageUnit()
            ));
        }
        return recs.stream()
                .sorted(Comparator.comparing(Recommendation::getUnitPrice))
                .collect(Collectors.toList());
    }
}
