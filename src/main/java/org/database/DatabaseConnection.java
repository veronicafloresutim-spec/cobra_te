package org.example.cobra_te.database;

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
    private String password = "";

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
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo de configuración, usando valores por defecto");
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
