package org.example.cobra_te.dao;

import org.example.cobra_te.database.DatabaseConnection;
import org.example.cobra_te.models.Usuario;
import org.example.cobra_te.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD de Usuario
 */
public class UsuarioDao implements CrudDao<Usuario> {

    private final DatabaseConnection dbConnection;

    public UsuarioDao() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Integer insert(Usuario usuario) {
        String sql = "INSERT INTO usuario (rol, contrasena, nombres, apellidoPaterno, " +
                "apellidoMaterno, correo, telefono, sexo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getRol());
            // Hash de la contraseña antes de guardarla
            stmt.setString(2, PasswordUtils.hashPassword(usuario.getContrasena()));
            stmt.setString(3, usuario.getNombres());
            stmt.setString(4, usuario.getApellidoPaterno());
            stmt.setString(5, usuario.getApellidoMaterno());
            stmt.setString(6, usuario.getCorreo());
            stmt.setString(7, usuario.getTelefono());
            stmt.setString(8, usuario.getSexo());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Integer id = generatedKeys.getInt(1);
                        usuario.setIdUsuario(id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Usuario findById(Integer id) {
        String sql = "SELECT * FROM usuario WHERE idUsuario = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY apellidoPaterno, apellidoMaterno, nombres";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public boolean update(Usuario usuario) {
        String sql = "UPDATE usuario SET rol = ?, contrasena = ?, nombres = ?, apellidoPaterno = ?, " +
                "apellidoMaterno = ?, correo = ?, telefono = ?, sexo = ? WHERE idUsuario = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getRol());
            // Hash de la contraseña antes de actualizarla
            stmt.setString(2, PasswordUtils.hashPassword(usuario.getContrasena()));
            stmt.setString(3, usuario.getNombres());
            stmt.setString(4, usuario.getApellidoPaterno());
            stmt.setString(5, usuario.getApellidoMaterno());
            stmt.setString(6, usuario.getCorreo());
            stmt.setString(7, usuario.getTelefono());
            stmt.setString(8, usuario.getSexo());
            stmt.setInt(9, usuario.getIdUsuario());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM usuario WHERE idUsuario = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Busca un usuario por su correo electrónico
     */
    public Usuario findByEmail(String correo) {
        String sql = "SELECT * FROM usuario WHERE correo = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por correo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Autentica un usuario con correo y contraseña
     */
    public Usuario authenticate(String correo, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE correo = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("contrasena");
                    // Verificar la contraseña usando el hash
                    if (PasswordUtils.verifyPassword(contrasena, hashedPassword)) {
                        return mapResultSetToUsuario(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> findByRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE rol = ? ORDER BY apellidoPaterno, apellidoMaterno, nombres";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por rol: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Verifica si un correo ya está registrado
     */
    public boolean existsByCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE correo = ?";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de correo: " + e.getMessage());
        }
        return false;
    }

    /**
     * Registra un nuevo usuario (específico para registro de cajeros)
     */
    public boolean registerCajero(String nombres, String apellidoPaterno, String apellidoMaterno,
            String correo, String telefono, String sexo, String contrasena) {

        // Verificar que el correo no exista
        if (existsByCorreo(correo)) {
            return false;
        }

        Usuario nuevoCajero = new Usuario();
        nuevoCajero.setRol("cajero");
        nuevoCajero.setNombres(nombres);
        nuevoCajero.setApellidoPaterno(apellidoPaterno);
        nuevoCajero.setApellidoMaterno(apellidoMaterno);
        nuevoCajero.setCorreo(correo);
        nuevoCajero.setTelefono(telefono);
        nuevoCajero.setSexo(sexo);
        nuevoCajero.setContrasena(contrasena); // Se hasheará automáticamente en el insert

        return insert(nuevoCajero) != null;
    }

    /**
     * Mapea un ResultSet a un objeto Usuario
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("idUsuario"));
        usuario.setRol(rs.getString("rol"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setNombres(rs.getString("nombres"));
        usuario.setApellidoPaterno(rs.getString("apellidoPaterno"));
        usuario.setApellidoMaterno(rs.getString("apellidoMaterno"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setSexo(rs.getString("sexo"));
        return usuario;
    }
}
