package com.aivle.mini7.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LoadModelService {

    @Value("${hospital.api.host}")
    private String fastApiUrl;

    public Map<String, Object> loadEmergencyModel() {

        String url = fastApiUrl + "/load_emergency_model";
        RestTemplate restTemplate = new RestTemplate();

        try {
            // FastAPI 호출 및 결과 받기
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            // 응답 처리
            if (response == null || !"success".equals(response.get("status"))) {
                throw new RuntimeException("FastAPI에서 오류 발생: " + response.get("message"));
            }

            return response; // 성공적으로 로드된 모델의 상태 반환
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
