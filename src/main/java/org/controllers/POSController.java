package org.controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.dao.CategoriaDao;
import org.dao.ProductoDao;
import org.dao.VentaDao;
import org.dao.VentaProductoDao;
import org.models.Categoria;
import org.models.Producto;
import org.models.Venta;
import org.models.VentaProducto;
import org.utils.SessionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para el punto de venta (POS)
 */
public class POSController {

    @FXML
    private ComboBox<Categoria> cmbCategorias;
    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private GridPane gridProductos;
    @FXML
    private TableView<ItemVenta> tableCarrito;
    @FXML
    private TableColumn<ItemVenta, String> colProducto;
    @FXML
    private TableColumn<ItemVenta, Integer> colCantidad;
    @FXML
    private TableColumn<ItemVenta, Double> colPrecio;
    @FXML
    private TableColumn<ItemVenta, Double> colSubtotal;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblCantidadItems;
    @FXML
    private Button btnLimpiarCarrito;
    @FXML
    private Button btnProcesarVenta;
    @FXML
    private VBox panelProductos;

    private ProductoDao productoDao;
    private CategoriaDao categoriaDao;
    private VentaDao ventaDao;
    private VentaProductoDao ventaProductoDao;
    private ObservableList<ItemVenta> carrito;
    private BigDecimal totalVenta;

    /**
     * Clase interna para representar items en el carrito
     */
    public static class ItemVenta {
        private final SimpleStringProperty producto;
        private final SimpleIntegerProperty cantidad;
        private final SimpleDoubleProperty precio;
        private final SimpleDoubleProperty subtotal;
        private final Integer idProducto;

        public ItemVenta(Integer idProducto, String producto, Integer cantidad, Double precio) {
            this.idProducto = idProducto;
            this.producto = new SimpleStringProperty(producto);
            this.cantidad = new SimpleIntegerProperty(cantidad);
            this.precio = new SimpleDoubleProperty(precio);
            this.subtotal = new SimpleDoubleProperty(cantidad * precio);
        }

        // Getters para JavaFX
        public String getProducto() {
            return producto.get();
        }

        public Integer getCantidad() {
            return cantidad.get();
        }

        public Double getPrecio() {
            return precio.get();
        }

        public Double getSubtotal() {
            return subtotal.get();
        }

        public Integer getIdProducto() {
            return idProducto;
        }

        // Setters
        public void setCantidad(Integer cantidad) {
            this.cantidad.set(cantidad);
            this.subtotal.set(cantidad * this.precio.get());
        }

        // Properties para binding
        public SimpleStringProperty productoProperty() {
            return producto;
        }

        public SimpleIntegerProperty cantidadProperty() {
            return cantidad;
        }

        public SimpleDoubleProperty precioProperty() {
            return precio;
        }

        public SimpleDoubleProperty subtotalProperty() {
            return subtotal;
        }
    }

    public void initialize() {
        productoDao = new ProductoDao();
        categoriaDao = new CategoriaDao();
        ventaDao = new VentaDao();
        ventaProductoDao = new VentaProductoDao();
        carrito = FXCollections.observableArrayList();
        totalVenta = BigDecimal.ZERO;

        setupTableView();
        loadCategorias();
        loadProductos();
        setupEventHandlers();
    }

    private void setupTableView() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        // Formatear columnas de precio
        colPrecio.setCellFactory(col -> new TableCell<ItemVenta, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        colSubtotal.setCellFactory(col -> new TableCell<ItemVenta, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", subtotal));
                }
            }
        });

        // Permitir edición de cantidad
        colCantidad.setCellFactory(col -> new TableCell<ItemVenta, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>(1, 99, 1);

            @Override
            protected void updateItem(Integer cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    spinner.getValueFactory().setValue(cantidad);
                    spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                        ItemVenta item = getTableView().getItems().get(getIndex());
                        item.setCantidad(newVal);
                        updateTotal();
                    });
                    setGraphic(spinner);
                }
            }
        });

        tableCarrito.setItems(carrito);
    }

    private void loadCategorias() {
        try {
            List<Categoria> categorias = categoriaDao.findAll();
            cmbCategorias.getItems().clear();
            cmbCategorias.getItems().add(new Categoria(0, "Todas las categorías", ""));
            cmbCategorias.getItems().addAll(categorias);
            cmbCategorias.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    private void loadProductos() {
        try {
            List<Producto> productos;
            Categoria categoriaSeleccionada = cmbCategorias.getSelectionModel().getSelectedItem();

            if (categoriaSeleccionada != null && categoriaSeleccionada.getIdCategoria() != null
                    && categoriaSeleccionada.getIdCategoria() > 0) {
                productos = productoDao.findByCategoria(categoriaSeleccionada.getIdCategoria());
            } else {
                productos = productoDao.findAll();
            }

            String filtroTexto = txtBuscarProducto.getText().trim();
            if (!filtroTexto.isEmpty()) {
                productos = productos.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(filtroTexto.toLowerCase()))
                        .toList();
            }

            mostrarProductosEnGrid(productos);
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los productos: " + e.getMessage());
        }
    }

    private void mostrarProductosEnGrid(List<Producto> productos) {
        gridProductos.getChildren().clear();

        int columnas = 3;
        int fila = 0, columna = 0;

        for (Producto producto : productos) {
            Button btnProducto = createProductButton(producto);
            gridProductos.add(btnProducto, columna, fila);

            columna++;
            if (columna >= columnas) {
                columna = 0;
                fila++;
            }
        }
    }

    private Button createProductButton(Producto producto) {
        Button button = new Button();
        button.setText(producto.getNombre() + "\n" +
                (producto.getTamano() != null ? producto.getTamano() + "\n" : "") +
                String.format("$%.2f", producto.getPrecio()));
        button.setPrefSize(180, 100);
        button.setStyle("-fx-background-color: #ffffff; " +
                "-fx-border-color: #dddddd; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-font-size: 12px; " +
                "-fx-text-alignment: center;");

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-background-color: #f0f0f0;"));
        button.setOnMouseExited(e -> button.setStyle(
                button.getStyle().replace("-fx-background-color: #f0f0f0;", "-fx-background-color: #ffffff;")));

        button.setOnAction(e -> agregarAlCarrito(producto));

        return button;
    }

    private void agregarAlCarrito(Producto producto) {
        // Verificar si el producto ya está en el carrito
        Optional<ItemVenta> itemExistente = carrito.stream()
                .filter(item -> item.getIdProducto().equals(producto.getIdProducto()))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Incrementar cantidad
            ItemVenta item = itemExistente.get();
            item.setCantidad(item.getCantidad() + 1);
        } else {
            // Agregar nuevo item
            ItemVenta nuevoItem = new ItemVenta(
                    producto.getIdProducto(),
                    producto.getNombre() + (producto.getTamano() != null ? " (" + producto.getTamano() + ")" : ""),
                    1,
                    producto.getPrecio().doubleValue());
            carrito.add(nuevoItem);
        }

        updateTotal();
        tableCarrito.refresh();
    }

    private void updateTotal() {
        totalVenta = carrito.stream()
                .map(item -> BigDecimal.valueOf(item.getSubtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotal.setText(String.format("$%.2f", totalVenta));
        lblCantidadItems.setText(String.valueOf(carrito.size()));

        btnProcesarVenta.setDisable(carrito.isEmpty());
    }

    private void setupEventHandlers() {
        cmbCategorias.setOnAction(e -> loadProductos());
        txtBuscarProducto.textProperty().addListener((obs, oldText, newText) -> loadProductos());

        // Doble click para eliminar item del carrito
        tableCarrito.setRowFactory(tv -> {
            TableRow<ItemVenta> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    carrito.remove(row.getItem());
                    updateTotal();
                }
            });
            return row;
        });
    }

    @FXML
    private void handleLimpiarCarrito() {
        if (!carrito.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Limpiar Carrito");
            alert.setHeaderText("¿Está seguro de limpiar el carrito?");
            alert.setContentText("Se eliminarán todos los productos del carrito.");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                carrito.clear();
                updateTotal();
            }
        }
    }

    @FXML
    private void handleProcesarVenta() {
        if (carrito.isEmpty()) {
            showAlert("Carrito Vacío", "Agregue productos al carrito antes de procesar la venta.");
            return;
        }

        try {
            // Crear la venta
            Venta venta = new Venta(LocalDateTime.now(),
                    SessionManager.getInstance().getCurrentUser().getIdUsuario(),
                    totalVenta);
            Integer idVenta = ventaDao.insert(venta);

            if (idVenta != null) {
                // Agregar productos a la venta
                for (ItemVenta item : carrito) {
                    VentaProducto ventaProducto = new VentaProducto(idVenta, item.getIdProducto(), item.getCantidad());
                    ventaProductoDao.insert(ventaProducto);
                }

                // Mostrar ticket de venta
                mostrarTicketVenta(idVenta);

                // Limpiar carrito
                carrito.clear();
                updateTotal();

                showAlert("Venta Procesada", "La venta se procesó correctamente.\nTotal: $" +
                        String.format("%.2f", totalVenta));
            } else {
                showAlert("Error", "No se pudo procesar la venta.");
            }
        } catch (Exception e) {
            showAlert("Error", "Error al procesar la venta: " + e.getMessage());
        }
    }

    private void mostrarTicketVenta(Integer idVenta) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ticket de Venta");
        alert.setHeaderText("Venta #" + idVenta + " - "
                + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        StringBuilder ticket = new StringBuilder();
        ticket.append("COBRA TE - CAFETERÍA\n");
        ticket.append("========================\n\n");
        ticket.append("Cajero: ").append(SessionManager.getInstance().getCurrentUserName()).append("\n\n");

        for (ItemVenta item : carrito) {
            ticket.append(String.format("%-20s %2d x $%6.2f = $%8.2f\n",
                    item.getProducto(), item.getCantidad(), item.getPrecio(), item.getSubtotal()));
        }

        ticket.append("\n========================\n");
        ticket.append(String.format("TOTAL: $%.2f\n", totalVenta));
        ticket.append("========================\n");
        ticket.append("\n¡Gracias por su compra!");

        alert.setContentText(ticket.toString());
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
