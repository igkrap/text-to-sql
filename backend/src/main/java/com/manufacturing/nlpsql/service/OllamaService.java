package com.manufacturing.nlpsql.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manufacturing.nlpsql.config.OllamaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OllamaService {

    private final OllamaConfig ollamaConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Ollama API를 통해 자연어를 SQL로 변환
     */
    public String generateSql(String naturalLanguageQuery, String schemaInfo) {
        String prompt = buildPrompt(naturalLanguageQuery, schemaInfo);

        try {
            String response = callOllamaApi(prompt);
            return extractSqlFromResponse(response);
        } catch (Exception e) {
            log.error("Ollama API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("LLM을 통한 SQL 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 프롬프트 생성
     */
    private String buildPrompt(String query, String schemaInfo) {
        return String.format("""
            당신은 PostgreSQL 데이터베이스 전문가입니다. 제조 시스템의 자연어 질의를 SQL로 변환하는 작업을 수행합니다.

            데이터베이스 스키마:
            %s

            사용자 질의: %s

            규칙:
            1. SELECT 문만 생성하세요 (INSERT, UPDATE, DELETE 금지)
            2. 유효한 PostgreSQL SQL 문법을 사용하세요
            3. 테이블 조인이 필요한 경우 LEFT JOIN을 사용하세요
            4. WHERE 절에 조건을 명확히 작성하세요
            5. SQL 코드만 출력하고 설명은 포함하지 마세요
            6. SQL 코드 앞뒤에 백틱(```)이나 마크다운 형식을 사용하지 마세요

            SQL:
            """, schemaInfo, query);
    }

    /**
     * Ollama API 호출
     */
    private String callOllamaApi(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(ollamaConfig.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(ollamaConfig.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(ollamaConfig.getTimeout(), TimeUnit.SECONDS)
                .build();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", ollamaConfig.getModel());
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(ollamaConfig.getBaseUrl() + "/api/generate")
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .build();

        log.info("Ollama API 호출 - 모델: {}, URL: {}", ollamaConfig.getModel(), ollamaConfig.getBaseUrl());

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ollama API 호출 실패: " + response);
            }

            String responseBody = response.body().string();
            log.debug("Ollama 응답: {}", responseBody);

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("response").asText();
        }
    }

    /**
     * 응답에서 SQL 추출
     */
    private String extractSqlFromResponse(String response) {
        // 백틱 제거
        String sql = response.trim();
        sql = sql.replaceAll("```sql\\n?", "");
        sql = sql.replaceAll("```\\n?", "");
        sql = sql.replaceAll("^sql\\n", "");

        // 여러 SQL 문이 있는 경우 첫 번째만 추출
        if (sql.contains(";")) {
            sql = sql.substring(0, sql.indexOf(";") + 1);
        }

        sql = sql.trim();

        log.info("생성된 SQL: {}", sql);
        return sql;
    }

    /**
     * 데이터베이스 스키마 정보 생성
     */
    public String getSchemaInfo() {
        return """
            테이블 1: products (제품)
            - id: BIGINT (PK)
            - product_code: VARCHAR(50) (제품 코드)
            - product_name: VARCHAR(200) (제품명)
            - category: VARCHAR(100) (카테고리)
            - description: TEXT (설명)
            - unit_price: DECIMAL(10,2) (단가)
            - unit: VARCHAR(20) (단위)
            - created_at: TIMESTAMP (생성일시)
            - updated_at: TIMESTAMP (수정일시)

            테이블 2: inventory (재고)
            - id: BIGINT (PK)
            - product_id: BIGINT (FK -> products.id)
            - warehouse: VARCHAR(100) (창고명)
            - quantity: INTEGER (수량)
            - min_quantity: INTEGER (최소재고량)
            - max_quantity: INTEGER (최대재고량)
            - last_updated: TIMESTAMP (최종수정일시)

            테이블 3: production_orders (생산오더)
            - id: BIGINT (PK)
            - order_number: VARCHAR(50) (오더번호)
            - product_id: BIGINT (FK -> products.id)
            - quantity: INTEGER (수량)
            - status: VARCHAR(20) (상태: PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
            - start_date: DATE (시작일)
            - due_date: DATE (마감일)
            - completed_date: DATE (완료일)
            - created_at: TIMESTAMP (생성일시)
            - updated_at: TIMESTAMP (수정일시)

            테이블 4: work_orders (작업오더)
            - id: BIGINT (PK)
            - work_order_number: VARCHAR(50) (작업오더번호)
            - production_order_id: BIGINT (FK -> production_orders.id)
            - workstation: VARCHAR(100) (작업장)
            - operation: VARCHAR(200) (작업내용)
            - planned_hours: INTEGER (계획시간)
            - actual_hours: INTEGER (실제시간)
            - status: VARCHAR(20) (상태: PENDING, IN_PROGRESS, COMPLETED, ON_HOLD)
            - assigned_to: VARCHAR(100) (담당자)
            - created_at: TIMESTAMP (생성일시)
            - updated_at: TIMESTAMP (수정일시)
            """;
    }

    /**
     * Ollama 서버 연결 확인
     */
    public boolean checkConnection() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(ollamaConfig.getBaseUrl() + "/api/tags")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            log.error("Ollama 연결 확인 실패: {}", e.getMessage());
            return false;
        }
    }
}
