package com.aivle.mini7.service;

import com.aivle.mini7.config.OpenAIConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OpenAIService {

    private final OpenAIConfig config;
    private final RestTemplate restTemplate;

    // OpenAIConfig와 RestTemplate을 생성자로 주입받음
    public OpenAIService(OpenAIConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> callOpenAI() {
        // FastAPI의 /openai 엔드포인트 URL
        String url = config.getFastApiUrl() + "/openai";

        try {
            // FastAPI 호출 및 응답 받기
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 응답이 null이거나 오류 키가 포함된 경우 예외 처리
            if (response == null || response.containsKey("error")) {
                throw new RuntimeException("FastAPI에서 오류 발생: " + response.get("error"));
            }

            return response; // FastAPI 응답 반환
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
