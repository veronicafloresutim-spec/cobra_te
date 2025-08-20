package org.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.utils.SessionManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador principal de la aplicación
 */
public class MainController {

    @FXML
    private BorderPane mainContainer;
    @FXML
    private Label lblUserInfo;
    @FXML
    private Label lblDateTime;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem menuVentas;
    @FXML
    private MenuItem menuUsuarios;
    @FXML
    private MenuItem menuCategorias;
    @FXML
    private MenuItem menuProductos;
    @FXML
    private MenuItem menuReportes;
    @FXML
    private VBox centerContent;

    public void initialize() {
        updateUserInfo();
        updateDateTime();
        setupMenuAccess();

        // Actualizar fecha/hora cada minuto
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.minutes(1), e -> updateDateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Cargar vista por defecto (POS)
        loadPOSView();
    }

    private void updateUserInfo() {
        SessionManager session = SessionManager.getInstance();
        if (session.isLoggedIn()) {
            String role = session.isAdmin() ? "Administrador" : "Cajero";
            lblUserInfo.setText(session.getCurrentUserName() + " (" + role + ")");
        }
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblDateTime.setText(now.format(formatter));
    }

    private void setupMenuAccess() {
        SessionManager session = SessionManager.getInstance();

        // Solo los administradores pueden acceder a gestión de usuarios, categorías y
        // productos
        boolean isAdmin = session.isAdmin();
        menuUsuarios.setDisable(!isAdmin);
        menuCategorias.setDisable(!isAdmin);
        menuProductos.setDisable(!isAdmin);
        menuReportes.setDisable(!isAdmin);
    }

    @FXML
    private void handleVentas() {
        loadPOSView();
    }

    @FXML
    private void handleUsuarios() {
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert("Acceso Denegado", "Solo los administradores pueden gestionar usuarios");
            return;
        }
        loadView("/org/example/cobra_te/views/usuarios-view.fxml", "Gestión de Usuarios");
    }

    @FXML
    private void handleCategorias() {
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert("Acceso Denegado", "Solo los administradores pueden gestionar categorías");
            return;
        }
        loadView("/org/example/cobra_te/views/categorias-view.fxml", "Gestión de Categorías");
    }

    @FXML
    private void handleProductos() {
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert("Acceso Denegado", "Solo los administradores pueden gestionar productos");
            return;
        }
        loadView("/org/example/cobra_te/views/productos-view.fxml", "Gestión de Productos");
    }

    @FXML
    private void handleReportes() {
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert("Acceso Denegado", "Solo los administradores pueden ver reportes");
            return;
        }
        loadView("/org/example/cobra_te/views/reportes-view.fxml", "Reportes y Estadísticas");
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Sesión");
        alert.setHeaderText("¿Está seguro que desea cerrar sesión?");
        alert.setContentText("Tendrá que volver a iniciar sesión para continuar.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            SessionManager.getInstance().logout();

            // Cerrar ventana actual
            Stage currentStage = (Stage) mainContainer.getScene().getWindow();
            currentStage.close();

            // Abrir ventana de login
            openLoginWindow();
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de Cobra Te");
        alert.setHeaderText("Sistema POS - Cobra Te");
        alert.setContentText("Sistema de Punto de Venta para Cafetería\n" +
                "Versión 1.0\n\n" +
                "Desarrollado con JavaFX y MariaDB\n" +
                "© 2025 Cobra Te");
        alert.showAndWait();
    }

    private void loadPOSView() {
        loadView("/org/example/cobra_te/views/pos-view.fxml", "Punto de Venta");
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            centerContent.getChildren().clear();
            centerContent.getChildren().add(view);

            // Actualizar título de la ventana
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setTitle("Cobra Te - " + title);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la vista: " + title);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cobra_te/views/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Cobra Te - Inicio de Sesión");
            stage.setScene(new Scene(root, 600, 500));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
