package org.models;

/**
 * Clase modelo para la entidad VentaProducto (tabla de relación)
 */
public class VentaProducto {
    private Integer idVenta;
    private Integer idProducto;
    private Integer cantidad;
    private Producto producto;

    // Constructor vacío
    public VentaProducto() {
    }

    // Constructor con parámetros
    public VentaProducto(Integer idVenta, Integer idProducto, Integer cantidad) {
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "VentaProducto{" +
                "idVenta=" + idVenta +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                '}';
    }
}
