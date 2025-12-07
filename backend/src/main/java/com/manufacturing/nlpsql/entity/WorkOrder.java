package com.manufacturing.nlpsql.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String workOrderNumber;

    @ManyToOne
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;

    @Column(nullable = false)
    private String workstation;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private Integer plannedHours;

    private Integer actualHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status;

    private String assignedTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum WorkStatus {
        PENDING, IN_PROGRESS, COMPLETED, ON_HOLD
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
