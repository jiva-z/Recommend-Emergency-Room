package com.aivle.mini7.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDto {
    private EmergencyPredictionDto emergencyPrediction; // 응급 상황 예측 정보
    private List<HospitalDto> nearestHospitals; // 가까운 병원 목록
    private String language; // 사용 언어
}