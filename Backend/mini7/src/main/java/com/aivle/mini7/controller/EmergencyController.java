package com.aivle.mini7.controller;

import com.aivle.mini7.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmergencyController {

    @Autowired
    private EmergencyService emergencyService;

    // /predict_emergency 엔드포인트
    @GetMapping("/hospital/predict_emergency")
    public Map<String, Object> predict(@RequestParam("text") String text) {
        try {
            // EmergencyService 호출
            return emergencyService.predictEmergency(text);
        } catch (Exception e) {
            // FastAPI 호출 또는 처리 중 오류가 발생한 경우 에러 메시지 반환
            return Map.of("error", e.getMessage());
        }
    }
}
