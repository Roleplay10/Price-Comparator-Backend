package com.roleplay10.price_comparator.controller;

import com.roleplay10.price_comparator.dto.request.alert.CreateAlertRequest;
import com.roleplay10.price_comparator.dto.response.alert.AlertResponse;
import com.roleplay10.price_comparator.service.alert.PriceAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class PriceAlertController {
    private final PriceAlertService alertService;

    @PostMapping
    public ResponseEntity<AlertResponse> create(
            Principal principal,
            @RequestBody CreateAlertRequest req) {
        var dto = alertService.createAlert(principal.getName(), req);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> list(Principal principal) {
        return ResponseEntity.ok(
                alertService.listAlerts(principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> disable(
            Principal principal,
            @PathVariable Long id) {
        alertService.disableAlert(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}