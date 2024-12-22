package com.aivle.mini7.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmergencyService {

    @Value("${hospital.api.host}") // application-local.properties에서 값 주입
    private String fastApiUrl; // FastAPI 서버 주소

    public Map<String, Object> predictEmergency(String text) {
        // FastAPI의 /predict_emergency 엔드포인트 호출
        String url = fastApiUrl + "/predict_emergency?text=" + text;
        RestTemplate restTemplate = new RestTemplate();

        try {
            // FastAPI 호출 및 결과 받기
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 응답 처리
            if (response == null || response.containsKey("error")) {
                throw new RuntimeException("FastAPI에서 오류 발생: " + response.get("error"));
            }

            return response; // 결과 반환
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
