package com.aivle.mini7.controller;

import com.aivle.mini7.client.dto.HospitalDto;
import com.aivle.mini7.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
public class indexController {
    @GetMapping("/")
    public String searchpage() {
        return "Emergency/searchpage";
    }

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/hospital/recommend_hospital")
    public String recommendHospital(
            @RequestParam String text,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "3") Integer count,  // 기본값을 2로 변경
            Model model
    ) {
        try {
            Map<String, Object> response = hospitalService.recommendHospital(text, latitude, longitude, count);

            @SuppressWarnings("unchecked")
            Map<String, Object> emergencyPrediction = (Map<String, Object>) response.get("emergency_prediction");

            Integer predictedClass = (Integer) emergencyPrediction.get("predicted_class");
            if (predictedClass <= 3) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> hospitals = (List<Map<String, Object>>) response.get("nearest_hospitals");

                // 언어 확인
                String language = (String) response.get("language");

                if (hospitals != null) {
                    if ("English".equals(language)) {
                        // 영어인 경우 매핑
                        hospitals.forEach(hospital -> {
                            hospital.put("Hospital Name", hospital.get("Hospital Name"));
                            hospital.put("전화번호1", hospital.get("Tel"));
                            hospital.put("주소", hospital.get("Address"));
                            hospital.put("거리(km)", hospital.get("Distance(km)"));
                            hospital.put("소요시간", hospital.get("Duration"));
                            hospital.put("도착예정시각", hospital.get("Expected Arrival"));
                        });
                    }
                    // 한국어인 경우는 이미 올바른 키로 매핑되어 있으므로 추가 처리 불필요
                }
                model.addAttribute("isEnglish", "English".equals(response.get("language")));
                model.addAttribute("hospitals", hospitals);
            }

            model.addAttribute("summary", emergencyPrediction.get("summary"));
            model.addAttribute("keywords", emergencyPrediction.get("keywords"));
            model.addAttribute("predictedClass", predictedClass);
            model.addAttribute("language", response.get("language"));

            if (response.containsKey("message")) {
                model.addAttribute("message", response.get("message"));
            }


            return "Emergency/result";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/re_input")
    public String reInput() {
        return "redirect:/";
    }


    @GetMapping("/hospital/mypage")
    public String mypage(){
        return "Emergency/mypage";
    }

    @GetMapping("/mypage")
    public String mypage1(){
        return "Emergency/mypage";
    }

    @GetMapping("/login")
    public String loginPage1() {
        return "Emergency/login";
    }

    @GetMapping("/hospital/logout")
    public String loginPage() {
        return "Emergency/login";
    }



}
