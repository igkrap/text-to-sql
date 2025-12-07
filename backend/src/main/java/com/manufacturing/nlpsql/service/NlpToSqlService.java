package com.manufacturing.nlpsql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class NlpToSqlService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 자연어 쿼리를 SQL로 변환
     */
    public String convertToSql(String naturalLanguageQuery) {
        String query = naturalLanguageQuery.toLowerCase().trim();

        // 테이블 식별
        String table = identifyTable(query);

        // SELECT 절 생성
        String selectClause = buildSelectClause(query, table);

        // FROM 절
        String fromClause = " FROM " + table;

        // JOIN 절 (필요한 경우)
        String joinClause = buildJoinClause(query, table);

        // WHERE 절 생성
        String whereClause = buildWhereClause(query, table);

        // GROUP BY 절
        String groupByClause = buildGroupByClause(query);

        // ORDER BY 절
        String orderByClause = buildOrderByClause(query);

        // LIMIT 절
        String limitClause = buildLimitClause(query);

        return selectClause + fromClause + joinClause + whereClause +
               groupByClause + orderByClause + limitClause;
    }

    /**
     * 쿼리에서 대상 테이블 식별
     */
    private String identifyTable(String query) {
        if (query.contains("제품") || query.contains("product")) {
            return "products";
        } else if (query.contains("재고") || query.contains("inventory")) {
            return "inventory";
        } else if (query.contains("생산") || query.contains("생산오더") ||
                   query.contains("production") || query.contains("order")) {
            return "production_orders";
        } else if (query.contains("작업") || query.contains("work")) {
            return "work_orders";
        }
        return "products"; // 기본값
    }

    /**
     * SELECT 절 생성
     */
    private String buildSelectClause(String query, String table) {
        // 집계 함수 확인
        if (query.contains("개수") || query.contains("count") || query.contains("몇 개")) {
            return "SELECT COUNT(*)";
        } else if (query.contains("합계") || query.contains("total") || query.contains("sum")) {
            if (query.contains("수량") || query.contains("quantity")) {
                return "SELECT SUM(quantity)";
            } else if (query.contains("가격") || query.contains("price")) {
                return "SELECT SUM(unit_price)";
            }
        } else if (query.contains("평균") || query.contains("average") || query.contains("avg")) {
            if (query.contains("수량") || query.contains("quantity")) {
                return "SELECT AVG(quantity)";
            } else if (query.contains("가격") || query.contains("price")) {
                return "SELECT AVG(unit_price)";
            }
        } else if (query.contains("최대") || query.contains("max")) {
            if (query.contains("수량") || query.contains("quantity")) {
                return "SELECT MAX(quantity)";
            } else if (query.contains("가격") || query.contains("price")) {
                return "SELECT MAX(unit_price)";
            }
        } else if (query.contains("최소") || query.contains("min")) {
            if (query.contains("수량") || query.contains("quantity")) {
                return "SELECT MIN(quantity)";
            } else if (query.contains("가격") || query.contains("price")) {
                return "SELECT MIN(unit_price)";
            }
        }

        // 기본 SELECT *
        return "SELECT *";
    }

    /**
     * JOIN 절 생성
     */
    private String buildJoinClause(String query, String table) {
        StringBuilder join = new StringBuilder();

        if (table.equals("inventory")) {
            join.append(" LEFT JOIN products ON inventory.product_id = products.id");
        } else if (table.equals("production_orders")) {
            join.append(" LEFT JOIN products ON production_orders.product_id = products.id");
        } else if (table.equals("work_orders")) {
            join.append(" LEFT JOIN production_orders ON work_orders.production_order_id = production_orders.id");
            if (query.contains("제품") || query.contains("product")) {
                join.append(" LEFT JOIN products ON production_orders.product_id = products.id");
            }
        }

        return join.toString();
    }

    /**
     * WHERE 절 생성
     */
    private String buildWhereClause(String query, String table) {
        List<String> conditions = new ArrayList<>();

        // 상태 필터
        if (query.contains("완료") || query.contains("completed")) {
            if (table.equals("production_orders")) {
                conditions.add("production_orders.status = 'COMPLETED'");
            } else if (table.equals("work_orders")) {
                conditions.add("work_orders.status = 'COMPLETED'");
            }
        } else if (query.contains("진행중") || query.contains("in progress")) {
            if (table.equals("production_orders")) {
                conditions.add("production_orders.status = 'IN_PROGRESS'");
            } else if (table.equals("work_orders")) {
                conditions.add("work_orders.status = 'IN_PROGRESS'");
            }
        } else if (query.contains("계획") || query.contains("planned")) {
            if (table.equals("production_orders")) {
                conditions.add("production_orders.status = 'PLANNED'");
            }
        }

        // 카테고리 필터
        Pattern categoryPattern = Pattern.compile("카테고리[가가]?\\s*['\"]?([^'\"\\s]+)['\"]?");
        Matcher categoryMatcher = categoryPattern.matcher(query);
        if (categoryMatcher.find()) {
            String category = categoryMatcher.group(1);
            conditions.add("products.category = '" + category + "'");
        }

        // 창고 필터
        Pattern warehousePattern = Pattern.compile("창고[가가]?\\s*['\"]?([^'\"\\s]+)['\"]?");
        Matcher warehouseMatcher = warehousePattern.matcher(query);
        if (warehouseMatcher.find()) {
            String warehouse = warehouseMatcher.group(1);
            conditions.add("inventory.warehouse = '" + warehouse + "'");
        }

        // 수량 조건
        Pattern quantityPattern = Pattern.compile("수량[이가]?\\s*(>=?|<=?|=)\\s*(\\d+)");
        Matcher quantityMatcher = quantityPattern.matcher(query);
        if (quantityMatcher.find()) {
            String operator = quantityMatcher.group(1);
            String value = quantityMatcher.group(2);
            conditions.add(table + ".quantity " + operator + " " + value);
        } else if (query.contains("부족") || query.contains("low stock")) {
            if (table.equals("inventory")) {
                conditions.add("inventory.quantity < inventory.min_quantity");
            }
        }

        // 날짜 조건
        if (query.contains("오늘") || query.contains("today")) {
            conditions.add("DATE(" + table + ".created_at) = CURRENT_DATE");
        } else if (query.contains("이번주") || query.contains("this week")) {
            conditions.add(table + ".created_at >= DATE_TRUNC('week', CURRENT_DATE)");
        } else if (query.contains("이번달") || query.contains("this month")) {
            conditions.add(table + ".created_at >= DATE_TRUNC('month', CURRENT_DATE)");
        }

        if (conditions.isEmpty()) {
            return "";
        }

        return " WHERE " + String.join(" AND ", conditions);
    }

    /**
     * GROUP BY 절 생성
     */
    private String buildGroupByClause(String query) {
        if (query.contains("카테고리별") || query.contains("by category")) {
            return " GROUP BY products.category";
        } else if (query.contains("창고별") || query.contains("by warehouse")) {
            return " GROUP BY inventory.warehouse";
        } else if (query.contains("상태별") || query.contains("by status")) {
            return " GROUP BY status";
        }
        return "";
    }

    /**
     * ORDER BY 절 생성
     */
    private String buildOrderByClause(String query) {
        if (query.contains("정렬") || query.contains("order") || query.contains("sort")) {
            if (query.contains("수량") || query.contains("quantity")) {
                if (query.contains("내림차순") || query.contains("desc")) {
                    return " ORDER BY quantity DESC";
                }
                return " ORDER BY quantity ASC";
            } else if (query.contains("가격") || query.contains("price")) {
                if (query.contains("내림차순") || query.contains("desc")) {
                    return " ORDER BY unit_price DESC";
                }
                return " ORDER BY unit_price ASC";
            } else if (query.contains("날짜") || query.contains("date")) {
                return " ORDER BY created_at DESC";
            }
        }
        return "";
    }

    /**
     * LIMIT 절 생성
     */
    private String buildLimitClause(String query) {
        Pattern limitPattern = Pattern.compile("(\\d+)\\s*개|top\\s*(\\d+)|limit\\s*(\\d+)");
        Matcher matcher = limitPattern.matcher(query);
        if (matcher.find()) {
            String limit = matcher.group(1) != null ? matcher.group(1) :
                          matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            return " LIMIT " + limit;
        }
        return "";
    }

    /**
     * SQL 실행 및 결과 반환
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("SQL 실행 오류: {}", e.getMessage());
            throw new RuntimeException("쿼리 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
