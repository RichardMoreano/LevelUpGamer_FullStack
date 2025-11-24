package cl.duoc.levelup.dto;

import cl.duoc.levelup.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponse {

    @Schema(description = "Token JWT de autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Token de refresco", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9REFRESH...")
    private String refreshToken;

    @Schema(description = "Datos del usuario autenticado")
    private Usuario usuario;

    @Schema(description = "Mensaje de respuesta", example = "Login exitoso")
    private String message;
    
    // Constructor por defecto
    public AuthResponse() {}
    
    // Constructor con token y usuario
    public AuthResponse(String token, String refreshToken, Usuario usuario) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.usuario = usuario;
        this.message = "Login exitoso";
    }
    
    // Constructor con mensaje de error
    public AuthResponse(String message) {
        this.message = message;
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}