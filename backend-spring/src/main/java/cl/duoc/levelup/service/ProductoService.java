package cl.duoc.levelup.service;

import cl.duoc.levelup.entity.Producto;
import cl.duoc.levelup.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> obtenerActivos() {
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> obtenerConStockCritico() {
        return productoRepository.findProductosConStockCritico();
    }

    public Optional<Producto> obtenerPorId(String codigo) {
        return productoRepository.findById(codigo);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre);
    }

    public Producto crearProducto(Producto producto) {
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(String codigo, Producto productoActualizado) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Actualizar campos
        if (productoActualizado.getNombre() != null) {
            producto.setNombre(productoActualizado.getNombre());
        }
        if (productoActualizado.getDescripcion() != null) {
            producto.setDescripcion(productoActualizado.getDescripcion());
        }
        if (productoActualizado.getPrecio() != null) {
            producto.setPrecio(productoActualizado.getPrecio());
        }
        if (productoActualizado.getStock() != null) {
            producto.setStock(productoActualizado.getStock());
        }
        if (productoActualizado.getCategoria() != null) {
            producto.setCategoria(productoActualizado.getCategoria());
        }
        if (productoActualizado.getImagen() != null) {
            producto.setImagen(productoActualizado.getImagen());
        }

        return productoRepository.save(producto);
    }

    public void eliminarProducto(String codigo) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public void activarProducto(String codigo) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setActivo(true);
        productoRepository.save(producto);
    }

    public Producto actualizarStock(String codigo, Integer nuevoStock) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    public boolean verificarDisponibilidad(String codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        return producto.getStock() >= cantidad && producto.getActivo();
    }

    public void reducirStock(String codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    public void restaurarStock(String codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }

    public List<String> obtenerCategorias() {
        return productoRepository.findAllCategorias();
    }
}