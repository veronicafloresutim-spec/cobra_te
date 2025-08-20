package org.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.dao.UsuarioDao;
import org.models.Usuario;
import org.utils.SessionManager;
import org.utils.ErrorMessages;

import java.io.IOException;

/**
 * Controlador para la pantalla de inicio de sesión
 */
public class LoginController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegistro;
    @FXML
    private Label lblError;
    @FXML
    private ProgressIndicator progressIndicator;

    private UsuarioDao usuarioDao;

    public void initialize() {
        usuarioDao = new UsuarioDao();
        progressIndicator.setVisible(false);
        lblError.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError(ErrorMessages.getEmptyFieldsError());
            return;
        }

        // Mostrar indicador de carga
        progressIndicator.setVisible(true);
        btnLogin.setDisable(true);
        lblError.setVisible(false);

        // Simular proceso de autenticación (en un hilo separado para no bloquear la UI)
        new Thread(() -> {
            try {
                Usuario usuario = usuarioDao.authenticate(email, password);

                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    btnLogin.setDisable(false);

                    if (usuario != null) {
                        // Guardar sesión del usuario
                        SessionManager.getInstance().setCurrentUser(usuario);

                        // Abrir ventana principal
                        openMainWindow();

                        // Cerrar ventana de login
                        Stage currentStage = (Stage) btnLogin.getScene().getWindow();
                        currentStage.close();
                    } else {
                        showError(ErrorMessages.getAuthenticationError());
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    btnLogin.setDisable(false);
                    showError(ErrorMessages.getDatabaseConnectionError(e.getMessage()));
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        System.exit(0);
    }

    @FXML
    private void handleRegistro() {
        try {
            // Cargar la pantalla de registro
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/cobra_te/views/registro-view.fxml"));
            Parent root = loader.load();

            // Obtener el stage actual y cambiar la escena
            Stage stage = (Stage) btnRegistro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Cobra Te - Registro de Cajero");

        } catch (IOException e) {
            e.printStackTrace();
            showError(ErrorMessages.getScreenLoadError("registro"));
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cobra_te/views/main-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Cobra Te - Sistema POS");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError(ErrorMessages.getScreenLoadError("principal"));
        }
    }
}
