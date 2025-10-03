package org;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.utils.ErrorMessages;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Cargar pantalla de login
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource("/views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 800);
        stage.setTitle("Cobra Te - Inicio de Sesión");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }
}
