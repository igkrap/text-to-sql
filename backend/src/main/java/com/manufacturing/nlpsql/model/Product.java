package com.manufacturing.nlpsql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;
    private String productCode;
    private String productName;
    private String category;
    private String description;
    private BigDecimal unitPrice;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
