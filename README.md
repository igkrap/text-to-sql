# 🏭 제조 NLP SQL 질의 시스템 (로컬 LLM + MyBatis)

Spring Boot, React, PostgreSQL, **로컬 LLM(Ollama)**, **MyBatis**를 이용한 자연어 기반 제조 데이터베이스 질의 시스템

## 📋 프로젝트 개요

이 프로젝트는 **로컬에서 실행되는 LLM(Ollama)**을 사용하여 제조 환경에서 사용자가 자연어로 데이터베이스를 질의할 수 있는 시스템입니다. 클라우드 API 없이 완전히 로컬 환경에서 동작하며, MyBatis를 사용하여 데이터베이스를 관리합니다.

### 주요 기능

- ✅ **로컬 LLM (Ollama)** 기반 자연어를 SQL로 자동 변환
- ✅ **MyBatis** 기반 데이터베이스 매핑
- ✅ 제조 도메인 특화 (제품, 재고, 생산오더, 작업오더)
- ✅ 실시간 쿼리 실행 및 결과 표시
- ✅ 생성된 SQL 쿼리 확인 가능
- ✅ Ollama 연결 상태 실시간 모니터링
- ✅ 완전한 로컬 실행 (인터넷 불필요)

### 기술 스택

**백엔드:**
- Spring Boot 3.2.0
- Java 17
- **MyBatis 3.0.3** (SQL 매퍼)
- PostgreSQL
- **Ollama** (로컬 LLM)
- OkHttp (Ollama API 통신)
- Maven

**프론트엔드:**
- React 18
- Vite
- Axios
- Modern CSS

**AI/LLM:**
- Ollama (llama3.2:3b 기본 모델)
- 완전한 로컬 실행

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- Node.js 16 이상
- PostgreSQL 12 이상
- **Ollama** (로컬 LLM 서버)
- Maven 3.6 이상

### 1. Ollama 설치 및 모델 다운로드

#### macOS / Linux:
```bash
# Ollama 설치
curl -fsSL https://ollama.com/install.sh | sh

# Ollama 서비스 시작
ollama serve

# 새 터미널에서 모델 다운로드
ollama pull llama3.2:3b
```

#### Windows:
1. https://ollama.com/download 에서 Ollama 설치
2. 설치 후 자동으로 서비스 시작됨
3. PowerShell에서 모델 다운로드:
```powershell
ollama pull llama3.2:3b
```

#### Docker로 Ollama 실행:
```bash
docker run -d -p 11434:11434 --name ollama ollama/ollama
docker exec -it ollama ollama pull llama3.2:3b
```

### 2. PostgreSQL 데이터베이스 설정

```bash
# PostgreSQL 접속
psql -U postgres

# 데이터베이스 생성
CREATE DATABASE manufacturing_db;
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

### 3. 백엔드 실행

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

### 4. 프론트엔드 실행

```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

## 🐳 Docker Compose로 전체 스택 실행 (권장)

가장 쉬운 방법입니다:

```bash
# 모든 서비스 시작 (PostgreSQL + Ollama + Backend + Frontend)
docker-compose up -d

# Ollama 모델 다운로드 (최초 1회)
docker exec -it manufacturing-ollama ollama pull llama3.2:3b

# 로그 확인
docker-compose logs -f

# 브라우저에서 http://localhost:3000 접속
```

서비스 중지:
```bash
docker-compose down
```

## 🎯 사용 방법

### 지원되는 자연어 쿼리 예제

#### 기본 조회
- "모든 제품 보여줘"
- "재고 전체 조회"
- "생산오더 목록"

#### 조건부 조회
- "재고가 부족한 제품은?"
- "완료된 생산오더 보여줘"
- "진행중인 작업오더는?"
- "카테고리가 전자부품인 제품 조회"
- "A창고에 있는 재고 보여줘"

#### 집계 쿼리
- "제품 개수는?"
- "완료된 생산오더 개수는?"
- "재고 수량 합계는?"
- "제품 가격 평균은?"
- "카테고리별 제품 개수"

#### 정렬 및 제한
- "재고 수량이 많은 순으로 10개 보여줘"
- "가격이 높은 제품 5개"

## 📊 데이터베이스 스키마 (MyBatis)

### Products (제품)
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    unit_price DECIMAL(10, 2),
    unit VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Inventory (재고)
```sql
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id),
    warehouse VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    min_quantity INTEGER,
    max_quantity INTEGER,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Production_Orders (생산오더)
```sql
CREATE TABLE production_orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date DATE,
    due_date DATE,
    completed_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Work_Orders (작업오더)
```sql
CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    work_order_number VARCHAR(50) UNIQUE NOT NULL,
    production_order_id BIGINT NOT NULL REFERENCES production_orders(id),
    workstation VARCHAR(100) NOT NULL,
    operation VARCHAR(200) NOT NULL,
    planned_hours INTEGER NOT NULL,
    actual_hours INTEGER,
    status VARCHAR(20) NOT NULL,
    assigned_to VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 설정 변경

### Ollama 모델 변경

`backend/src/main/resources/application.yml`:
```yaml
ollama:
  base-url: http://localhost:11434
  model: llama3.2:3b  # 다른 모델로 변경 가능
  timeout: 120
```

사용 가능한 모델:
- `llama3.2:1b` - 가장 빠름, 작은 메모리
- `llama3.2:3b` - 균형잡힌 성능 (기본값, 권장)
- `llama3.1:8b` - 더 높은 정확도
- `codellama:7b` - 코드/SQL 특화
- `mistral:7b` - 대안 모델

모델 변경 후:
```bash
ollama pull <모델명>
```

### 데이터베이스 연결 설정

`backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/manufacturing_db
    username: postgres
    password: postgres
```

### MyBatis 설정

`backend/src/main/resources/application.yml`:
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.manufacturing.nlpsql.model
  configuration:
    map-underscore-to-camel-case: true
```

## 🧪 API 엔드포인트

### POST /api/query/execute
자연어 쿼리 실행 (LLM 사용)

**Request:**
```json
{
  "naturalLanguageQuery": "재고가 부족한 제품 보여줘"
}
```

**Response:**
```json
{
  "naturalLanguageQuery": "재고가 부족한 제품 보여줘",
  "generatedSql": "SELECT * FROM inventory LEFT JOIN products ON inventory.product_id = products.id WHERE inventory.quantity < inventory.min_quantity",
  "results": [...],
  "rowCount": 1,
  "executionTime": "234ms"
}
```

### GET /api/query/examples
예제 쿼리 목록 조회

### GET /api/query/status
Ollama 연결 상태 확인

**Response:**
```json
{
  "ollama_available": true,
  "status": "ready",
  "message": "로컬 LLM(Ollama)이 정상적으로 연결되었습니다."
}
```

## 💡 로컬 LLM 작동 원리

1. **사용자 입력**: "재고가 부족한 제품은?"
2. **프롬프트 생성**: 데이터베이스 스키마 정보 + 자연어 질의를 포함한 프롬프트
3. **Ollama API 호출**: 로컬 LLM이 SQL 생성
4. **SQL 검증**: 보안 검증 (SELECT만 허용)
5. **쿼리 실행**: MyBatis를 통해 PostgreSQL 실행
6. **결과 반환**: JSON 형태로 프론트엔드에 전달

## 🔒 보안 기능

- ✅ SELECT 쿼리만 허용
- ✅ 위험한 SQL 명령어 차단 (DROP, DELETE, UPDATE, INSERT 등)
- ✅ SQL 인젝션 방지
- ✅ 완전한 로컬 실행 (외부 API 호출 없음)

## 🛠️ 개발

### 백엔드 개발
```bash
cd backend
./mvnw spring-boot:run
```

주요 파일:
- `OllamaService.java` - Ollama API 통신
- `LlmNlpToSqlService.java` - LLM 기반 SQL 생성
- `mapper/*.xml` - MyBatis SQL 매핑
- `model/*.java` - 데이터 모델

### 프론트엔드 개발
```bash
cd frontend
npm run dev
```

Vite의 HMR로 빠른 개발이 가능합니다.

## 📦 프로덕션 빌드

```bash
# Docker Compose로 전체 빌드
docker-compose up --build -d
```

## 🎓 학습 자료

- [Ollama 공식 문서](https://ollama.com/)
- [MyBatis 공식 문서](https://mybatis.org/mybatis-3/)
- [Spring Boot 가이드](https://spring.io/guides)

## 🐛 문제 해결

### Ollama 연결 오류
```bash
# Ollama 상태 확인
curl http://localhost:11434/api/tags

# Ollama 재시작
ollama serve
```

### 모델 다운로드 문제
```bash
# 모델 확인
ollama list

# 모델 재다운로드
ollama pull llama3.2:3b
```

### PostgreSQL 연결 오류
```bash
# PostgreSQL 상태 확인
docker ps | grep postgres

# 로그 확인
docker logs manufacturing-postgres
```

## 📝 라이선스

MIT License

## 👥 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.
