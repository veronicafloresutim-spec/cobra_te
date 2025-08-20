package org.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase modelo para la entidad Venta
 */
public class Venta {
    private Integer idVenta;
    private LocalDateTime fecha;
    private Integer idUsuario;
    private BigDecimal total;
    private Usuario usuario;
    private List<VentaProducto> productos;

    // Constructor vacío
    public Venta() {
    }

    // Constructor con parámetros
    public Venta(LocalDateTime fecha, Integer idUsuario, BigDecimal total) {
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.total = total;
    }

    // Getters y Setters
    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<VentaProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<VentaProducto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", fecha=" + fecha +
                ", idUsuario=" + idUsuario +
                ", total=" + total +
                '}';
    }
}
