package org.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de utilidades para operaciones de base de datos
 */
public class DatabaseUtils {

    private final DatabaseConnection dbConnection;

    public DatabaseUtils() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Ejecuta múltiples operaciones en una transacción
     */
    public boolean executeTransaction(Runnable... operations) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // Ejecutar todas las operaciones
            for (Runnable operation : operations) {
                operation.run();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error en rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Error en transacción: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error al restaurar autocommit: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si existe una tabla en la base de datos
     */
    public boolean tableExists(String tableName) {
        try (Connection conn = dbConnection.getConnection()) {
            return conn.getMetaData().getTables(null, null, tableName, null).next();
        } catch (SQLException e) {
            System.err.println("Error al verificar tabla: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ejecuta un script SQL
     */
    public boolean executeScript(String script) {
        try (Connection conn = dbConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            String[] statements = script.split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement.trim());
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al ejecutar script: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicializa las tablas si no existen
     */
    public boolean initializeTables() {
        String createTablesScript = """
                CREATE TABLE IF NOT EXISTS usuario (
                    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
                    rol ENUM('cajero', 'administrador') NOT NULL,
                    contrasena VARCHAR(255) NOT NULL,
                    nombres VARCHAR(100) NOT NULL,
                    apellidoPaterno VARCHAR(100) NOT NULL,
                    apellidoMaterno VARCHAR(100) NOT NULL,
                    correo VARCHAR(100) NOT NULL UNIQUE,
                    telefono CHAR(10) NOT NULL,
                    sexo ENUM('H', 'M') NULL
                );

                CREATE TABLE IF NOT EXISTS categoria (
                    idCategoria INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion TEXT
                );

                CREATE TABLE IF NOT EXISTS producto (
                    idProducto INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL,
                    descripcion TEXT,
                    tamano VARCHAR(50) NULL,
                    precio DECIMAL(10,2) NOT NULL
                );

                CREATE TABLE IF NOT EXISTS productoCategoria (
                    idProducto INT NOT NULL,
                    idCategoria INT NOT NULL,
                    PRIMARY KEY (idProducto, idCategoria),
                    FOREIGN KEY (idProducto) REFERENCES producto(idProducto) ON DELETE CASCADE,
                    FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria) ON DELETE CASCADE
                );

                CREATE TABLE IF NOT EXISTS venta (
                    idVenta INT AUTO_INCREMENT PRIMARY KEY,
                    fecha DATETIME NOT NULL,
                    idUsuario INT NOT NULL,
                    total DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
                );

                CREATE TABLE IF NOT EXISTS ventaProducto (
                    idVenta INT NOT NULL,
                    idProducto INT NOT NULL,
                    cantidad INT NOT NULL DEFAULT 1,
                    PRIMARY KEY (idVenta, idProducto),
                    FOREIGN KEY (idVenta) REFERENCES venta(idVenta) ON DELETE CASCADE,
                    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
                );
                """;

        return executeScript(createTablesScript);
    }

    /**
     * Inserta datos de prueba
     */
    public boolean insertSampleData() {
        String sampleDataScript = """
                INSERT IGNORE INTO usuario (rol, contrasena, nombres, apellidoPaterno, apellidoMaterno, correo, telefono, sexo)
                VALUES
                ('administrador', 'admin123', 'Juan', 'Pérez', 'García', 'admin@cobra_te.com', '5551234567', 'H'),
                ('cajero', 'cajero123', 'María', 'López', 'Rodríguez', 'cajero@cobra_te.com', '5559876543', 'M');

                INSERT IGNORE INTO categoria (nombre, descripcion)
                VALUES
                ('Bebidas Calientes', 'Café, té y otras bebidas calientes'),
                ('Bebidas Frías', 'Refrescos, jugos y bebidas frías'),
                ('Snacks', 'Botanas y aperitivos'),
                ('Postres', 'Dulces y postres');

                INSERT IGNORE INTO producto (nombre, descripcion, tamano, precio)
                VALUES
                ('Café Americano', 'Café negro tradicional', 'Grande', 25.00),
                ('Cappuccino', 'Café con espuma de leche', 'Mediano', 35.00),
                ('Frappé de Vainilla', 'Bebida fría de café con vainilla', 'Grande', 45.00),
                ('Croissant', 'Pan francés mantequilloso', 'Unidad', 20.00),
                ('Cheesecake', 'Pastel de queso', 'Rebanada', 55.00);
                """;

        return executeScript(sampleDataScript);
    }

    /**
     * Limpia todas las tablas (cuidado: elimina todos los datos)
     */
    public boolean clearAllTables() {
        String clearScript = """
                SET FOREIGN_KEY_CHECKS = 0;
                TRUNCATE TABLE ventaProducto;
                TRUNCATE TABLE venta;
                TRUNCATE TABLE productoCategoria;
                TRUNCATE TABLE producto;
                TRUNCATE TABLE categoria;
                TRUNCATE TABLE usuario;
                SET FOREIGN_KEY_CHECKS = 1;
                """;

        return executeScript(clearScript);
    }

    /**
     * Obtiene estadísticas de la base de datos
     */
    public void printDatabaseStats() {
        try (Connection conn = dbConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("=== Estadísticas de la Base de Datos ===");

            String[] tables = { "usuario", "categoria", "producto", "venta", "ventaProducto", "productoCategoria" };

            for (String table : tables) {
                var rs = stmt.executeQuery("SELECT COUNT(*) as total FROM " + table);
                if (rs.next()) {
                    System.out.println(table + ": " + rs.getInt("total") + " registros");
                }
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
    }
}
