package cl.duoc.levelup.service;

import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.repository.UsuarioRepository;
import cl.duoc.levelup.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(Usuario usuario) {
        // Validar que el correo no exista
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con este correo");
        }

        // Encriptar password
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Establecer valores por defecto
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setPuntosLevelUp(0);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerPorRun(String run) {
        return usuarioRepository.findByRun(run);
    }

    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> obtenerActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public List<Usuario> obtenerPorTipo(Usuario.TipoUsuario tipo) {
        return usuarioRepository.findByTipoUsuario(tipo);
    }

    public Usuario actualizarUsuario(String run, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos permitidos
        if (usuarioActualizado.getNombres() != null) {
            usuario.setNombres(usuarioActualizado.getNombres());
        }
        if (usuarioActualizado.getApellidos() != null) {
            usuario.setApellidos(usuarioActualizado.getApellidos());
        }
        if (usuarioActualizado.getDireccion() != null) {
            usuario.setDireccion(usuarioActualizado.getDireccion());
        }
        if (usuarioActualizado.getRegion() != null) {
            usuario.setRegion(usuarioActualizado.getRegion());
        }
        if (usuarioActualizado.getComuna() != null) {
            usuario.setComuna(usuarioActualizado.getComuna());
        }

        return usuarioRepository.save(usuario);
    }

    public void desactivarUsuario(String run) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public void activarUsuario(String run) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    public Usuario agregarPuntos(String run, Integer puntos) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setPuntosLevelUp(usuario.getPuntosLevelUp() + puntos);
        return usuarioRepository.save(usuario);
    }

    public Usuario usarPuntos(String run, Integer puntos) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (usuario.getPuntosLevelUp() < puntos) {
            throw new RuntimeException("No tienes suficientes puntos LevelUp");
        }
        
        usuario.setPuntosLevelUp(usuario.getPuntosLevelUp() - puntos);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerPorDominio(String dominio) {
        return usuarioRepository.findByDominio(dominio);
    }

    public List<Usuario> obtenerConPuntosMinimos(Integer minPuntos) {
        return usuarioRepository.findByPuntosMinimos(minPuntos);
    }

    public boolean cambiarPassword(String run, String passwordActual, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            return false;
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        return true;
    }

    public Usuario obtenerUsuarioAutenticado(UserPrincipal userPrincipal) {
        return usuarioRepository.findByRun(userPrincipal.getRun())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }
}