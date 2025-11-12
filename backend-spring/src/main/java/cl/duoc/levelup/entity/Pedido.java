package cl.duoc.levelup.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_cliente", referencedColumnName = "run", nullable = false)
    private Usuario usuario;
    
    @Column(name = "region", length = 100)
    private String region;
    
    @Column(name = "comuna", length = 100)
    private String comuna;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;
    
    @Column(name = "descuento_duoc", precision = 12, scale = 2)
    private BigDecimal descuentoDuoc = BigDecimal.ZERO;
    
    @Column(name = "descuento_puntos", precision = 12, scale = 2)
    private BigDecimal descuentoPuntos = BigDecimal.ZERO;
    
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;
    
    // Relaciones
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoItem> items;
    
    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Boleta boleta;
    
    // Constructores
    public Pedido() {
        this.fecha = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
    }
    
    public Pedido(Usuario usuario) {
        this();
        this.usuario = usuario;
    }
    
    // Métodos de negocio
    public void calcularTotales() {
        if (items == null || items.isEmpty()) {
            this.subtotal = BigDecimal.ZERO;
            this.total = BigDecimal.ZERO;
            return;
        }
        
        // Calcular subtotal
        this.subtotal = items.stream()
            .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Aplicar descuentos
        if (usuario != null) {
            // Descuento DUOC
            double porcentajeDuoc = usuario.calcularDescuentoDuoc();
            this.descuentoDuoc = subtotal.multiply(BigDecimal.valueOf(porcentajeDuoc));
            
            // Descuento por puntos
            double descuentoPuntosCalculado = usuario.calcularDescuentoPuntos(subtotal.doubleValue());
            this.descuentoPuntos = BigDecimal.valueOf(descuentoPuntosCalculado);
        }
        
        // Total final
        this.total = subtotal.subtract(descuentoDuoc).subtract(descuentoPuntos);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
    }
    
    public void marcarComoDespachado() {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden despachar pedidos pendientes");
        }
        
        // Descontar stock de productos
        if (items != null) {
            items.forEach(item -> {
                Producto producto = item.getProducto();
                producto.descontarStock(item.getCantidad());
            });
        }
        
        // Usar puntos del usuario si hay descuento por puntos
        if (descuentoPuntos.compareTo(BigDecimal.ZERO) > 0 && usuario != null) {
            int puntosUsados = descuentoPuntos.divide(BigDecimal.valueOf(10)).intValue();
            usuario.usarPuntos(puntosUsados);
        }
        
        this.estado = EstadoPedido.DESPACHADO;
    }
    
    public void cancelar() {
        if (estado == EstadoPedido.DESPACHADO) {
            throw new IllegalStateException("No se puede cancelar un pedido ya despachado");
        }
        this.estado = EstadoPedido.CANCELADO;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDescuentoDuoc() { return descuentoDuoc; }
    public void setDescuentoDuoc(BigDecimal descuentoDuoc) { this.descuentoDuoc = descuentoDuoc; }
    
    public BigDecimal getDescuentoPuntos() { return descuentoPuntos; }
    public void setDescuentoPuntos(BigDecimal descuentoPuntos) { this.descuentoPuntos = descuentoPuntos; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public List<PedidoItem> getItems() { return items; }
    public void setItems(List<PedidoItem> items) { this.items = items; }
    
    public Boleta getBoleta() { return boleta; }
    public void setBoleta(Boleta boleta) { this.boleta = boleta; }
    
    // Enum para estados
    public enum EstadoPedido {
        PENDIENTE, DESPACHADO, CANCELADO
    }
}