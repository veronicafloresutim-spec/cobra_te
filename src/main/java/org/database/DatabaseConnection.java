package org.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Clase para manejar la conexión a la base de datos MariaDB
 */
public class DatabaseConnection {
    private static final String CONFIG_FILE = "/database.properties";
    private static DatabaseConnection instance;
    private Connection connection;

    // Configuración por defecto
    private String url = "jdbc:mariadb://localhost:3306/cobra_te";
    private String username = "root";
    private String password = "1234"; // Cambiar valor por defecto

    private DatabaseConnection() {
        loadConfiguration();
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver de MariaDB no encontrado", e);
        }
    }

    /**
     * Obtiene la instancia única de DatabaseConnection (Singleton)
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Carga la configuración desde el archivo properties
     */
    private void loadConfiguration() {
        try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);

                url = prop.getProperty("database.url", url);
                username = prop.getProperty("database.username", username);
                password = prop.getProperty("database.password", password);

                // Debug: verificar que se cargó la configuración
                System.out.println("Configuración cargada:");
                System.out.println("URL: " + url);
                System.out.println("Username: " + username);
                System.out.println("Password: " + (password.isEmpty() ? "[VACÍA]" : "[CONFIGURADA]"));
            } else {
                System.err.println("❌ No se encontró el archivo " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al cargar configuración: " + e.getMessage());
            System.out.println("Usando valores por defecto");
        }
    }

    /**
     * Obtiene una conexión a la base de datos
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    /**
     * Verifica si la conexión está activa
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Configura manualmente los parámetros de conexión
     */
    public void setConnectionParameters(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        closeConnection(); // Cerrar conexión actual para forzar nueva conexión
    }
}
