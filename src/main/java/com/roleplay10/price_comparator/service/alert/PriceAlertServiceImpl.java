package com.roleplay10.price_comparator.service.alert;

import com.roleplay10.price_comparator.domain.entity.AppUser;
import com.roleplay10.price_comparator.domain.entity.PriceAlert;
import com.roleplay10.price_comparator.domain.entity.Product;
import com.roleplay10.price_comparator.dto.request.alert.CreateAlertRequest;
import com.roleplay10.price_comparator.dto.response.alert.AlertResponse;
import com.roleplay10.price_comparator.repository.AppUserRepository;
import com.roleplay10.price_comparator.repository.PriceAlertRepository;
import com.roleplay10.price_comparator.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceAlertServiceImpl implements PriceAlertService {
    private final AppUserRepository userRepo;
    private final ProductRepository productRepo;
    private final PriceAlertRepository alertRepo;

    @Override
    @Transactional
    public AlertResponse createAlert(String username, CreateAlertRequest req) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Optional<PriceAlert> existing = alertRepo
                .findByUserUsernameAndProductId(username, req.getProductId());

        PriceAlert alert;
        if (existing.isPresent()) {
            alert = existing.get();
            if (Boolean.TRUE.equals(alert.getActive())) {
                throw new IllegalArgumentException(
                        "An active alert already exists for product " + req.getProductId());
            }
            alert.setActive(true);
            alert.setTargetPrice(req.getTargetPrice());
            alert.setCreatedAt(Instant.now());
        } else {
            Product product = productRepo.findById(req.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            alert = PriceAlert.builder()
                    .user(user)
                    .product(product)
                    .targetPrice(req.getTargetPrice())
                    .active(true)
                    .createdAt(Instant.now())
                    .build();
        }

        alert = alertRepo.save(alert);
        return toDto(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> listAlerts(String username) {
        return alertRepo.findByUserUsername(username).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void disableAlert(String username, Long alertId) {
        PriceAlert alert = alertRepo.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found"));
        if (!alert.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("Not your alert");
        }
        alert.setActive(false);
        alertRepo.save(alert);
    }

    private AlertResponse toDto(PriceAlert a) {
        AlertResponse r = new AlertResponse();
        r.setId(a.getId());
        r.setProductId(a.getProduct().getId());
        r.setProductName(a.getProduct().getName());
        r.setTargetPrice(a.getTargetPrice());
        r.setIsActive(a.getActive());
        return r;
    }
}