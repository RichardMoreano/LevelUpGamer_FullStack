package cl.duoc.levelup.controller;

import cl.duoc.levelup.entity.Pedido;
import cl.duoc.levelup.security.UserPrincipal;
import cl.duoc.levelup.service.PedidoService;
import cl.duoc.levelup.service.PedidoService.CrearPedidoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "http://localhost:5173")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> crearPedido(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CrearPedidoRequest request) {
        try {
            Pedido pedido = pedidoService.crearPedido(userPrincipal, request);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<Pedido>> getMisPedidos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Pedido> pedidos = pedidoService.obtenerPedidosUsuario(userPrincipal.getRun());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoints para administradores
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.obtenerTodosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable Long id, 
            @RequestBody Map<String, String> estadoData) {
        
        Pedido.EstadoPedido nuevoEstado = Pedido.EstadoPedido.valueOf(estadoData.get("estado"));
        Pedido pedido = pedidoService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        try {
            pedidoService.cancelarPedido(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}