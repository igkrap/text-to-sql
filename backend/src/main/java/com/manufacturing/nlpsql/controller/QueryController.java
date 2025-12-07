package com.manufacturing.nlpsql.controller;

import com.manufacturing.nlpsql.dto.QueryRequest;
import com.manufacturing.nlpsql.dto.QueryResponse;
import com.manufacturing.nlpsql.service.LlmNlpToSqlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    private final LlmNlpToSqlService llmNlpToSqlService;

    @PostMapping("/execute")
    public ResponseEntity<QueryResponse> executeQuery(@RequestBody QueryRequest request) {
        try {
            long startTime = System.currentTimeMillis();

            String nlQuery = request.getNaturalLanguageQuery();
            log.info("자연어 쿼리: {}", nlQuery);

            // LLM을 사용한 NLP to SQL 변환
            String sql = llmNlpToSqlService.convertToSql(nlQuery);
            log.info("생성된 SQL: {}", sql);

            // SQL 실행
            List<Map<String, Object>> results = llmNlpToSqlService.executeQuery(sql);

            long endTime = System.currentTimeMillis();
            String executionTime = (endTime - startTime) + "ms";

            return ResponseEntity.ok(
                QueryResponse.success(nlQuery, sql, results, executionTime)
            );

        } catch (Exception e) {
            log.error("쿼리 처리 오류: ", e);
            return ResponseEntity.ok(
                QueryResponse.error(request.getNaturalLanguageQuery(), e.getMessage())
            );
        }
    }

    @GetMapping("/examples")
    public ResponseEntity<List<String>> getExamples() {
        List<String> examples = List.of(
            "모든 제품 보여줘",
            "재고가 부족한 제품은?",
            "완료된 생산오더 개수는?",
            "진행중인 작업오더 보여줘",
            "카테고리가 전자부품인 제품 조회",
            "창고가 A창고인 재고 보여줘",
            "이번달 생성된 생산오더는?",
            "수량이 100보다 큰 재고 조회",
            "제품 가격 평균은?",
            "카테고리별 제품 개수",
            "상태별 생산오더 개수",
            "재고 수량 내림차순으로 10개만 보여줘"
        );
        return ResponseEntity.ok(examples);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        boolean ollamaAvailable = llmNlpToSqlService.isOllamaAvailable();

        status.put("ollama_available", ollamaAvailable);
        status.put("status", ollamaAvailable ? "ready" : "ollama_unavailable");
        status.put("message", ollamaAvailable ?
            "로컬 LLM(Ollama)이 정상적으로 연결되었습니다." :
            "Ollama 서버에 연결할 수 없습니다. Ollama가 실행중인지 확인해주세요.");

        return ResponseEntity.ok(status);
    }
}
