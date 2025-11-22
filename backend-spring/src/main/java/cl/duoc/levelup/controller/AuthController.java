package cl.duoc.levelup.controller;

import cl.duoc.levelup.dto.AuthResponse;
import cl.duoc.levelup.dto.LoginRequest;
import cl.duoc.levelup.dto.RegisterRequest;
import cl.duoc.levelup.dto.RegistroUsuarioRequest;
import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://richardmoreano.github.io"})
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Credenciales inválidas: " + e.getMessage()));
        }
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody String rawJson, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("🔍 JSON crudo recibido: " + rawJson);
            
            // Deserializar manualmente el JSON
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            RegistroUsuarioRequest registroRequest = objectMapper.readValue(rawJson, RegistroUsuarioRequest.class);
            System.out.println("🔍 DEBUG - Datos de registro recibidos:");
            System.out.println("RUN: " + registroRequest.getRun());
            System.out.println("Nombres: " + registroRequest.getNombres());
            System.out.println("Apellidos: " + registroRequest.getApellidos());
            System.out.println("Correo: " + registroRequest.getCorreo());
            System.out.println("Password: " + (registroRequest.getPassword() != null ? "***" : "null"));
            System.out.println("Tipo: " + registroRequest.getTipoUsuario());
            System.out.println("Región: " + registroRequest.getRegion());
            System.out.println("Comuna: " + registroRequest.getComuna());
            System.out.println("Dirección: " + registroRequest.getDireccion());
            
            Usuario usuario = authService.registerUserFromRequest(registroRequest);
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validar token", description = "Valida si el token JWT es válido")
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("valid", true);
            response.put("user", authentication.getName());
            return ResponseEntity.ok(response);
        }
        response.put("valid", false);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @Operation(summary = "Obtener perfil", description = "Obtiene la información del usuario autenticado")
    @GetMapping("/profile")
    public ResponseEntity<Usuario> getProfile(Authentication authentication) {
        try {
            Usuario usuario = authService.getUserProfile(authentication.getName());
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}