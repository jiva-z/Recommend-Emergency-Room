package com.aivle.mini7.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class HospitalService {
    @Value("${hospital.api.host}")
    private String fastApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> recommendHospital(String text, double latitude, double longitude, int count) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);
            requestBody.put("latitude", latitude);
            requestBody.put("longitude", longitude);
            requestBody.put("count", count);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    fastApiUrl + "/hospital/recommend_hospital",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("병원 추천 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
