package cl.duoc.levelup.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @Column(name = "run", length = 12)
    private String run;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
    
    @Email
    @NotBlank
    @Column(name = "correo", nullable = false, unique = true, length = 120)
    private String correo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;
    
    @Column(name = "region", length = 100)
    private String region;
    
    @Column(name = "comuna", length = 100)
    private String comuna;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @NotBlank
    @Size(min = 4, max = 255)
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "puntos_levelup", nullable = false)
    private Integer puntosLevelUp = 0;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    // Relaciones
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;
    
    // Constructores
    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public Usuario(String run, String nombres, String apellidos, String correo, 
                   TipoUsuario tipoUsuario, String password) {
        this();
        this.run = run;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.tipoUsuario = tipoUsuario;
        this.password = password;
    }
    
    // Métodos de negocio
    public boolean tieneDescuentoDuoc() {
        return correo != null && correo.toLowerCase().endsWith("@duoc.cl");
    }
    
    public double calcularDescuentoDuoc() {
        return tieneDescuentoDuoc() ? 0.20 : 0.0;
    }
    
    public double calcularDescuentoPuntos(double subtotal) {
        if (puntosLevelUp <= 0) return 0.0;
        
        double puntosEnPesos = puntosLevelUp * 10.0; // 1 punto = $10 CLP
        double maxDescuento = subtotal * 0.20; // Máximo 20% del subtotal
        
        return Math.min(puntosEnPesos, maxDescuento);
    }
    
    public void usarPuntos(int puntosUsados) {
        if (puntosUsados > 0 && puntosUsados <= this.puntosLevelUp) {
            this.puntosLevelUp -= puntosUsados;
        }
    }
    
    public void ganarPuntos(int puntosGanados) {
        if (puntosGanados > 0) {
            this.puntosLevelUp += puntosGanados;
        }
    }
    
    // Getters y Setters
    public String getRun() { return run; }
    public void setRun(String run) { this.run = run; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Integer getPuntosLevelUp() { return puntosLevelUp; }
    public void setPuntosLevelUp(Integer puntosLevelUp) { this.puntosLevelUp = puntosLevelUp; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }
    
    // Enum para tipos de usuario
    public enum TipoUsuario {
        ADMIN, VENDEDOR, CLIENTE
    }
}