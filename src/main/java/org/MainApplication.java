package org;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.database.DatabaseUtils;
import org.utils.ErrorMessages;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar base de datos
        try {
            DatabaseUtils dbUtils = new DatabaseUtils();
            dbUtils.initializeTables();
            dbUtils.insertSampleData();
        } catch (Exception e) {
            String friendlyMessage = ErrorMessages.getDatabaseConnectionError(e.getMessage());
            System.err.println(friendlyMessage);
        }

        // Cargar pantalla de login
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("/org/example/cobra_te/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("Cobra Te - Inicio de Sesión");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
