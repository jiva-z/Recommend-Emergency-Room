package com.aivle.mini7.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmergencyPredictionDto {
    private String summary; // 예측 요약
    private String keywords; // 주요 키워드
    private int predicted_class; // 예측된 클래스
    private List<Double> probabilities; // 클래스별 확률
}
