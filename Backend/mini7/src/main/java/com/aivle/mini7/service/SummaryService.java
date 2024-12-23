package com.aivle.mini7.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SummaryService {

    @Value("${hospital.api.host}")
    private String fastApiUrl;

    public Map<String, Object> summarizeText(String text) {
        // FastAPI의 /summarize_text 엔드포인트 호출
        String url = fastApiUrl + "/summarize_text?text=" + text;
        RestTemplate restTemplate = new RestTemplate();

        try {
            // FastAPI 호출 및 결과 받기
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 응답 처리
            if (response == null) {
                throw new RuntimeException("FastAPI에서 응답을 받지 못했습니다.");
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("텍스트 요약 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
