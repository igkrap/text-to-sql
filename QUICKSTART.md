# 🚀 빠른 시작 가이드

## 옵션 1: Docker Compose로 한번에 실행 (권장)

가장 쉬운 방법입니다. Docker만 설치되어 있으면 됩니다.

```bash
# 프로젝트 디렉토리로 이동
cd text-to-sql

# 모든 서비스 시작 (PostgreSQL + Backend + Frontend)
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 브라우저에서 열기
# http://localhost:3000
```

서비스 중지:
```bash
docker-compose down
```

완전히 삭제 (데이터 포함):
```bash
docker-compose down -v
```

## 옵션 2: 로컬에서 직접 실행

### 1단계: PostgreSQL 시작

**Docker로 PostgreSQL만 실행:**
```bash
docker run --name postgres-mfg \
  -e POSTGRES_DB=manufacturing_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

**또는 로컬 PostgreSQL 사용:**
```sql
CREATE DATABASE manufacturing_db;
```

### 2단계: 백엔드 실행

**터미널 1:**
```bash
cd backend
./mvnw spring-boot:run
```

또는 Windows에서:
```bash
cd backend
mvnw.cmd spring-boot:run
```

백엔드가 `http://localhost:8080`에서 시작됩니다.

### 3단계: 프론트엔드 실행

**터미널 2:**
```bash
cd frontend
npm install
npm run dev
```

프론트엔드가 `http://localhost:3000`에서 시작됩니다.

## 테스트해보기

브라우저에서 `http://localhost:3000`을 열고 다음 쿼리를 시도해보세요:

1. **"모든 제품 보여줘"** - 전체 제품 목록 조회
2. **"재고가 부족한 제품은?"** - 최소 재고량 이하의 제품 찾기
3. **"완료된 생산오더 개수는?"** - 완료된 오더 수 계산
4. **"카테고리별 제품 개수"** - 카테고리별 집계
5. **"재고 수량 내림차순으로 10개만 보여줘"** - 정렬 및 제한

## 문제 해결

### 포트가 이미 사용중인 경우

**백엔드 포트 변경 (8080 → 8081):**
`backend/src/main/resources/application.yml`:
```yaml
server:
  port: 8081
```

**프론트엔드 포트 변경 (3000 → 3001):**
`frontend/vite.config.js`:
```javascript
server: {
  port: 3001
}
```

### PostgreSQL 연결 오류

1. PostgreSQL이 실행중인지 확인:
   ```bash
   docker ps | grep postgres
   ```

2. 연결 정보가 맞는지 확인:
   - Host: localhost
   - Port: 5432
   - Database: manufacturing_db
   - Username: postgres
   - Password: postgres

### Maven 빌드 오류

```bash
cd backend
./mvnw clean install -U
```

### npm 설치 오류

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

## 다음 단계

- [전체 README](README.md)에서 상세한 사용법 확인
- 예제 쿼리로 다양한 기능 테스트
- 자신만의 자연어 쿼리 패턴 만들기
- NLP 변환 규칙 커스터마이징

## 개발 모드

### 백엔드 핫 리로드
Spring Boot DevTools가 포함되어 있어 코드 변경 시 자동 재시작됩니다.

### 프론트엔드 핫 리로드
Vite의 HMR로 코드 변경 시 즉시 반영됩니다.

### API 테스트

**curl로 테스트:**
```bash
curl -X POST http://localhost:8080/api/query/execute \
  -H "Content-Type: application/json" \
  -d '{"naturalLanguageQuery": "모든 제품 보여줘"}'
```

**예제 쿼리 목록 조회:**
```bash
curl http://localhost:8080/api/query/examples
```

즐거운 개발 되세요! 🎉
