package org.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.dao.UsuarioDao;
import org.utils.PasswordUtils;
import org.utils.ErrorMessages;

import java.io.IOException;

/**
 * Controlador para la pantalla de registro de nuevos cajeros
 */
public class RegistroController {

    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML
    private TextField txtApellidoMaterno;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox<String> cmbSexo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private PasswordField txtConfirmarContrasena;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblMensaje;
    @FXML
    private ProgressIndicator progressIndicator;

    private UsuarioDao usuarioDao;
    private Stage stage;

    @FXML
    private void initialize() {
        usuarioDao = new UsuarioDao();

        // Configurar ComboBox de sexo
        cmbSexo.getItems().addAll("Masculino", "Femenino", "Otro");

        // Ocultar indicador de progreso inicialmente
        progressIndicator.setVisible(false);

        // Limpiar mensaje inicial
        lblMensaje.setText("");

        // Configurar validación en tiempo real
        setupValidation();
    }

    private void setupValidation() {
        // Validar contraseña en tiempo real
        txtContrasena.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePassword();
        });

        // Validar confirmación de contraseña
        txtConfirmarContrasena.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordConfirmation();
        });

        // Validar correo
        txtCorreo.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail();
        });
    }

    private void validatePassword() {
        String password = txtContrasena.getText();
        String message = PasswordUtils.getPasswordValidationMessage(password);

        if (!message.isEmpty() && !password.isEmpty()) {
            lblMensaje.setText(message);
            lblMensaje.setStyle("-fx-text-fill: red;");
        } else if (!password.isEmpty()) {
            lblMensaje.setText("Contraseña válida");
            lblMensaje.setStyle("-fx-text-fill: green;");
        } else {
            lblMensaje.setText("");
        }
    }

    private void validatePasswordConfirmation() {
        String password = txtContrasena.getText();
        String confirmation = txtConfirmarContrasena.getText();

        if (!confirmation.isEmpty()) {
            if (!password.equals(confirmation)) {
                lblMensaje.setText("Las contraseñas no coinciden");
                lblMensaje.setStyle("-fx-text-fill: red;");
            } else if (PasswordUtils.isValidPassword(password)) {
                lblMensaje.setText("Las contraseñas coinciden");
                lblMensaje.setStyle("-fx-text-fill: green;");
            }
        }
    }

    private void validateEmail() {
        String email = txtCorreo.getText();
        if (!email.isEmpty() && !isValidEmail(email)) {
            lblMensaje.setText("Formato de correo inválido");
            lblMensaje.setStyle("-fx-text-fill: red;");
        } else if (!email.isEmpty()) {
            // Verificar si el correo ya existe
            if (usuarioDao.existsByCorreo(email)) {
                lblMensaje.setText("Este correo ya está registrado");
                lblMensaje.setStyle("-fx-text-fill: red;");
            } else {
                lblMensaje.setText("Correo disponible");
                lblMensaje.setStyle("-fx-text-fill: green;");
            }
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @FXML
    private void handleRegistrar() {
        if (!validateForm()) {
            return;
        }

        progressIndicator.setVisible(true);
        btnRegistrar.setDisable(true);

        try {
            String nombres = txtNombres.getText().trim();
            String apellidoPaterno = txtApellidoPaterno.getText().trim();
            String apellidoMaterno = txtApellidoMaterno.getText().trim();
            String correo = txtCorreo.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String sexo = cmbSexo.getValue();
            String contrasena = txtContrasena.getText();

            boolean registrado = usuarioDao.registerCajero(
                    nombres, apellidoPaterno, apellidoMaterno,
                    correo, telefono, sexo, contrasena);

            if (registrado) {
                showSuccessAlert("Registro exitoso",
                        "El cajero " + nombres + " " + apellidoPaterno + " se ha registrado correctamente.");

                // Volver a la pantalla de login
                handleCancelar();
            } else {
                showErrorAlert("Error de registro",
                        ErrorMessages.getRegistrationError("correo electrónico (puede estar en uso)"));
            }

        } catch (Exception e) {
            showErrorAlert("Error", ErrorMessages.getDatabaseConnectionError(e.getMessage()));
        } finally {
            progressIndicator.setVisible(false);
            btnRegistrar.setDisable(false);
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        errors.append("📝 Por favor corrige los siguientes campos:\n\n");

        // Validar campos requeridos
        if (txtNombres.getText().trim().isEmpty()) {
            errors.append("• Nombre es obligatorio\n");
        }

        if (txtApellidoPaterno.getText().trim().isEmpty()) {
            errors.append("• Apellido paterno es obligatorio\n");
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            errors.append("• Correo electrónico es obligatorio\n");
        } else if (!isValidEmail(txtCorreo.getText().trim())) {
            errors.append("• Formato de correo inválido (ej: usuario@dominio.com)\n");
        } else if (usuarioDao.existsByCorreo(txtCorreo.getText().trim())) {
            errors.append("• Este correo ya está registrado en el sistema\n");
        }

        if (cmbSexo.getValue() == null) {
            errors.append("• Selecciona el sexo\n");
        }

        // Validar contraseña
        String password = txtContrasena.getText();
        String passwordError = PasswordUtils.getPasswordValidationMessage(password);
        if (!passwordError.isEmpty()) {
            errors.append("• Contraseña: ").append(passwordError).append("\n");
        }

        // Validar confirmación de contraseña
        if (!txtContrasena.getText().equals(txtConfirmarContrasena.getText())) {
            errors.append("• Las contraseñas no coinciden\n");
        }

        if (errors.length() > 40) { // Más que solo el header
            showErrorAlert("❌ Formulario incompleto", errors.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancelar() {
        try {
            // Cargar la pantalla de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Cobra Te - Inicio de Sesión");

        } catch (IOException e) {
            showErrorAlert("Error", ErrorMessages.getScreenLoadError("login"));
        }
    }

    @FXML
    private void handleVolver() {
        try {
            // Cargar la pantalla de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Cobra Te - Inicio de Sesión");

        } catch (IOException e) {
            showErrorAlert("Error", ErrorMessages.getScreenLoadError("login"));
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
