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

    /**
     * Devuelve todos los productos registrados en la base de datos.
     */
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    /**
     * Devuelve solo los productos que están activos (no eliminados).
     */
    public List<Producto> obtenerActivos() {
        return productoRepository.findByActivoTrue();
    }

    /**
     * Busca productos por su categoría (ejemplo: consolas, accesorios).
     */
    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    /**
     * Devuelve productos que tienen poco stock (stock crítico).
     */
    public List<Producto> obtenerConStockCritico() {
        return productoRepository.findProductosConStockCritico();
    }

    /**
     * Busca un producto por su código único.
     */
        public Optional<Producto> obtenerPorId(String codigo) {
        return productoRepository.findById(codigo);
    }

    /**
     * Busca productos que contengan el nombre indicado.
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContaining(nombre);
    }

    /**
     * Crea un nuevo producto y lo deja activo por defecto.
     */
    public Producto crearProducto(Producto producto) {
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    /**
     * Actualiza los datos de un producto, solo los campos que vienen con datos.
     */
        public Producto actualizarProducto(String codigo, Producto productoActualizado) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Solo actualiza los campos que no son nulos
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

    /**
     * Marca un producto como inactivo (no se elimina de la base de datos).
     */
        public void eliminarProducto(String codigo) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    /**
     * Activa un producto que estaba inactivo.
     */
        public void activarProducto(String codigo) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setActivo(true);
        productoRepository.save(producto);
    }

    /**
     * Cambia el stock de un producto por el valor que se indica.
     */
        public Producto actualizarStock(String codigo, Integer nuevoStock) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    /**
     * Verifica si hay suficiente stock y si el producto está activo.
     */
        public boolean verificarDisponibilidad(String codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        return producto.getStock() >= cantidad && producto.getActivo();
    }

    /**
     * Resta una cantidad al stock del producto si hay suficiente.
     */
        public void reducirStock(String codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    /**
     * Suma una cantidad al stock del producto (por ejemplo, si se cancela un pedido).
     */
        public void restaurarStock(String codigo, Integer cantidad) {
        Producto producto = productoRepository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }

    /**
     * Devuelve todas las categorías de productos que existen.
     */
    public List<String> obtenerCategorias() {
        return productoRepository.findAllCategorias();
    }
}