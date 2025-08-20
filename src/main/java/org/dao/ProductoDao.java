package org.example.cobra_te.dao;

import org.example.cobra_te.database.DatabaseConnection;
import org.example.cobra_te.models.Producto;
import org.example.cobra_te.models.Categoria;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de Producto
 */
public class ProductoDao implements CrudDao<Producto> {

    private final DatabaseConnection dbConnection;
    private final CategoriaDao categoriaDao;

    public ProductoDao() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.categoriaDao = new CategoriaDao();
    }

    @Override
    public Integer insert(Producto producto) {
        String sql = "INSERT INTO producto (nombre, descripcion, tamano, precio) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getTamano());
            stmt.setBigDecimal(4, producto.getPrecio());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Integer id = generatedKeys.getInt(1);
                        producto.setIdProducto(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Producto findById(Integer id) {
        String sql = "SELECT * FROM producto WHERE idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Producto producto = mapResultSetToProducto(rs);
                    // Cargar las categorías del producto
                    producto.setCategorias(categoriaDao.findByProductoId(id));
                    return producto;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Producto> findAll() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto ORDER BY nombre";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Producto producto = mapResultSetToProducto(rs);
                // Cargar las categorías del producto
                producto.setCategorias(categoriaDao.findByProductoId(producto.getIdProducto()));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los productos: " + e.getMessage());
        }
        return productos;
    }

    @Override
    public boolean update(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, tamano = ?, precio = ? WHERE idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setString(3, producto.getTamano());
            stmt.setBigDecimal(4, producto.getPrecio());
            stmt.setInt(5, producto.getIdProducto());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM producto WHERE idProducto = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca productos por nombre (búsqueda parcial)
     */
    public List<Producto> findByNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre LIKE ? ORDER BY nombre";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = mapResultSetToProducto(rs);
                    producto.setCategorias(categoriaDao.findByProductoId(producto.getIdProducto()));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Busca productos por categoría
     */
    public List<Producto> findByCategoria(Integer idCategoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.* FROM producto p " +
                "INNER JOIN productoCategoria pc ON p.idProducto = pc.idProducto " +
                "WHERE pc.idCategoria = ? ORDER BY p.nombre";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCategoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = mapResultSetToProducto(rs);
                    producto.setCategorias(categoriaDao.findByProductoId(producto.getIdProducto()));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por categoría: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Busca productos por rango de precio
     */
    public List<Producto> findByPrecioRange(BigDecimal precioMin, BigDecimal precioMax) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE precio BETWEEN ? AND ? ORDER BY precio";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, precioMin);
            stmt.setBigDecimal(2, precioMax);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = mapResultSetToProducto(rs);
                    producto.setCategorias(categoriaDao.findByProductoId(producto.getIdProducto()));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por rango de precio: " + e.getMessage());
        }
        return productos;
    }

    /**
     * Asigna una categoría a un producto
     */
    public boolean asignarCategoria(Integer idProducto, Integer idCategoria) {
        String sql = "INSERT INTO productoCategoria (idProducto, idCategoria) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE idProducto = idProducto";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            stmt.setInt(2, idCategoria);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al asignar categoría al producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Desasigna una categoría de un producto
     */
    public boolean desasignarCategoria(Integer idProducto, Integer idCategoria) {
        String sql = "DELETE FROM productoCategoria WHERE idProducto = ? AND idCategoria = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            stmt.setInt(2, idCategoria);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desasignar categoría del producto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto Producto
     */
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("idProducto"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setTamano(rs.getString("tamano"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        return producto;
    }
}
