package com.aivle.mini7.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emergency")
public class resultController {

    // 결과 페이지 표시
    @GetMapping("/result")
    public String showResult() {
        return "Emergency/result";
    }

    // 다시 입력 페이지로 리다이렉트
    @GetMapping("/re-input")
    public String reInput() {
        return "redirect:/";
    }
}
