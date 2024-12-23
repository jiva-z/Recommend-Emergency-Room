package com.aivle.mini7.controller;
import com.aivle.mini7.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/hospital")
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @GetMapping("/summarize_text")  // FastAPI와 동일한 경로로 수정
    public Map<String, Object> summarize(@RequestParam("text") String text) {
        try {
            return summaryService.summarizeText(text);
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
