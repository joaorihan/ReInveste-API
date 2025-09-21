package br.com.fiap3espg.spring_boot_project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "XP Investimento - Auxílio contra Vício em Apostas",
            "version", "1.0.0"
        );
        return ResponseEntity.ok(response);
    }
}