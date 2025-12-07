package com.manufacturing.nlpsql.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyBatisDataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        // 스키마 생성
        try {
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("schema.sql"));
            log.info("데이터베이스 스키마 생성 완료");
        } catch (Exception e) {
            log.warn("스키마 생성 중 오류 (이미 존재할 수 있음): {}", e.getMessage());
        }

        // 데이터가 이미 존재하는지 확인
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Integer.class);
        if (count != null && count > 0) {
            log.info("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("샘플 제조 데이터 초기화 시작...");

        // 제품 데이터 삽입
        insertProducts();

        // 재고 데이터 삽입
        insertInventory();

        // 생산오더 데이터 삽입
        insertProductionOrders();

        // 작업오더 데이터 삽입
        insertWorkOrders();

        log.info("샘플 제조 데이터 초기화 완료!");
    }

    private void insertProducts() {
        String sql = "INSERT INTO products (product_code, product_name, category, description, unit_price, unit) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, "P001", "스마트폰 케이스", "전자부품", "내구성 강한 폴리카보네이트 케이스", new BigDecimal("9.99"), "EA");
        jdbcTemplate.update(sql, "P002", "USB 케이블", "전자부품", "고속 충전 USB-C 케이블", new BigDecimal("5.99"), "EA");
        jdbcTemplate.update(sql, "P003", "리튬 배터리", "전자부품", "3000mAh 리튬이온 배터리", new BigDecimal("12.99"), "EA");
        jdbcTemplate.update(sql, "P004", "PCB 보드", "전자부품", "다층 인쇄회로기판", new BigDecimal("25.50"), "EA");
        jdbcTemplate.update(sql, "P005", "LED 디스플레이", "디스플레이", "5인치 OLED 디스플레이", new BigDecimal("45.00"), "EA");
        jdbcTemplate.update(sql, "P006", "알루미늄 프레임", "구조부품", "경량 알루미늄 프레임", new BigDecimal("15.75"), "EA");
        jdbcTemplate.update(sql, "P007", "플라스틱 몰딩", "구조부품", "사출성형 플라스틱 부품", new BigDecimal("8.50"), "EA");
        jdbcTemplate.update(sql, "P008", "나사 세트", "조립부품", "M3 스테인리스 나사 100개", new BigDecimal("3.99"), "SET");

        log.info("제품 8개 생성 완료");
    }

    private void insertInventory() {
        String sql = "INSERT INTO inventory (product_id, warehouse, quantity, min_quantity, max_quantity) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, 1L, "A창고", 150, 50, 300);
        jdbcTemplate.update(sql, 2L, "A창고", 200, 100, 500);
        jdbcTemplate.update(sql, 3L, "B창고", 45, 50, 200);  // 재고 부족
        jdbcTemplate.update(sql, 4L, "B창고", 80, 30, 150);
        jdbcTemplate.update(sql, 5L, "A창고", 25, 20, 100);
        jdbcTemplate.update(sql, 6L, "C창고", 120, 40, 200);
        jdbcTemplate.update(sql, 7L, "C창고", 180, 60, 300);
        jdbcTemplate.update(sql, 8L, "A창고", 500, 200, 1000);

        log.info("재고 데이터 생성 완료");
    }

    private void insertProductionOrders() {
        String sql = "INSERT INTO production_orders (order_number, product_id, quantity, status, start_date, due_date, completed_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        LocalDate now = LocalDate.now();

        jdbcTemplate.update(sql, "PO-2024-001", 1L, 100, "COMPLETED", now.minusDays(10), now.minusDays(3), now.minusDays(2));
        jdbcTemplate.update(sql, "PO-2024-002", 2L, 150, "IN_PROGRESS", now.minusDays(5), now.plusDays(5), null);
        jdbcTemplate.update(sql, "PO-2024-003", 3L, 80, "PLANNED", now.plusDays(2), now.plusDays(10), null);
        jdbcTemplate.update(sql, "PO-2024-004", 5L, 50, "IN_PROGRESS", now.minusDays(3), now.plusDays(7), null);
        jdbcTemplate.update(sql, "PO-2024-005", 6L, 120, "COMPLETED", now.minusDays(15), now.minusDays(5), now.minusDays(4));

        log.info("생산오더 5개 생성 완료");
    }

    private void insertWorkOrders() {
        String sql = "INSERT INTO work_orders (work_order_number, production_order_id, workstation, operation, planned_hours, actual_hours, status, assigned_to) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, "WO-001", 1L, "조립라인-1", "조립", 8, 7, "COMPLETED", "김철수");
        jdbcTemplate.update(sql, "WO-002", 1L, "검사라인", "품질검사", 2, 2, "COMPLETED", "이영희");
        jdbcTemplate.update(sql, "WO-003", 2L, "조립라인-2", "조립", 10, 5, "IN_PROGRESS", "박민수");
        jdbcTemplate.update(sql, "WO-004", 2L, "검사라인", "품질검사", 3, null, "PENDING", null);
        jdbcTemplate.update(sql, "WO-005", 4L, "조립라인-1", "조립", 12, 8, "IN_PROGRESS", "최지훈");
        jdbcTemplate.update(sql, "WO-006", 5L, "포장라인", "포장", 4, 4, "COMPLETED", "정수연");

        log.info("작업오더 데이터 생성 완료");
    }
}
