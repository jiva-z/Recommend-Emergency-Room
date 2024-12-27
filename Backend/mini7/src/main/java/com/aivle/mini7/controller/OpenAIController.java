package com.aivle.mini7.controller;

import com.aivle.mini7.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    @GetMapping("/hospital/openai")
    public Map<String, Object> callOpenAI() {
        try {
            // OpenAIService의 호출 메서드 실행
            return openAIService.callOpenAI();
        } catch (Exception e) {
            // 예외가 발생한 경우 에러 메시지 반환
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
