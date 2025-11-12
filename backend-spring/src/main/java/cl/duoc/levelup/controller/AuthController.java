package cl.duoc.levelup.controller;

import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.service.AuthService;
import cl.duoc.levelup.service.AuthService.JwtAuthenticationResponse;
import cl.duoc.levelup.service.AuthService.LoginRequest;
import cl.duoc.levelup.service.AuthService.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"*"})
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            Usuario usuario = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Con JWT, el logout es principalmente del lado del cliente
        // Se puede implementar blacklist de tokens si es necesario
        return ResponseEntity.ok("Logout exitoso");
    }
}