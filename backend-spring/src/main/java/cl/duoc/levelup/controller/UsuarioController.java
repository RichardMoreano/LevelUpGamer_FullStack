package cl.duoc.levelup.controller;

import cl.duoc.levelup.entity.Usuario;
import cl.duoc.levelup.security.UserPrincipal;
import cl.duoc.levelup.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<Usuario> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado(userPrincipal);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/me")
    public ResponseEntity<Usuario> updateCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody Usuario usuarioActualizado) {
        Usuario usuario = usuarioService.actualizarUsuario(userPrincipal.getRun(), usuarioActualizado);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/me/cambiar-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, String> passwordData) {
        
        String passwordActual = passwordData.get("passwordActual");
        String nuevaPassword = passwordData.get("nuevaPassword");
        
        boolean success = usuarioService.cambiarPassword(userPrincipal.getRun(), passwordActual, nuevaPassword);
        
        Map<String, Object> response = Map.of("success", success);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/puntos")
    public ResponseEntity<Map<String, Integer>> getPuntos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado(userPrincipal);
        Map<String, Integer> response = Map.of("puntos", usuario.getPuntosLevelUp());
        return ResponseEntity.ok(response);
    }

    // Endpoints para administradores
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getActiveUsers() {
        List<Usuario> usuarios = usuarioService.obtenerActivos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getUsersByType(@PathVariable Usuario.TipoUsuario tipo) {
        List<Usuario> usuarios = usuarioService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{run}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> getUserByRun(@PathVariable String run) {
        return usuarioService.obtenerPorRun(run)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{run}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> updateUser(@PathVariable String run, @Valid @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(run, usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @PutMapping("/{run}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable String run) {
        usuarioService.desactivarUsuario(run);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{run}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable String run) {
        usuarioService.activarUsuario(run);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{run}/puntos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> addPoints(@PathVariable String run, @RequestBody Map<String, Integer> pointsData) {
        Integer puntos = pointsData.get("puntos");
        Usuario usuario = usuarioService.agregarPuntos(run, puntos);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/dominio/{dominio}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getUsersByDomain(@PathVariable String dominio) {
        List<Usuario> usuarios = usuarioService.obtenerPorDominio(dominio);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/puntos-minimos/{minPuntos}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getUsersWithMinPoints(@PathVariable Integer minPuntos) {
        List<Usuario> usuarios = usuarioService.obtenerConPuntosMinimos(minPuntos);
        return ResponseEntity.ok(usuarios);
    }
}