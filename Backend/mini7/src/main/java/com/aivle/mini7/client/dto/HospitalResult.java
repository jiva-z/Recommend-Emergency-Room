package com.aivle.mini7.client.dto;

import lombok.Data;

@Data
public class HospitalResult {
    private String hospital_name; // 병원 이름
    private String tel1; // 병원 전화번호
    private String address; // 병원 주소
    private double distance_km; // 병원까지의 거리
    private String duration; // 예상 소요 시간
    private String arrival_time; // 도착 예정 시간
}
