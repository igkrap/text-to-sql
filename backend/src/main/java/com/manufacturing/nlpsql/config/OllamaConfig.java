package com.manufacturing.nlpsql.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ollama")
@Data
public class OllamaConfig {
    private String baseUrl;
    private String model;
    private Integer timeout;
}
