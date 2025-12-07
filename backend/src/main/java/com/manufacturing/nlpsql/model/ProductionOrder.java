package com.manufacturing.nlpsql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrder {
    private Long id;
    private String orderNumber;
    private Long productId;
    private Integer quantity;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Join용 필드
    private String productName;
    private String productCode;
}
