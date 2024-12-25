package com.aivle.mini7.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    @Value("${hospital.api.host")
    private String fastApiUrl;

    @Value("${openai.api.key.file}")
    private String apiKeyFilePath;

    // FastAPI URL Getter
    public String getFastApiUrl() {
        return fastApiUrl;
    }

    // OpenAI API Key File Path Getter
    public String getApiKeyFilePath() {
        return apiKeyFilePath;
    }

    // RestTemplate Bean 정의
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
