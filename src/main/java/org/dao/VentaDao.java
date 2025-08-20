package org.dao;

import org.database.DatabaseConnection;
import org.models.Venta;
import org.models.VentaProducto;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de Venta
 */
public class VentaDao implements CrudDao<Venta> {

    private final DatabaseConnection dbConnection;
    private final UsuarioDao usuarioDao;
    private final VentaProductoDao ventaProductoDao;

    public VentaDao() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.usuarioDao = new UsuarioDao();
        this.ventaProductoDao = new VentaProductoDao();
    }

    @Override
    public Integer insert(Venta venta) {
        String sql = "INSERT INTO venta (fecha, idUsuario, total) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
            stmt.setInt(2, venta.getIdUsuario());
            stmt.setBigDecimal(3, venta.getTotal());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Integer id = generatedKeys.getInt(1);
                        venta.setIdVenta(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Venta findById(Integer id) {
        String sql = "SELECT * FROM venta WHERE idVenta = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venta venta = mapResultSetToVenta(rs);
                    // Cargar el usuario de la venta
                    venta.setUsuario(usuarioDao.findById(venta.getIdUsuario()));
                    // Cargar los productos de la venta
                    venta.setProductos(ventaProductoDao.findByVentaId(id));
                    return venta;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar venta por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Venta> findAll() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM venta ORDER BY fecha DESC";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venta venta = mapResultSetToVenta(rs);
                venta.setUsuario(usuarioDao.findById(venta.getIdUsuario()));
                venta.setProductos(ventaProductoDao.findByVentaId(venta.getIdVenta()));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las ventas: " + e.getMessage());
        }
        return ventas;
    }

    @Override
    public boolean update(Venta venta) {
        String sql = "UPDATE venta SET fecha = ?, idUsuario = ?, total = ? WHERE idVenta = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
            stmt.setInt(2, venta.getIdUsuario());
            stmt.setBigDecimal(3, venta.getTotal());
            stmt.setInt(4, venta.getIdVenta());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM venta WHERE idVenta = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca ventas por usuario
     */
    public List<Venta> findByUsuario(Integer idUsuario) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM venta WHERE idUsuario = ? ORDER BY fecha DESC";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venta venta = mapResultSetToVenta(rs);
                    venta.setUsuario(usuarioDao.findById(venta.getIdUsuario()));
                    venta.setProductos(ventaProductoDao.findByVentaId(venta.getIdVenta()));
                    ventas.add(venta);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar ventas por usuario: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Busca ventas por rango de fechas
     */
    public List<Venta> findByFechaRange(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM venta WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venta venta = mapResultSetToVenta(rs);
                    venta.setUsuario(usuarioDao.findById(venta.getIdUsuario()));
                    venta.setProductos(ventaProductoDao.findByVentaId(venta.getIdVenta()));
                    ventas.add(venta);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar ventas por rango de fecha: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtiene el total de ventas por día
     */
    public BigDecimal getTotalVentasPorDia(LocalDateTime fecha) {
        String sql = "SELECT COALESCE(SUM(total), 0) as totalDia FROM venta WHERE DATE(fecha) = DATE(?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(fecha));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("totalDia");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas por día: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Obtiene las ventas del día actual
     */
    public List<Venta> getVentasHoy() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM venta WHERE DATE(fecha) = CURDATE() ORDER BY fecha DESC";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venta venta = mapResultSetToVenta(rs);
                venta.setUsuario(usuarioDao.findById(venta.getIdUsuario()));
                venta.setProductos(ventaProductoDao.findByVentaId(venta.getIdVenta()));
                ventas.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas de hoy: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Mapea un ResultSet a un objeto Venta
     */
    private Venta mapResultSetToVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(rs.getInt("idVenta"));
        venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        venta.setIdUsuario(rs.getInt("idUsuario"));
        venta.setTotal(rs.getBigDecimal("total"));
        return venta;
    }
}
