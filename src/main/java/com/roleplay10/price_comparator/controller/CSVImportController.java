package com.roleplay10.price_comparator.controller;

import com.roleplay10.price_comparator.service.csv.CSVImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class CSVImportController {
    private final CSVImportService importService;

    @PostMapping(value = "/prices", consumes = "multipart/form-data")
    public ResponseEntity<?> importPrices(@RequestParam("files") MultipartFile[] files) {
        try {
            importService.importPriceCSVs(files);
            return ResponseEntity.ok("Imported " + files.length + " file(s) successfully.");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Import failed: " + e.getMessage());
        }
    }
    @PostMapping(value = "/discounts", consumes = "multipart/form-data")
    public ResponseEntity<?> importDiscounts(@RequestParam("files") MultipartFile[] files) {
        try {
            importService.importDiscountCSVs(files);
            return ResponseEntity.ok("Imported " + files.length + " discount file(s) successfully.");
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Discount import failed: " + e.getMessage());
        }
    }
}