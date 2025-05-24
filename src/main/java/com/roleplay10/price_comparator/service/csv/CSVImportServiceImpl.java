package com.roleplay10.price_comparator.service.csv;

import com.roleplay10.price_comparator.domain.entity.Discount;
import com.roleplay10.price_comparator.domain.entity.Product;
import com.roleplay10.price_comparator.domain.entity.Store;
import com.roleplay10.price_comparator.domain.entity.StorePrice;
import com.roleplay10.price_comparator.repository.DiscountRepository;
import com.roleplay10.price_comparator.repository.ProductRepository;
import com.roleplay10.price_comparator.repository.StorePriceRepository;
import com.roleplay10.price_comparator.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneOffset.UTC;

@Service
@RequiredArgsConstructor
public class CSVImportServiceImpl implements CSVImportService {
    private final ProductRepository productRepo;
    private final StoreRepository   storeRepo;
    private final StorePriceRepository priceRepo;
    private final DiscountRepository discountRepo;

    private static final Pattern FILENAME =
            Pattern.compile("^(.+?)_(\\d{4}-\\d{2}-\\d{2})\\.csv$");

    @Override
    @Transactional
    public void importPriceCSVs(MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            String fname = file.getOriginalFilename();
            if (fname == null) throw new IllegalArgumentException("Missing filename");
            Matcher m = FILENAME.matcher(fname.toLowerCase());
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Filename must be '{store}_{yyyy-MM-dd}.csv': " + fname);
            }
            String storeName = m.group(1);
            LocalDate date = LocalDate.parse(m.group(2));

            Store store = storeRepo.findByNameIgnoreCase(storeName)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Unknown store: " + storeName));

            try (Reader reader = new InputStreamReader(file.getInputStream(), UTF_8);
                 CSVParser parser = CSVFormat.DEFAULT
                         .withDelimiter(';')
                         .withFirstRecordAsHeader()
                         .withIgnoreEmptyLines()
                         .parse(reader)) {

                for (CSVRecord rec : parser) {
                    String pid       = rec.get("product_id").trim();
                    String name      = rec.get("product_name").trim();
                    String category  = rec.get("product_category").trim();
                    String brand     = rec.get("brand").trim();
                    BigDecimal qty   = new BigDecimal(rec.get("package_quantity").trim());
                    String unit      = rec.get("package_unit").trim();
                    BigDecimal price = new BigDecimal(rec.get("price").trim());
                    String currency  = rec.get("currency").trim();

                    Product p = productRepo.findById(pid)
                            .orElseGet(() -> Product.builder().id(pid).build());
                    p.setName(name);
                    p.setCategory(category);
                    p.setBrand(brand);
                    p.setPackageQty(qty);
                    p.setPackageUnit(unit);
                    productRepo.save(p);

                    StorePrice sp = StorePrice.builder()
                            .store(store)
                            .product(p)
                            .price(price)
                            .currency(currency)
                            .effectiveAt(date.atStartOfDay().toInstant(UTC))
                            .build();
                    priceRepo.save(sp);
                }
            }
        }
    }
    private static final Pattern DISCOUNT_FILE =
            Pattern.compile("^(.+?)_discounts_(\\d{4}-\\d{2}-\\d{2})\\.csv$");

    @Override
    @Transactional
    public void importDiscountCSVs(MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            String fname = file.getOriginalFilename();
            if (fname == null) throw new IllegalArgumentException("Missing filename");
            var m = DISCOUNT_FILE.matcher(fname.toLowerCase());
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Filename must be '{store}_discounts_{yyyy-MM-dd}.csv': " + fname);
            }
            String storeName = m.group(1);
            LocalDate discountDate = LocalDate.parse(m.group(2));

            Store store = storeRepo.findByNameIgnoreCase(storeName)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Unknown store: " + storeName));

            try (Reader reader = new InputStreamReader(file.getInputStream(), UTF_8);
                 CSVParser parser = CSVFormat.DEFAULT
                         .withDelimiter(';')
                         .withFirstRecordAsHeader()
                         .withIgnoreEmptyLines()
                         .parse(reader)) {

                for (CSVRecord rec : parser) {
                    String pid       = rec.get("product_id").trim();
                    String name      = rec.get("product_name").trim();
                    String brand     = rec.get("brand").trim();
                    BigDecimal qty   = new BigDecimal(rec.get("package_quantity").trim());
                    String unit      = rec.get("package_unit").trim();
                    String category  = rec.get("product_category").trim();
                    LocalDate from   = LocalDate.parse(rec.get("from_date").trim());
                    LocalDate to     = LocalDate.parse(rec.get("to_date").trim());
                    int pct          = Integer.parseInt(rec.get("percentage_of_discount").trim());

                    Product p = productRepo.findById(pid)
                            .orElseGet(() -> Product.builder().id(pid).build());
                    p.setName(name);
                    p.setBrand(brand);
                    p.setCategory(category);
                    p.setPackageQty(qty);
                    p.setPackageUnit(unit);
                    productRepo.save(p);

                    Discount d = Discount.builder()
                            .store(store)
                            .product(p)
                            .percentage(pct)
                            .startAt(from.atStartOfDay().toInstant(ZoneOffset.UTC))
                            .endAt(to.atStartOfDay().toInstant(ZoneOffset.UTC))
                            .createdAt(Instant.now())
                            .build();
                    discountRepo.save(d);
                }
            }
        }
    }
}

