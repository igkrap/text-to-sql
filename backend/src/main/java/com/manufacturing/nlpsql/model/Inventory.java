package com.manufacturing.nlpsql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Long id;
    private Long productId;
    private String warehouse;
    private Integer quantity;
    private Integer minQuantity;
    private Integer maxQuantity;
    private LocalDateTime lastUpdated;

    // Join용 필드
    private String productName;
    private String productCode;
}
