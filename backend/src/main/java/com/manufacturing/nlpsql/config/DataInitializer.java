package com.manufacturing.nlpsql.config;

import com.manufacturing.nlpsql.entity.*;
import com.manufacturing.nlpsql.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final WorkOrderRepository workOrderRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("샘플 제조 데이터 초기화 시작...");

        // 제품 데이터
        Product p1 = createProduct("P001", "스마트폰 케이스", "전자부품", "내구성 강한 폴리카보네이트 케이스", "9.99", "EA");
        Product p2 = createProduct("P002", "USB 케이블", "전자부품", "고속 충전 USB-C 케이블", "5.99", "EA");
        Product p3 = createProduct("P003", "리튬 배터리", "전자부품", "3000mAh 리튬이온 배터리", "12.99", "EA");
        Product p4 = createProduct("P004", "PCB 보드", "전자부품", "다층 인쇄회로기판", "25.50", "EA");
        Product p5 = createProduct("P005", "LED 디스플레이", "디스플레이", "5인치 OLED 디스플레이", "45.00", "EA");
        Product p6 = createProduct("P006", "알루미늄 프레임", "구조부품", "경량 알루미늄 프레임", "15.75", "EA");
        Product p7 = createProduct("P007", "플라스틱 몰딩", "구조부품", "사출성형 플라스틱 부품", "8.50", "EA");
        Product p8 = createProduct("P008", "나사 세트", "조립부품", "M3 스테인리스 나사 100개", "3.99", "SET");

        List<Product> products = productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8));
        log.info("제품 {} 개 생성 완료", products.size());

        // 재고 데이터
        inventoryRepository.saveAll(List.of(
            createInventory(p1, "A창고", 150, 50, 300),
            createInventory(p2, "A창고", 200, 100, 500),
            createInventory(p3, "B창고", 45, 50, 200),  // 재고 부족
            createInventory(p4, "B창고", 80, 30, 150),
            createInventory(p5, "A창고", 25, 20, 100),
            createInventory(p6, "C창고", 120, 40, 200),
            createInventory(p7, "C창고", 180, 60, 300),
            createInventory(p8, "A창고", 500, 200, 1000)
        ));
        log.info("재고 데이터 생성 완료");

        // 생산오더 데이터
        ProductionOrder po1 = createProductionOrder("PO-2024-001", p1, 100, ProductionOrder.OrderStatus.COMPLETED, LocalDate.now().minusDays(10), LocalDate.now().minusDays(3), LocalDate.now().minusDays(2));
        ProductionOrder po2 = createProductionOrder("PO-2024-002", p2, 150, ProductionOrder.OrderStatus.IN_PROGRESS, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), null);
        ProductionOrder po3 = createProductionOrder("PO-2024-003", p3, 80, ProductionOrder.OrderStatus.PLANNED, LocalDate.now().plusDays(2), LocalDate.now().plusDays(10), null);
        ProductionOrder po4 = createProductionOrder("PO-2024-004", p5, 50, ProductionOrder.OrderStatus.IN_PROGRESS, LocalDate.now().minusDays(3), LocalDate.now().plusDays(7), null);
        ProductionOrder po5 = createProductionOrder("PO-2024-005", p6, 120, ProductionOrder.OrderStatus.COMPLETED, LocalDate.now().minusDays(15), LocalDate.now().minusDays(5), LocalDate.now().minusDays(4));

        List<ProductionOrder> orders = productionOrderRepository.saveAll(List.of(po1, po2, po3, po4, po5));
        log.info("생산오더 {} 개 생성 완료", orders.size());

        // 작업오더 데이터
        workOrderRepository.saveAll(List.of(
            createWorkOrder("WO-001", po1, "조립라인-1", "조립", 8, 7, WorkOrder.WorkStatus.COMPLETED, "김철수"),
            createWorkOrder("WO-002", po1, "검사라인", "품질검사", 2, 2, WorkOrder.WorkStatus.COMPLETED, "이영희"),
            createWorkOrder("WO-003", po2, "조립라인-2", "조립", 10, 5, WorkOrder.WorkStatus.IN_PROGRESS, "박민수"),
            createWorkOrder("WO-004", po2, "검사라인", "품질검사", 3, null, WorkOrder.WorkStatus.PENDING, null),
            createWorkOrder("WO-005", po4, "조립라인-1", "조립", 12, 8, WorkOrder.WorkStatus.IN_PROGRESS, "최지훈"),
            createWorkOrder("WO-006", po5, "포장라인", "포장", 4, 4, WorkOrder.WorkStatus.COMPLETED, "정수연")
        ));
        log.info("작업오더 데이터 생성 완료");

        log.info("샘플 제조 데이터 초기화 완료!");
    }

    private Product createProduct(String code, String name, String category, String desc, String price, String unit) {
        Product product = new Product();
        product.setProductCode(code);
        product.setProductName(name);
        product.setCategory(category);
        product.setDescription(desc);
        product.setUnitPrice(new BigDecimal(price));
        product.setUnit(unit);
        return product;
    }

    private Inventory createInventory(Product product, String warehouse, int quantity, int min, int max) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(quantity);
        inventory.setMinQuantity(min);
        inventory.setMaxQuantity(max);
        return inventory;
    }

    private ProductionOrder createProductionOrder(String orderNum, Product product, int quantity,
                                                  ProductionOrder.OrderStatus status, LocalDate start, LocalDate due, LocalDate completed) {
        ProductionOrder order = new ProductionOrder();
        order.setOrderNumber(orderNum);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus(status);
        order.setStartDate(start);
        order.setDueDate(due);
        order.setCompletedDate(completed);
        return order;
    }

    private WorkOrder createWorkOrder(String woNum, ProductionOrder po, String station, String operation,
                                     int planned, Integer actual, WorkOrder.WorkStatus status, String assigned) {
        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderNumber(woNum);
        wo.setProductionOrder(po);
        wo.setWorkstation(station);
        wo.setOperation(operation);
        wo.setPlannedHours(planned);
        wo.setActualHours(actual);
        wo.setStatus(status);
        wo.setAssignedTo(assigned);
        return wo;
    }
}
