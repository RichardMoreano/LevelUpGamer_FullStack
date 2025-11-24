package cl.duoc.levelup.controller;

import cl.duoc.levelup.dto.BoletaDTO;
import cl.duoc.levelup.entity.Boleta;
import cl.duoc.levelup.service.BoletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "https://richardmoreano.github.io"
})
public class BoletaController {

    @Autowired
    private BoletaService boletaService;

    // 🔹 Solo ADMIN y VENDEDOR pueden ver boletas
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<BoletaDTO>> getAll() {
        List<Boleta> boletas = boletaService.obtenerTodas();
        return ResponseEntity.ok(boletaService.toDTOList(boletas));
    }

    @GetMapping("/{numero}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<BoletaDTO> getByNumero(@PathVariable String numero) {
        return boletaService.obtenerPorNumero(numero)
                .map(boleta -> ResponseEntity.ok(boletaService.toDTO(boleta)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<BoletaDTO> getByPedido(@PathVariable Long pedidoId) {
        return boletaService.obtenerPorPedidoId(pedidoId)
                .map(boleta -> ResponseEntity.ok(boletaService.toDTO(boleta)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para el botón "Ver boleta":
     * - Si YA existe boleta para el pedido, la devuelve.
     * - Si NO existe, la crea y luego la devuelve.
     */
    @PostMapping("/generar/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<BoletaDTO> generarParaPedido(@PathVariable Long pedidoId) {
        Boleta boleta = boletaService.obtenerOCrearPorPedidoId(pedidoId);
        return ResponseEntity.ok(boletaService.toDTO(boleta));
    }
}
