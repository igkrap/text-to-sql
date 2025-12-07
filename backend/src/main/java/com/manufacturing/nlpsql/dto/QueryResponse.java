package com.manufacturing.nlpsql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    private String naturalLanguageQuery;
    private String generatedSql;
    private List<Map<String, Object>> results;
    private int rowCount;
    private String executionTime;
    private String error;

    public static QueryResponse success(String nlQuery, String sql, List<Map<String, Object>> results, String execTime) {
        QueryResponse response = new QueryResponse();
        response.setNaturalLanguageQuery(nlQuery);
        response.setGeneratedSql(sql);
        response.setResults(results);
        response.setRowCount(results.size());
        response.setExecutionTime(execTime);
        return response;
    }

    public static QueryResponse error(String nlQuery, String errorMsg) {
        QueryResponse response = new QueryResponse();
        response.setNaturalLanguageQuery(nlQuery);
        response.setError(errorMsg);
        return response;
    }
}
