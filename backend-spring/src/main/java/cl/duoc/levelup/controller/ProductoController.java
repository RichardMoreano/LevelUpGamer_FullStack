package cl.duoc.levelup.controller;

import cl.duoc.levelup.entity.Producto;
import cl.duoc.levelup.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProducts() {
        List<Producto> productos = productoService.obtenerTodos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Producto>> getActiveProducts() {
        List<Producto> productos = productoService.obtenerActivos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> getProductById(@PathVariable String codigo) {
        return productoService.obtenerPorId(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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