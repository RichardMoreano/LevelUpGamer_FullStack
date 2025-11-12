package cl.duoc.levelup.service;

import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.security.JwtTokenProvider;
import cl.duoc.levelup.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioService usuarioService;

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCorreo(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado(userPrincipal);
        
        return new JwtAuthenticationResponse(jwt, usuario);
    }

    public Usuario registerUser(SignUpRequest signUpRequest) {
        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setRun(signUpRequest.getRun());
        usuario.setNombres(signUpRequest.getNombres());
        usuario.setApellidos(signUpRequest.getApellidos());
        usuario.setCorreo(signUpRequest.getCorreo());
        usuario.setPassword(signUpRequest.getPassword());
        usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE);
        usuario.setRegion(signUpRequest.getRegion());
        usuario.setComuna(signUpRequest.getComuna());
        usuario.setDireccion(signUpRequest.getDireccion());

        return usuarioService.crearUsuario(usuario);
    }

    // DTOs
    public static class LoginRequest {
        private String correo;
        private String password;

        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class SignUpRequest {
        private String run;
        private String nombres;
        private String apellidos;
        private String correo;
        private String password;
        private String region;
        private String comuna;
        private String direccion;

        public String getRun() { return run; }
        public void setRun(String run) { this.run = run; }
        public String getNombres() { return nombres; }
        public void setNombres(String nombres) { this.nombres = nombres; }
        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getComuna() { return comuna; }
        public void setComuna(String comuna) { this.comuna = comuna; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
    }

    public static class JwtAuthenticationResponse {
        private String accessToken;
        private String tokenType = "Bearer";
        private Usuario usuario;

        public JwtAuthenticationResponse(String accessToken, Usuario usuario) {
            this.accessToken = accessToken;
            this.usuario = usuario;
        }

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    }
}