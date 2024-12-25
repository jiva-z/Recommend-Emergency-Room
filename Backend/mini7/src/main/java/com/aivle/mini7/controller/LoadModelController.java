package com.aivle.mini7.controller;

import com.aivle.mini7.service.LoadModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoadModelController {

    @Autowired
    private LoadModelService loadModelService;

    @GetMapping("/hospital/load_emergency_model")
    public Map<String, Object> loadModel() {
        try {
            return loadModelService.loadEmergencyModel();
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
