package org.example.cobra_te.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.cobra_te.dao.UsuarioDao;
import org.example.cobra_te.models.Usuario;
import org.example.cobra_te.utils.SessionManager;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de usuarios
 */
public class UsuariosController {

    @FXML
    private TableView<Usuario> tableUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> colId;
    @FXML
    private TableColumn<Usuario, String> colNombres;
    @FXML
    private TableColumn<Usuario, String> colApellidoPaterno;
    @FXML
    private TableColumn<Usuario, String> colApellidoMaterno;
    @FXML
    private TableColumn<Usuario, String> colCorreo;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, String> colTelefono;
    @FXML
    private TableColumn<Usuario, String> colSexo;

    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbFiltroRol;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnRefresh;

    // Campos del formulario
    @FXML
    private VBox panelFormulario;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML
    private TextField txtApellidoMaterno;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private ComboBox<String> cmbSexo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblTituloFormulario;

    private UsuarioDao usuarioDao;
    private ObservableList<Usuario> listaUsuarios;
    private Usuario usuarioEnEdicion;
    private boolean modoEdicion = false;

    public void initialize() {
        usuarioDao = new UsuarioDao();
        listaUsuarios = FXCollections.observableArrayList();

        setupTableView();
        setupFormulario();
        cargarUsuarios();
        setupEventHandlers();

        // Verificar permisos
        if (!SessionManager.getInstance().isAdmin()) {
            btnNuevo.setDisable(true);
            btnEditar.setDisable(true);
            btnEliminar.setDisable(true);
        }
    }

    private void setupTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));

        tableUsuarios.setItems(listaUsuarios);

        // Doble click para editar
        tableUsuarios.setRowFactory(tv -> {
            TableRow<Usuario> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() && SessionManager.getInstance().isAdmin()) {
                    editarUsuario(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupFormulario() {
        // Configurar ComboBox
        cmbRol.getItems().addAll("cajero", "administrador");
        cmbSexo.getItems().addAll("H", "M");
        cmbFiltroRol.getItems().addAll("Todos", "cajero", "administrador");
        cmbFiltroRol.getSelectionModel().selectFirst();

        // Ocultar formulario inicialmente
        panelFormulario.setVisible(false);
    }

    private void cargarUsuarios() {
        try {
            String filtroRol = cmbFiltroRol.getSelectionModel().getSelectedItem();
            List<Usuario> usuarios;

            if ("Todos".equals(filtroRol)) {
                usuarios = usuarioDao.findAll();
            } else {
                usuarios = usuarioDao.findByRol(filtroRol);
            }

            String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
            if (!textoBusqueda.isEmpty()) {
                usuarios = usuarios.stream()
                        .filter(u -> u.getNombres().toLowerCase().contains(textoBusqueda) ||
                                u.getApellidoPaterno().toLowerCase().contains(textoBusqueda) ||
                                u.getApellidoMaterno().toLowerCase().contains(textoBusqueda) ||
                                u.getCorreo().toLowerCase().contains(textoBusqueda))
                        .toList();
            }

            listaUsuarios.setAll(usuarios);
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> cargarUsuarios());
        cmbFiltroRol.setOnAction(e -> cargarUsuarios());

        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEditar.setDisable(newSelection == null || !SessionManager.getInstance().isAdmin());
            btnEliminar.setDisable(newSelection == null || !SessionManager.getInstance().isAdmin() ||
                    (newSelection != null && newSelection.getIdUsuario()
                            .equals(SessionManager.getInstance().getCurrentUser().getIdUsuario())));
        });
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        usuarioEnEdicion = null;
        limpiarFormulario();
        lblTituloFormulario.setText("Nuevo Usuario");
        panelFormulario.setVisible(true);
    }

    @FXML
    private void handleEditar() {
        Usuario selected = tableUsuarios.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editarUsuario(selected);
        } else {
            showAlert("Selección requerida", "Por favor seleccione un usuario para editar.");
        }
    }

    private void editarUsuario(Usuario usuario) {
        modoEdicion = true;
        usuarioEnEdicion = usuario;
        cargarDatosEnFormulario(usuario);
        lblTituloFormulario.setText("Editar Usuario");
        panelFormulario.setVisible(true);
    }

    @FXML
    private void handleEliminar() {
        Usuario selected = tableUsuarios.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selección requerida", "Por favor seleccione un usuario para eliminar.");
            return;
        }

        // No permitir eliminar el usuario actual
        if (selected.getIdUsuario().equals(SessionManager.getInstance().getCurrentUser().getIdUsuario())) {
            showAlert("Operación no permitida", "No puede eliminar su propio usuario.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este usuario?");
        alert.setContentText("Usuario: " + selected.getNombreCompleto() + "\nEsta acción no se puede deshacer.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (usuarioDao.delete(selected.getIdUsuario())) {
                    showAlert("Éxito", "Usuario eliminado correctamente.");
                    cargarUsuarios();
                } else {
                    showAlert("Error", "No se pudo eliminar el usuario.");
                }
            } catch (Exception e) {
                showAlert("Error", "Error al eliminar usuario: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        cargarUsuarios();
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            Usuario usuario = obtenerUsuarioDeFormulario();

            if (modoEdicion) {
                usuario.setIdUsuario(usuarioEnEdicion.getIdUsuario());
                if (usuarioDao.update(usuario)) {
                    showAlert("Éxito", "Usuario actualizado correctamente.");
                } else {
                    showAlert("Error", "No se pudo actualizar el usuario.");
                    return;
                }
            } else {
                Integer id = usuarioDao.insert(usuario);
                if (id != null) {
                    showAlert("Éxito", "Usuario creado correctamente.");
                } else {
                    showAlert("Error", "No se pudo crear el usuario.");
                    return;
                }
            }

            handleCancelar();
            cargarUsuarios();
        } catch (Exception e) {
            showAlert("Error", "Error al guardar usuario: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        panelFormulario.setVisible(false);
        limpiarFormulario();
        modoEdicion = false;
        usuarioEnEdicion = null;
    }

    private boolean validarFormulario() {
        if (txtNombres.getText().trim().isEmpty()) {
            showAlert("Validación", "El nombre es requerido.");
            txtNombres.requestFocus();
            return false;
        }

        if (txtApellidoPaterno.getText().trim().isEmpty()) {
            showAlert("Validación", "El apellido paterno es requerido.");
            txtApellidoPaterno.requestFocus();
            return false;
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            showAlert("Validación", "El correo es requerido.");
            txtCorreo.requestFocus();
            return false;
        }

        if (!txtCorreo.getText().contains("@")) {
            showAlert("Validación", "El correo no tiene un formato válido.");
            txtCorreo.requestFocus();
            return false;
        }

        if (txtContrasena.getText().trim().isEmpty()) {
            showAlert("Validación", "La contraseña es requerida.");
            txtContrasena.requestFocus();
            return false;
        }

        if (txtTelefono.getText().trim().length() != 10) {
            showAlert("Validación", "El teléfono debe tener 10 dígitos.");
            txtTelefono.requestFocus();
            return false;
        }

        if (cmbRol.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validación", "Debe seleccionar un rol.");
            return false;
        }

        return true;
    }

    private Usuario obtenerUsuarioDeFormulario() {
        Usuario usuario = new Usuario();
        usuario.setNombres(txtNombres.getText().trim());
        usuario.setApellidoPaterno(txtApellidoPaterno.getText().trim());
        usuario.setApellidoMaterno(txtApellidoMaterno.getText().trim());
        usuario.setCorreo(txtCorreo.getText().trim());
        usuario.setContrasena(txtContrasena.getText());
        usuario.setTelefono(txtTelefono.getText().trim());
        usuario.setRol(cmbRol.getSelectionModel().getSelectedItem());
        usuario.setSexo(cmbSexo.getSelectionModel().getSelectedItem());
        return usuario;
    }

    private void cargarDatosEnFormulario(Usuario usuario) {
        txtNombres.setText(usuario.getNombres());
        txtApellidoPaterno.setText(usuario.getApellidoPaterno());
        txtApellidoMaterno.setText(usuario.getApellidoMaterno());
        txtCorreo.setText(usuario.getCorreo());
        txtContrasena.setText(usuario.getContrasena());
        txtTelefono.setText(usuario.getTelefono());
        cmbRol.getSelectionModel().select(usuario.getRol());
        cmbSexo.getSelectionModel().select(usuario.getSexo());
    }

    private void limpiarFormulario() {
        txtNombres.clear();
        txtApellidoPaterno.clear();
        txtApellidoMaterno.clear();
        txtCorreo.clear();
        txtContrasena.clear();
        txtTelefono.clear();
        cmbRol.getSelectionModel().clearSelection();
        cmbSexo.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
