package cl.duoc.levelup.controller;

import cl.duoc.levelup.entity.Producto;
import cl.duoc.levelup.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = {"*"})
@Tag(name = "Productos", description = "Gestión de productos del catálogo")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Obtener todos los productos", description = "Obtiene la lista completa de productos (requiere permisos de admin)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Producto>> getAllProducts() {
        try {
            List<Producto> productos = productoService.obtenerTodos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener productos públicos", description = "Obtiene la lista de productos activos para visualización pública")
    @GetMapping("/publicos")
    public ResponseEntity<List<Producto>> getActiveProducts() {
        try {
            List<Producto> productos = productoService.obtenerActivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener producto por código", description = "Obtiene un producto específico por su código")
    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> getProductById(@PathVariable String codigo) {
        try {
            return productoService.obtenerPorId(codigo)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> getProductsByCategory(@PathVariable String categoria) {
        List<Producto> productos = productoService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> searchProducts(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/stock-critico")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<List<Producto>> getCriticalStockProducts() {
        List<Producto> productos = productoService.obtenerConStockCritico();
        return ResponseEntity.ok(productos);
    }

    // Endpoints para administradores
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> createProduct(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crearProducto(producto);
        return ResponseEntity.ok(nuevoProducto);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> updateProduct(@PathVariable String codigo, @Valid @RequestBody Producto producto) {
        Producto productoActualizado = productoService.actualizarProducto(codigo, producto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String codigo) {
        productoService.eliminarProducto(codigo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{codigo}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateProduct(@PathVariable String codigo) {
        productoService.activarProducto(codigo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{codigo}/stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('VENDEDOR')")
    public ResponseEntity<Producto> updateStock(@PathVariable String codigo, @RequestBody Map<String, Integer> stockData) {
        Integer nuevoStock = stockData.get("stock");
        Producto producto = productoService.actualizarStock(codigo, nuevoStock);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/{codigo}/disponibilidad")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(@PathVariable String codigo, @RequestParam Integer cantidad) {
        boolean disponible = productoService.verificarDisponibilidad(codigo, cantidad);
        Map<String, Boolean> response = Map.of("disponible", disponible);
        return ResponseEntity.ok(response);
    }
}