package com.manufacturing.nlpsql.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LlmNlpToSqlService {

    private final OllamaService ollamaService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 자연어 쿼리를 SQL로 변환 (LLM 사용)
     */
    public String convertToSql(String naturalLanguageQuery) {
        log.info("LLM을 사용하여 SQL 생성 중: {}", naturalLanguageQuery);

        String schemaInfo = ollamaService.getSchemaInfo();
        String sql = ollamaService.generateSql(naturalLanguageQuery, schemaInfo);

        // SQL 유효성 검사
        validateSql(sql);

        return sql;
    }

    /**
     * SQL 유효성 검사
     */
    private void validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new RuntimeException("생성된 SQL이 비어있습니다.");
        }

        String upperSql = sql.toUpperCase().trim();

        // SELECT 문만 허용
        if (!upperSql.startsWith("SELECT")) {
            throw new RuntimeException("SELECT 쿼리만 허용됩니다.");
        }

        // 위험한 키워드 차단
        String[] dangerousKeywords = {"DROP", "DELETE", "INSERT", "UPDATE", "TRUNCATE", "ALTER", "CREATE"};
        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                throw new RuntimeException("허용되지 않은 SQL 명령어가 포함되어 있습니다: " + keyword);
            }
        }
    }

    /**
     * SQL 실행 및 결과 반환
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            log.info("SQL 실행: {}", sql);
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("SQL 실행 오류: {}", e.getMessage());
            throw new RuntimeException("쿼리 실행 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * Ollama 연결 상태 확인
     */
    public boolean isOllamaAvailable() {
        return ollamaService.checkConnection();
    }
}
