package com.aivle.mini7.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OpenAIService {

    @Value("${hospital.api.host}") // FastAPI 서버 URL
    private String fastApiUrl;

    @Value("${openai.api.key.file}") // OpenAI API 키 파일 경로
    private String apiKeyFilePath;

    public Map<String, Object> callOpenAI() {
        // FastAPI의 /openai 엔드포인트 URL
        String url = fastApiUrl + "/openai";

        // RestTemplate을 사용하여 FastAPI 호출
        RestTemplate restTemplate = new RestTemplate();
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
