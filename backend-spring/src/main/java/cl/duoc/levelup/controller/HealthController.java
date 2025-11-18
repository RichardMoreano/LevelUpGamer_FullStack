package cl.duoc.levelup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Level-Up Gamer API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("message", "Backend is working correctly");
        response.put("endpoints", Map.of(
            "auth", "/api/v1/auth",
            "products", "/api/v1/productos",
            "users", "/api/v1/usuarios",
            "swagger", "/swagger-ui.html"
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Level-Up Gamer API");
        return ResponseEntity.ok(response);
    }
}