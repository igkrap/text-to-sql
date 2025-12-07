# 🏭 제조 NLP SQL 질의 시스템

Spring Boot, React, PostgreSQL을 이용한 자연어 기반 제조 데이터베이스 질의 시스템

## 📋 프로젝트 개요

이 프로젝트는 제조 환경에서 사용자가 자연어로 데이터베이스를 질의할 수 있는 시스템입니다. 복잡한 SQL을 몰라도 "재고가 부족한 제품은?" 같은 자연어 질문으로 데이터를 조회할 수 있습니다.

### 주요 기능

- ✅ 자연어를 SQL로 자동 변환
- ✅ 제조 도메인 특화 (제품, 재고, 생산오더, 작업오더)
- ✅ 실시간 쿼리 실행 및 결과 표시
- ✅ 생성된 SQL 쿼리 확인 가능
- ✅ 다양한 예제 쿼리 제공

### 기술 스택

**백엔드:**
- Spring Boot 3.2.0
- Java 17
- PostgreSQL
- JPA/Hibernate
- Maven

**프론트엔드:**
- React 18
- Vite
- Axios
- Modern CSS

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- Node.js 16 이상
- PostgreSQL 12 이상
- Maven 3.6 이상

### 1. PostgreSQL 데이터베이스 설정

```bash
# PostgreSQL 접속
psql -U postgres

# 데이터베이스 생성
CREATE DATABASE manufacturing_db;

# 사용자 생성 (선택사항)
CREATE USER dbuser WITH PASSWORD 'dbpassword';
GRANT ALL PRIVILEGES ON DATABASE manufacturing_db TO dbuser;
```

또는 Docker로 PostgreSQL 실행:

```bash
docker run --name postgres-mfg \
  -e POSTGRES_DB=manufacturing_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

### 2. 백엔드 실행

```bash
cd backend

# Maven으로 빌드 및 실행
./mvnw clean install
./mvnw spring-boot:run

# 또는 JAR 파일로 실행
./mvnw package
java -jar target/nlp-sql-query-1.0.0.jar
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

### 3. 프론트엔드 실행

```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

## 🎯 사용 방법

### 지원되는 자연어 쿼리 예제

#### 기본 조회
- "모든 제품 보여줘"
- "모든 재고 조회"
- "생산오더 전체 보여줘"

#### 조건부 조회
- "재고가 부족한 제품은?"
- "완료된 생산오더 보여줘"
- "진행중인 작업오더는?"
- "카테고리가 전자부품인 제품 조회"
- "창고가 A창고인 재고 보여줘"

#### 집계 쿼리
- "제품 개수는?"
- "완료된 생산오더 개수는?"
- "재고 수량 합계는?"
- "제품 가격 평균은?"
- "카테고리별 제품 개수"
- "상태별 생산오더 개수"

#### 정렬 및 제한
- "재고 수량 내림차순으로 정렬"
- "가격이 높은 제품 10개만 보여줘"
- "최근 생성된 생산오더 5개"

#### 날짜 조건
- "오늘 생성된 생산오더는?"
- "이번주 생성된 제품은?"
- "이번달 생산오더 조회"

## 📊 데이터베이스 스키마

### Products (제품)
- `id`: 제품 ID
- `product_code`: 제품 코드
- `product_name`: 제품명
- `category`: 카테고리
- `unit_price`: 단가
- `unit`: 단위

### Inventory (재고)
- `id`: 재고 ID
- `product_id`: 제품 ID (FK)
- `warehouse`: 창고명
- `quantity`: 수량
- `min_quantity`: 최소 재고량
- `max_quantity`: 최대 재고량

### Production_Orders (생산오더)
- `id`: 오더 ID
- `order_number`: 오더 번호
- `product_id`: 제품 ID (FK)
- `quantity`: 수량
- `status`: 상태 (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
- `start_date`: 시작일
- `due_date`: 마감일
- `completed_date`: 완료일

### Work_Orders (작업오더)
- `id`: 작업오더 ID
- `work_order_number`: 작업오더 번호
- `production_order_id`: 생산오더 ID (FK)
- `workstation`: 작업장
- `operation`: 작업
- `planned_hours`: 계획 시간
- `actual_hours`: 실제 시간
- `status`: 상태 (PENDING, IN_PROGRESS, COMPLETED, ON_HOLD)
- `assigned_to`: 담당자

## 🔧 설정 변경

### 데이터베이스 연결 설정

`backend/src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 수정할 수 있습니다:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/manufacturing_db
    username: postgres
    password: postgres
```

### 포트 변경

**백엔드 포트:** `application.yml`의 `server.port` 수정

**프론트엔드 포트:** `frontend/vite.config.js`의 `server.port` 수정

## 🐳 Docker로 실행하기

전체 스택을 Docker Compose로 실행:

```bash
docker-compose up -d
```

서비스 URL:
- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080/api
- PostgreSQL: localhost:5432

## 🧪 API 엔드포인트

### POST /api/query/execute
자연어 쿼리 실행

**Request:**
```json
{
  "naturalLanguageQuery": "모든 제품 보여줘"
}
```

**Response:**
```json
{
  "naturalLanguageQuery": "모든 제품 보여줘",
  "generatedSql": "SELECT * FROM products",
  "results": [...],
  "rowCount": 8,
  "executionTime": "45ms"
}
```

### GET /api/query/examples
예제 쿼리 목록 조회

## 📝 NLP to SQL 변환 규칙

시스템은 다음과 같은 규칙 기반 변환을 사용합니다:

1. **테이블 식별**: 키워드로 대상 테이블 결정 (제품, 재고, 생산, 작업)
2. **집계 함수**: "개수", "합계", "평균" 등의 키워드로 집계 함수 적용
3. **조건 절**: 상태, 카테고리, 수량 등의 조건 파싱
4. **정렬**: "정렬", "내림차순", "오름차순" 키워드 처리
5. **제한**: "10개", "top 5" 등의 LIMIT 절 처리

## 🛠️ 개발

### 백엔드 개발

```bash
cd backend
./mvnw spring-boot:run
```

핫 리로드가 활성화되어 있어 코드 변경 시 자동으로 재시작됩니다.

### 프론트엔드 개발

```bash
cd frontend
npm run dev
```

Vite의 HMR(Hot Module Replacement)로 빠른 개발이 가능합니다.

## 📦 프로덕션 빌드

### 백엔드
```bash
cd backend
./mvnw clean package
java -jar target/nlp-sql-query-1.0.0.jar
```

### 프론트엔드
```bash
cd frontend
npm run build
npm run preview
```

## 🤝 기여

이슈와 풀 리퀘스트를 환영합니다!

## 📄 라이선스

MIT License

## 👥 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
