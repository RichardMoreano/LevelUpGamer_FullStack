package cl.duoc.levelup.controller;

import cl.duoc.levelup.dto.SolicitudContactoRequest;
import cl.duoc.levelup.entity.Solicitud;
import cl.duoc.levelup.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "https://richardmoreano.github.io"
})
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    // 👇 Endpoint público para el formulario de contacto
    @PostMapping
    public ResponseEntity<Solicitud> crearSolicitud(
            @Valid @RequestBody SolicitudContactoRequest request) {

        Solicitud creada = solicitudService.crearSolicitud(request);
        return ResponseEntity
                .created(URI.create("/api/solicitudes/" + creada.getId()))
                .body(creada);
    }

    // 👇 Solo ADMIN puede ver y administrar solicitudes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Solicitud>> getAll() {
        return ResponseEntity.ok(solicitudService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Solicitud> getById(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Solicitud>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(solicitudService.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Solicitud> updateEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String estado = body.getOrDefault("estado", "pendiente");
        Solicitud actualizada = solicitudService.actualizarEstado(id, estado);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        solicitudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
