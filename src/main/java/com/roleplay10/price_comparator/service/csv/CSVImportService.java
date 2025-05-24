package com.roleplay10.price_comparator.service.csv;

import org.springframework.web.multipart.MultipartFile;

public interface CSVImportService {
    void importPriceCSVs(MultipartFile[] files) throws Exception;
    void importDiscountCSVs(MultipartFile[] files) throws Exception;
}
