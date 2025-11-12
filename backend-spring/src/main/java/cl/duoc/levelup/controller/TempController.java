package cl.duoc.levelup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin(origins = "*")
public class TempController {

    @GetMapping("/")
    public String home() {
        return "LevelUp Gamer Backend is WORKING!";
    }

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registro exitoso temporalmente - backend funcionando");
        response.put("received", userData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> credentials) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login exitoso temporalmente - backend funcionando");
        response.put("token", "temp-token-123");
        
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("correo", credentials.get("correo"));
        usuario.put("nombres", "Usuario Temporal");
        usuario.put("tipoUsuario", "cliente");
        response.put("usuario", usuario);
        
        return ResponseEntity.ok(response);
    }
}