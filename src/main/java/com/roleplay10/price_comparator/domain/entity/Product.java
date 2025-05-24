package com.roleplay10.price_comparator.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @Column(length = 10)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    private String category;
    private String brand;

    @Column(name = "package_qty")
    private BigDecimal packageQty;

    @Column(name = "package_unit")
    private String packageUnit;
}
