package com.manufacturing.nlpsql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {
    private Long id;
    private String workOrderNumber;
    private Long productionOrderId;
    private String workstation;
    private String operation;
    private Integer plannedHours;
    private Integer actualHours;
    private String status;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Join용 필드
    private String orderNumber;
    private String productName;
}
