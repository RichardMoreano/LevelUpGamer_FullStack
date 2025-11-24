package cl.duoc.levelup.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para exponer boletas al frontend sin enviar entidades completas.
 */
public class BoletaDTO {

    private String numero;
    private LocalDate fecha;
    private LocalDateTime fechaCreacion;

    private Long pedidoId;

    private String clienteNombre;
    private String clienteCorreo;

    private BigDecimal subtotal;
    private BigDecimal descuentoDuoc;
    private BigDecimal descuentoPuntos;
    private BigDecimal total;

    public BoletaDTO() {
    }

    public BoletaDTO(String numero,
                     LocalDate fecha,
                     LocalDateTime fechaCreacion,
                     Long pedidoId,
                     String clienteNombre,
                     String clienteCorreo,
                     BigDecimal subtotal,
                     BigDecimal descuentoDuoc,
                     BigDecimal descuentoPuntos,
                     BigDecimal total) {
        this.numero = numero;
        this.fecha = fecha;
        this.fechaCreacion = fechaCreacion;
        this.pedidoId = pedidoId;
        this.clienteNombre = clienteNombre;
        this.clienteCorreo = clienteCorreo;
        this.subtotal = subtotal;
        this.descuentoDuoc = descuentoDuoc;
        this.descuentoPuntos = descuentoPuntos;
        this.total = total;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteCorreo() {
        return clienteCorreo;
    }

    public void setClienteCorreo(String clienteCorreo) {
        this.clienteCorreo = clienteCorreo;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuentoDuoc() {
        return descuentoDuoc;
    }

    public void setDescuentoDuoc(BigDecimal descuentoDuoc) {
        this.descuentoDuoc = descuentoDuoc;
    }

    public BigDecimal getDescuentoPuntos() {
        return descuentoPuntos;
    }

    public void setDescuentoPuntos(BigDecimal descuentoPuntos) {
        this.descuentoPuntos = descuentoPuntos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
