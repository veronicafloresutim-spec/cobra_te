package org.models;

import java.math.BigDecimal;
import java.util.List;

/**
 * Clase modelo para la entidad Producto
 */
public class Producto {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private String tamano;
    private BigDecimal precio;
    private List<Categoria> categorias;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros
    public Producto(String nombre, String descripcion, String tamano, BigDecimal precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tamano = tamano;
        this.precio = precio;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tamano='" + tamano + '\'' +
                ", precio=" + precio +
                '}';
    }
}
