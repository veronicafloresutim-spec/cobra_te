package org.dao;

import org.database.DatabaseConnection;
import org.models.VentaProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de VentaProducto
 */
public class VentaProductoDao {

    private final DatabaseConnection dbConnection;
    private final ProductoDao productoDao;

    public VentaProductoDao() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.productoDao = new ProductoDao();
    }

    /**
     * Inserta un nuevo detalle de venta (producto en venta)
     */
    public boolean insert(VentaProducto ventaProducto) {
        String sql = "INSERT INTO ventaProducto (idVenta, idProducto, cantidad) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE cantidad = cantidad + VALUES(cantidad)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ventaProducto.getIdVenta());
            stmt.setInt(2, ventaProducto.getIdProducto());
            stmt.setInt(3, ventaProducto.getCantidad());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto en venta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca los productos de una venta específica
     */
    public List<VentaProducto> findByVentaId(Integer idVenta) {
        List<VentaProducto> ventaProductos = new ArrayList<>();
        String sql = "SELECT * FROM ventaProducto WHERE idVenta = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VentaProducto ventaProducto = mapResultSetToVentaProducto(rs);
                    // Cargar información del producto
                    ventaProducto.setProducto(productoDao.findById(ventaProducto.getIdProducto()));
                    ventaProductos.add(ventaProducto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos de la venta: " + e.getMessage());
        }
        return ventaProductos;
    }

    /**
     * Busca las ventas que contienen un producto específico
     */
    public List<VentaProducto> findByProductoId(Integer idProducto) {
        List<VentaProducto> ventaProductos = new ArrayList<>();
        String sql = "SELECT * FROM ventaProducto WHERE idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VentaProducto ventaProducto = mapResultSetToVentaProducto(rs);
                    ventaProducto.setProducto(productoDao.findById(ventaProducto.getIdProducto()));
                    ventaProductos.add(ventaProducto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar ventas del producto: " + e.getMessage());
        }
        return ventaProductos;
    }

    /**
     * Actualiza la cantidad de un producto en una venta
     */
    public boolean updateCantidad(Integer idVenta, Integer idProducto, Integer nuevaCantidad) {
        String sql = "UPDATE ventaProducto SET cantidad = ? WHERE idVenta = ? AND idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevaCantidad);
            stmt.setInt(2, idVenta);
            stmt.setInt(3, idProducto);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cantidad del producto en venta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina un producto de una venta
     */
    public boolean delete(Integer idVenta, Integer idProducto) {
        String sql = "DELETE FROM ventaProducto WHERE idVenta = ? AND idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            stmt.setInt(2, idProducto);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto de la venta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Elimina todos los productos de una venta
     */
    public boolean deleteByVentaId(Integer idVenta) {
        String sql = "DELETE FROM ventaProducto WHERE idVenta = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar productos de la venta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene los productos más vendidos
     */
    public List<VentaProducto> getProductosMasVendidos(int limite) {
        List<VentaProducto> ventaProductos = new ArrayList<>();
        String sql = "SELECT idProducto, SUM(cantidad) as totalVendido " +
                "FROM ventaProducto " +
                "GROUP BY idProducto " +
                "ORDER BY totalVendido DESC " +
                "LIMIT ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VentaProducto ventaProducto = new VentaProducto();
                    ventaProducto.setIdProducto(rs.getInt("idProducto"));
                    ventaProducto.setCantidad(rs.getInt("totalVendido"));
                    ventaProducto.setProducto(productoDao.findById(ventaProducto.getIdProducto()));
                    ventaProductos.add(ventaProducto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos más vendidos: " + e.getMessage());
        }
        return ventaProductos;
    }

    /**
     * Obtiene la cantidad total vendida de un producto
     */
    public Integer getCantidadTotalVendida(Integer idProducto) {
        String sql = "SELECT COALESCE(SUM(cantidad), 0) as total FROM ventaProducto WHERE idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cantidad total vendida: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Mapea un ResultSet a un objeto VentaProducto
     */
    private VentaProducto mapResultSetToVentaProducto(ResultSet rs) throws SQLException {
        VentaProducto ventaProducto = new VentaProducto();
        ventaProducto.setIdVenta(rs.getInt("idVenta"));
        ventaProducto.setIdProducto(rs.getInt("idProducto"));
        ventaProducto.setCantidad(rs.getInt("cantidad"));
        return ventaProducto;
    }
}
