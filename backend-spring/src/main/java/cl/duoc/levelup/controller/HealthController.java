package cl.duoc.levelup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class HealthController {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Level-Up Gamer API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("message", "Backend is working correctly");
        response.put("profile", env.getActiveProfiles());
        response.put("port", env.getProperty("server.port", "8080"));
        response.put("endpoints", Map.of(
            "health", "/health",
            "auth", "/api/v1/auth",
            "products", "/api/v1/productos",
            "users", "/api/v1/usuarios",
            "swagger", "/swagger-ui.html"
        ));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Level-Up Gamer API");
        response.put("timestamp", System.currentTimeMillis());
        
        // Check database connection
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            response.put("database", "UP");
        } catch (Exception e) {
            response.put("database", "DOWN");
            response.put("database_error", e.getMessage());
        }
        
        // Environment info
        response.put("profile", env.getActiveProfiles());
        response.put("java_version", System.getProperty("java.version"));
        
        return ResponseEntity.ok(response);
    }
}