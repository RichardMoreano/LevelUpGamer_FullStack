package cl.duoc.levelup.service;

import cl.duoc.levelup.dto.AuthResponse;
import cl.duoc.levelup.dto.LoginRequest;
import cl.duoc.levelup.dto.RegisterRequest;
import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.repository.UsuarioRepository;
import cl.duoc.levelup.security.JwtTokenProvider;
import cl.duoc.levelup.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            Usuario usuario = usuarioRepository.findByCorreo(loginRequest.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));
            
            String refreshToken = tokenProvider.generateRefreshToken(usuario.getCorreo());
            
            // No enviar contraseña en la respuesta
            usuario.setPassword(null);
            
            return new AuthResponse(jwt, refreshToken, usuario);
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales inválidas", e);
        }
    }

    public Usuario registerUser(RegisterRequest registerRequest) {
        // Verificar si el usuario ya existe por email
        if (usuarioRepository.findByCorreo(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si el usuario ya existe por RUN
        if (usuarioRepository.findByRun(registerRequest.getRun()).isPresent()) {
            throw new RuntimeException("El RUN ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setRun(registerRequest.getRun());
        usuario.setNombres(registerRequest.getNombre());
        usuario.setApellidos(registerRequest.getApellido());
        usuario.setCorreo(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setDireccion(registerRequest.getDireccion());
        usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE); // Por defecto, los nuevos usuarios son clientes
        usuario.setActivo(true);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        // No retornar la contraseña
        savedUsuario.setPassword(null);
        
        return savedUsuario;
    }

    public Usuario registerUserFromEntity(Usuario usuarioRequest) {
        // Verificar si el usuario ya existe por email
        if (usuarioRepository.findByCorreo(usuarioRequest.getCorreo()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si el usuario ya existe por RUN
        if (usuarioRepository.findByRun(usuarioRequest.getRun()).isPresent()) {
            throw new RuntimeException("El RUN ya está registrado");
        }

        // Crear nuevo usuario basado en la entidad recibida
        Usuario usuario = new Usuario();
        usuario.setRun(usuarioRequest.getRun());
        usuario.setNombres(usuarioRequest.getNombres());
        usuario.setApellidos(usuarioRequest.getApellidos());
        usuario.setCorreo(usuarioRequest.getCorreo());
        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        usuario.setDireccion(usuarioRequest.getDireccion());
        usuario.setRegion(usuarioRequest.getRegion());
        usuario.setComuna(usuarioRequest.getComuna());
        
        // Usar tipo de usuario del request o CLIENTE por defecto
        usuario.setTipoUsuario(usuarioRequest.getTipoUsuario() != null ? 
            usuarioRequest.getTipoUsuario() : Usuario.TipoUsuario.CLIENTE);
        usuario.setActivo(true);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        // No retornar la contraseña
        savedUsuario.setPassword(null);
        
        return savedUsuario;
    }

    public Usuario registerUserFromRequest(cl.duoc.levelup.dto.RegistroUsuarioRequest registroRequest) {
        // Verificar si el usuario ya existe por email
        if (usuarioRepository.findByCorreo(registroRequest.getCorreo()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si el usuario ya existe por RUN
        if (usuarioRepository.findByRun(registroRequest.getRun()).isPresent()) {
            throw new RuntimeException("El RUN ya está registrado");
        }

        // Crear nuevo usuario basado en el request
        Usuario usuario = new Usuario();
        usuario.setRun(registroRequest.getRun());
        usuario.setNombres(registroRequest.getNombres());
        usuario.setApellidos(registroRequest.getApellidos());
        usuario.setCorreo(registroRequest.getCorreo());
        usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        usuario.setDireccion(registroRequest.getDireccion());
        usuario.setRegion(registroRequest.getRegion());
        usuario.setComuna(registroRequest.getComuna());
        
        // Convertir string a enum
        if (registroRequest.getTipoUsuario() != null) {
            try {
                usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(registroRequest.getTipoUsuario()));
            } catch (IllegalArgumentException e) {
                usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE); // Por defecto
            }
        } else {
            usuario.setTipoUsuario(Usuario.TipoUsuario.CLIENTE); // Por defecto
        }
        
        usuario.setActivo(true);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        // No retornar la contraseña
        savedUsuario.setPassword(null);
        
        return savedUsuario;
    }

    public Usuario getUserProfile(String email) {
        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // No retornar la contraseña
        usuario.setPassword(null);
        
        return usuario;
    }

}