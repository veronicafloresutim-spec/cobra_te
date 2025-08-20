package org.example.cobra_te.dao;

import org.example.cobra_te.database.DatabaseConnection;
import org.example.cobra_te.models.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de Categoria
 */
public class CategoriaDao implements CrudDao<Categoria> {

    private final DatabaseConnection dbConnection;

    public CategoriaDao() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Integer insert(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Integer id = generatedKeys.getInt(1);
                        categoria.setIdCategoria(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar categoría: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Categoria findById(Integer id) {
        String sql = "SELECT * FROM categoria WHERE idCategoria = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar categoría por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Categoria> findAll() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria ORDER BY nombre";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las categorías: " + e.getMessage());
        }
        return categorias;
    }

    @Override
    public boolean update(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? WHERE idCategoria = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setInt(3, categoria.getIdCategoria());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM categoria WHERE idCategoria = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca categorías por nombre (búsqueda parcial)
     */
    public List<Categoria> findByNombre(String nombre) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria WHERE nombre LIKE ? ORDER BY nombre";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar categorías por nombre: " + e.getMessage());
        }
        return categorias;
    }

    /**
     * Obtiene las categorías de un producto específico
     */
    public List<Categoria> findByProductoId(Integer idProducto) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT c.* FROM categoria c " +
                "INNER JOIN productoCategoria pc ON c.idCategoria = pc.idCategoria " +
                "WHERE pc.idProducto = ? ORDER BY c.nombre";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías del producto: " + e.getMessage());
        }
        return categorias;
    }

    /**
     * Mapea un ResultSet a un objeto Categoria
     */
    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("idCategoria"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }
}
