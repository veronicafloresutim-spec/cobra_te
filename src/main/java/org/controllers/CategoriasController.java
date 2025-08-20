package org.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.dao.CategoriaDao;
import org.models.Categoria;
import org.utils.SessionManager;

import java.util.List;

/**
 * Controlador para la gestión de categorías
 */
public class CategoriasController {

    @FXML
    private TableView<Categoria> tableCategorias;
    @FXML
    private TableColumn<Categoria, Integer> colId;
    @FXML
    private TableColumn<Categoria, String> colNombre;
    @FXML
    private TableColumn<Categoria, String> colDescripcion;

    @FXML
    private TextField txtBuscar;
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
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblTituloFormulario;

    private CategoriaDao categoriaDao;
    private ObservableList<Categoria> listaCategorias;
    private Categoria categoriaEnEdicion;
    private boolean modoEdicion = false;

    public void initialize() {
        categoriaDao = new CategoriaDao();
        listaCategorias = FXCollections.observableArrayList();

        setupTableView();
        cargarCategorias();
        setupEventHandlers();

        // Verificar permisos
        if (!SessionManager.getInstance().isAdmin()) {
            btnNuevo.setDisable(true);
            btnEditar.setDisable(true);
            btnEliminar.setDisable(true);
        }

        // Ocultar formulario inicialmente
        panelFormulario.setVisible(false);
    }

    private void setupTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        tableCategorias.setItems(listaCategorias);

        // Doble click para editar
        tableCategorias.setRowFactory(tv -> {
            TableRow<Categoria> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() && SessionManager.getInstance().isAdmin()) {
                    editarCategoria(row.getItem());
                }
            });
            return row;
        });
    }

    private void cargarCategorias() {
        try {
            List<Categoria> categorias;
            String textoBusqueda = txtBuscar.getText().trim();

            if (textoBusqueda.isEmpty()) {
                categorias = categoriaDao.findAll();
            } else {
                categorias = categoriaDao.findByNombre(textoBusqueda);
            }

            listaCategorias.setAll(categorias);
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    private void setupEventHandlers() {
        txtBuscar.textProperty().addListener((obs, oldText, newText) -> cargarCategorias());

        tableCategorias.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEditar.setDisable(newSelection == null || !SessionManager.getInstance().isAdmin());
            btnEliminar.setDisable(newSelection == null || !SessionManager.getInstance().isAdmin());
        });
    }

    @FXML
    private void handleNuevo() {
        modoEdicion = false;
        categoriaEnEdicion = null;
        limpiarFormulario();
        lblTituloFormulario.setText("Nueva Categoría");
        panelFormulario.setVisible(true);
    }

    @FXML
    private void handleEditar() {
        Categoria selected = tableCategorias.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editarCategoria(selected);
        } else {
            showAlert("Selección requerida", "Por favor seleccione una categoría para editar.");
        }
    }

    private void editarCategoria(Categoria categoria) {
        modoEdicion = true;
        categoriaEnEdicion = categoria;
        cargarDatosEnFormulario(categoria);
        lblTituloFormulario.setText("Editar Categoría");
        panelFormulario.setVisible(true);
    }

    @FXML
    private void handleEliminar() {
        Categoria selected = tableCategorias.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selección requerida", "Por favor seleccione una categoría para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta categoría?");
        alert.setContentText("Categoría: " + selected.getNombre() + "\nEsta acción no se puede deshacer.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (categoriaDao.delete(selected.getIdCategoria())) {
                    showAlert("Éxito", "Categoría eliminada correctamente.");
                    cargarCategorias();
                } else {
                    showAlert("Error", "No se pudo eliminar la categoría.");
                }
            } catch (Exception e) {
                showAlert("Error", "Error al eliminar categoría: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefresh() {
        cargarCategorias();
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            Categoria categoria = obtenerCategoriaDeFormulario();

            if (modoEdicion) {
                categoria.setIdCategoria(categoriaEnEdicion.getIdCategoria());
                if (categoriaDao.update(categoria)) {
                    showAlert("Éxito", "Categoría actualizada correctamente.");
                } else {
                    showAlert("Error", "No se pudo actualizar la categoría.");
                    return;
                }
            } else {
                Integer id = categoriaDao.insert(categoria);
                if (id != null) {
                    showAlert("Éxito", "Categoría creada correctamente.");
                } else {
                    showAlert("Error", "No se pudo crear la categoría.");
                    return;
                }
            }

            handleCancelar();
            cargarCategorias();
        } catch (Exception e) {
            showAlert("Error", "Error al guardar categoría: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        panelFormulario.setVisible(false);
        limpiarFormulario();
        modoEdicion = false;
        categoriaEnEdicion = null;
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty()) {
            showAlert("Validación", "El nombre de la categoría es requerido.");
            txtNombre.requestFocus();
            return false;
        }

        return true;
    }

    private Categoria obtenerCategoriaDeFormulario() {
        Categoria categoria = new Categoria();
        categoria.setNombre(txtNombre.getText().trim());
        categoria.setDescripcion(txtDescripcion.getText().trim());
        return categoria;
    }

    private void cargarDatosEnFormulario(Categoria categoria) {
        txtNombre.setText(categoria.getNombre());
        txtDescripcion.setText(categoria.getDescripcion());
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
