# 📚 자연어 쿼리 예제 및 가이드

## 기본 조회 쿼리

### 전체 데이터 조회
```
모든 제품 보여줘
```
```sql
SELECT * FROM products
```

```
모든 재고 조회
```
```sql
SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id
```

```
생산오더 전체 보여줘
```
```sql
SELECT * FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id
```

## 조건부 조회

### 상태 기반 필터링
```
완료된 생산오더 보여줘
```
```sql
SELECT * FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id WHERE production_orders.status = 'COMPLETED'
```

```
진행중인 작업오더는?
```
```sql
SELECT * FROM work_orders LEFT JOIN production_orders ON work_orders.production_order_id = production_orders.id WHERE work_orders.status = 'IN_PROGRESS'
```

### 카테고리 필터링
```
카테고리가 전자부품인 제품 조회
```
```sql
SELECT * FROM products WHERE products.category = '전자부품'
```

### 창고 필터링
```
창고가 A창고인 재고 보여줘
```
```sql
SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id WHERE inventory.warehouse = 'A창고'
```

### 재고 부족 조회
```
재고가 부족한 제품은?
```
```sql
SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id WHERE inventory.quantity < inventory.min_quantity
```

## 집계 쿼리

### COUNT (개수)
```
제품 개수는?
```
```sql
SELECT COUNT(*) FROM products
```

```
완료된 생산오더 개수는?
```
```sql
SELECT COUNT(*) FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id WHERE production_orders.status = 'COMPLETED'
```

### SUM (합계)
```
재고 수량 합계는?
```
```sql
SELECT SUM(quantity) FROM inventory LEFT JOIN products ON inventory.product_id = products.id
```

```
제품 가격 합계
```
```sql
SELECT SUM(unit_price) FROM products
```

### AVG (평균)
```
제품 가격 평균은?
```
```sql
SELECT AVG(unit_price) FROM products
```

```
재고 수량 평균은?
```
```sql
SELECT AVG(quantity) FROM inventory LEFT JOIN products ON inventory.product_id = products.id
```

### MAX/MIN (최대/최소)
```
최대 가격 제품은?
```
```sql
SELECT MAX(unit_price) FROM products
```

```
최소 재고 수량은?
```
```sql
SELECT MIN(quantity) FROM inventory LEFT JOIN products ON inventory.product_id = products.id
```

## GROUP BY (그룹화)

```
카테고리별 제품 개수
```
```sql
SELECT COUNT(*) FROM products GROUP BY products.category
```

```
창고별 재고 개수
```
```sql
SELECT COUNT(*) FROM inventory LEFT JOIN products ON inventory.product_id = products.id GROUP BY inventory.warehouse
```

```
상태별 생산오더 개수
```
```sql
SELECT COUNT(*) FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id GROUP BY status
```

## 정렬 및 제한

### ORDER BY (정렬)
```
재고 수량 내림차순으로 정렬
```
```sql
SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id ORDER BY quantity DESC
```

```
가격 오름차순으로 제품 조회
```
```sql
SELECT * FROM products ORDER BY unit_price ASC
```

### LIMIT (제한)
```
재고 수량 내림차순으로 10개만 보여줘
```
```sql
SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id ORDER BY quantity DESC LIMIT 10
```

```
가격이 높은 제품 5개
```
```sql
SELECT * FROM products ORDER BY unit_price DESC LIMIT 5
```

## 날짜 기반 조회

```
오늘 생성된 생산오더는?
```
```sql
SELECT * FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id WHERE DATE(production_orders.created_at) = CURRENT_DATE
```

```
이번주 생성된 제품은?
```
```sql
SELECT * FROM products WHERE products.created_at >= DATE_TRUNC('week', CURRENT_DATE)
```

```
이번달 생산오더 조회
```
```sql
SELECT * FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id WHERE production_orders.created_at >= DATE_TRUNC('month', CURRENT_DATE)
```

## 복합 조건 쿼리

```
완료된 생산오더를 수량 내림차순으로 10개만
```
```sql
SELECT * FROM production_orders LEFT JOIN products ON production_orders.product_id = products.id WHERE production_orders.status = 'COMPLETED' ORDER BY quantity DESC LIMIT 10
```

```
A창고의 재고가 부족한 제품
```
```sql
SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id WHERE inventory.warehouse = 'A창고' AND inventory.quantity < inventory.min_quantity
```

## 키워드 매핑

### 테이블 식별 키워드
- **제품/product** → `products` 테이블
- **재고/inventory** → `inventory` 테이블
- **생산/생산오더/production/order** → `production_orders` 테이블
- **작업/work** → `work_orders` 테이블

### 집계 함수 키워드
- **개수/count/몇 개** → `COUNT(*)`
- **합계/total/sum** → `SUM(column)`
- **평균/average/avg** → `AVG(column)`
- **최대/max** → `MAX(column)`
- **최소/min** → `MIN(column)`

### 상태 키워드
- **완료/completed** → `status = 'COMPLETED'`
- **진행중/in progress** → `status = 'IN_PROGRESS'`
- **계획/planned** → `status = 'PLANNED'`

### 조건 키워드
- **부족/low stock** → `quantity < min_quantity`
- **카테고리가** → `category = 'value'`
- **창고가** → `warehouse = 'value'`

### 날짜 키워드
- **오늘/today** → `DATE(created_at) = CURRENT_DATE`
- **이번주/this week** → `>= DATE_TRUNC('week', CURRENT_DATE)`
- **이번달/this month** → `>= DATE_TRUNC('month', CURRENT_DATE)`

### 정렬 키워드
- **정렬/order/sort** → `ORDER BY`
- **내림차순/desc** → `DESC`
- **오름차순/asc** → `ASC`

### 제한 키워드
- **N개/top N/limit N** → `LIMIT N`

## 고급 팁

### 1. 여러 조건 결합
자연어로 여러 조건을 나열하면 AND로 결합됩니다:
```
카테고리가 전자부품이고 가격이 10보다 큰 제품
```

### 2. 그룹화와 집계
"~별"을 사용하면 GROUP BY가 적용됩니다:
```
카테고리별 평균 가격
```

### 3. 정렬과 제한 조합
정렬과 개수 제한을 함께 사용할 수 있습니다:
```
수량 많은 재고 상위 5개
```

### 4. 날짜 범위
시간 범위를 지정할 수 있습니다:
```
이번달 완료된 생산오더 개수
```

## 확장 가능성

현재 시스템은 규칙 기반으로 동작하며, 다음과 같이 확장 가능합니다:

1. **AI 모델 통합**: GPT 등의 LLM을 사용하여 더 복잡한 자연어 이해
2. **학습 기능**: 사용자의 쿼리 패턴을 학습하여 정확도 향상
3. **다국어 지원**: 영어, 일본어 등 다양한 언어 지원
4. **도메인 확장**: 제조 외 다른 도메인으로 확장
5. **시각화**: 쿼리 결과를 차트로 자동 생성

## 문제 해결

### 쿼리가 제대로 변환되지 않는 경우
1. 키워드를 명확하게 사용하세요 (예: "제품", "재고", "생산오더")
2. 조건을 구체적으로 명시하세요
3. 생성된 SQL을 확인하고 패턴을 학습하세요

### 결과가 없는 경우
1. 샘플 데이터가 로드되었는지 확인
2. 조건이 너무 제한적이지 않은지 확인
3. 테이블에 실제 데이터가 있는지 확인

## 기여하기

새로운 자연어 패턴을 추가하려면:
1. `NlpToSqlService.java` 파일 수정
2. 해당 메서드에 새로운 키워드 및 로직 추가
3. 테스트 후 PR 제출

즐거운 쿼리 되세요! 🎯
