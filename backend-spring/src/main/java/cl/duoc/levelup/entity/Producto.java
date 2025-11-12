package cl.duoc.levelup.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @Column(name = "codigo", length = 10)
    private String codigo;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @NotBlank
    @Size(max = 60)
    @Column(name = "categoria", nullable = false, length = 60)
    private String categoria;
    
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;
    
    @Min(0)
    @Column(name = "stock", nullable = false)
    private Integer stock;
    
    @Column(name = "stock_critico")
    private Integer stockCritico = 5;
    
    @Column(name = "imagen", length = 300)
    private String imagen;
    
    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @Lob
    @Column(name = "detalles", columnDefinition = "TEXT")
    private String detalles;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    // Relaciones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoItem> pedidoItems;
    
    // Constructores
    public Producto() {}
    
    public Producto(String codigo, String nombre, String categoria, BigDecimal precio, Integer stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }
    
    // Métodos de negocio
    public boolean tieneStockSuficiente(int cantidadRequerida) {
        return stock >= cantidadRequerida;
    }
    
    public boolean esStockCritico() {
        return stock <= (stockCritico != null ? stockCritico : 5);
    }
    
    public void descontarStock(int cantidad) {
        if (tieneStockSuficiente(cantidad)) {
            this.stock -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }
    
    public void aumentarStock(int cantidad) {
        if (cantidad > 0) {
            this.stock += cantidad;
        }
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getStockCritico() { return stockCritico; }
    public void setStockCritico(Integer stockCritico) { this.stockCritico = stockCritico; }
    
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public List<PedidoItem> getPedidoItems() { return pedidoItems; }
    public void setPedidoItems(List<PedidoItem> pedidoItems) { this.pedidoItems = pedidoItems; }
}