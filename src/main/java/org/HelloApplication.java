package org;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.database.DatabaseUtils;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar base de datos
        try {
            DatabaseUtils dbUtils = new DatabaseUtils();
            dbUtils.initializeTables();
            dbUtils.insertSampleData();
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }

        // Cargar pantalla de login
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/org/example/cobra_te/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("Cobra Te - Inicio de Sesión");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
