package org.example.cobra_te.examples;

import org.example.cobra_te.database.DatabaseConnection;

/**
 * Ejemplo simple para probar la conexión a la base de datos
 */
public class SimpleConnectionTest {

    public static void main(String[] args) {
        System.out.println("=== Prueba de Conexión Simple ===");

        try {
            // Configurar conexión manualmente (opcional)
            DatabaseConnection dbConn = DatabaseConnection.getInstance();

            // Cambiar estos valores por los de tu base de datos
            dbConn.setConnectionParameters(
                    "jdbc:mariadb://localhost:3306/cobra_te",
                    "root",
                    "tu_password_aqui");

            // Intentar conectar
            var connection = dbConn.getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("✓ Conexión exitosa a la base de datos MariaDB");
                System.out.println("URL: " + connection.getMetaData().getURL());
                System.out.println("Usuario: " + connection.getMetaData().getUserName());
                System.out.println("Producto: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("Versión: " + connection.getMetaData().getDatabaseProductVersion());
            } else {
                System.err.println("✗ No se pudo establecer la conexión");
            }

        } catch (Exception e) {
            System.err.println("✗ Error de conexión: " + e.getMessage());
            System.err.println("Asegúrate de que:");
            System.err.println("1. MariaDB esté instalado y ejecutándose");
            System.err.println("2. La base de datos 'cobra_te' exista");
            System.err.println("3. Las credenciales sean correctas");
            System.err.println("4. El puerto 3306 esté disponible");
        } finally {
            // Cerrar conexión
            DatabaseConnection.getInstance().closeConnection();
            System.out.println("Conexión cerrada.");
        }
    }
}
