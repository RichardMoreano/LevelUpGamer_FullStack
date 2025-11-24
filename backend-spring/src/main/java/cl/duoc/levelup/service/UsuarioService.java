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

    /**
     * Crea un nuevo usuario en la base de datos.
     * Primero revisa que el correo no esté repetido, luego encripta la contraseña
     * y pone valores iniciales como activo y puntos en cero.
     */
    public Usuario crearUsuario(Usuario usuario) {
        // Revisar si ya existe el correo
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con este correo");
        }

        // Guardar la contraseña encriptada
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Valores iniciales para el usuario
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setPuntosLevelUp(0);

        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su RUN (identificador único).
     */
    public Optional<Usuario> obtenerPorRun(String run) {
        return usuarioRepository.findByRun(run);
    }

    /**
     * Busca un usuario por su correo electrónico.
     */
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    /**
     * Devuelve todos los usuarios registrados.
     */
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Devuelve solo los usuarios que están activos.
     */
    public List<Usuario> obtenerActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    /**
     * Busca usuarios por su tipo (admin, vendedor, cliente, etc).
     */
    public List<Usuario> obtenerPorTipo(Usuario.TipoUsuario tipo) {
        return usuarioRepository.findByTipoUsuario(tipo);
    }

    /**
     * Actualiza los datos de un usuario, solo los campos permitidos.
     * La contraseña no se cambia aquí, solo con el método especial.
     */
    public Usuario actualizarUsuario(String run, Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Solo actualiza los campos que vienen con datos
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
        if (usuarioActualizado.getCorreo() != null) {
            usuario.setCorreo(usuarioActualizado.getCorreo());
        }
        if (usuarioActualizado.getTipoUsuario() != null) {
            usuario.setTipoUsuario(usuarioActualizado.getTipoUsuario());
        }

        // La contraseña solo se cambia con cambiarPassword()

        return usuarioRepository.save(usuario);
    }

    /**
     * Desactiva un usuario (por ejemplo, si se elimina o bloquea).
     */
    public void desactivarUsuario(String run) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Activa un usuario que estaba desactivado.
     */
    public void activarUsuario(String run) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    /**
     * Suma puntos LevelUp al usuario (por ejemplo, por compras).
     */
    public Usuario agregarPuntos(String run, Integer puntos) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPuntosLevelUp(usuario.getPuntosLevelUp() + puntos);
        return usuarioRepository.save(usuario);
    }

    /**
     * Resta puntos LevelUp al usuario si tiene suficientes.
     */
    public Usuario usarPuntos(String run, Integer puntos) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getPuntosLevelUp() < puntos) {
            throw new RuntimeException("No tienes suficientes puntos LevelUp");
        }

        usuario.setPuntosLevelUp(usuario.getPuntosLevelUp() - puntos);
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca usuarios por el dominio de su correo (por ejemplo, @duoc.cl).
     */
    public List<Usuario> obtenerPorDominio(String dominio) {
        return usuarioRepository.findByDominio(dominio);
    }

    /**
     * Devuelve usuarios que tienen al menos cierta cantidad de puntos.
     */
    public List<Usuario> obtenerConPuntosMinimos(Integer minPuntos) {
        return usuarioRepository.findByPuntosMinimos(minPuntos);
    }

    /**
     * Cambia la contraseña del usuario si la actual es correcta.
     */
    public boolean cambiarPassword(String run, String passwordActual, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verifica que la contraseña actual coincida
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            return false;
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        return true;
    }

    /**
     * Devuelve el usuario que está autenticado actualmente.
     */
    public Usuario obtenerUsuarioAutenticado(UserPrincipal userPrincipal) {
        return usuarioRepository.findByRun(userPrincipal.getRun())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }
}
