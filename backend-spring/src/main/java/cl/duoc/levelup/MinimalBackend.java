package cl.duoc.levelup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
public class MinimalBackend {

    public static void main(String[] args) {
        SpringApplication.run(MinimalBackend.class, args);
    }

    @RestController
    @CrossOrigin(origins = "*")
    public static class ApiController {

        @GetMapping("/")
        public String home() {
            return "LevelUp Gamer Backend is WORKING! 🎮";
        }

        @PostMapping("/api/v1/auth/register")
        public ResponseEntity<?> register(@RequestBody Map<String, Object> userData) {
            System.out.println("📝 Registro recibido: " + userData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Usuario registrado exitosamente!");
            response.put("usuario", Map.of(
                "correo", userData.getOrDefault("correo", ""),
                "nombres", userData.getOrDefault("nombres", ""),
                "run", userData.getOrDefault("run", ""),
                "tipoUsuario", "cliente"
            ));
            
            return ResponseEntity.ok(response);
        }

        @PostMapping("/api/v1/auth/login")
        public ResponseEntity<?> login(@RequestBody Map<String, Object> credentials) {
            System.out.println("🔐 Login recibido: " + credentials);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "¡Login exitoso!");
            response.put("token", "temp-jwt-token-123");
            response.put("usuario", Map.of(
                "correo", credentials.getOrDefault("correo", ""),
                "nombres", "Usuario Temporal",
                "tipoUsuario", "cliente"
            ));
            
            return ResponseEntity.ok(response);
        }
    }
}