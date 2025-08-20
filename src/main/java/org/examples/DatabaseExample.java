package org.examples;

import org.dao.*;
import org.database.DatabaseConnection;
import org.database.DatabaseUtils;
import org.models.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase de ejemplo que demuestra el uso de los módulos CRUD
 */
public class DatabaseExample {

    private final UsuarioDao usuarioDao;
    private final CategoriaDao categoriaDao;
    private final ProductoDao productoDao;
    private final VentaDao ventaDao;
    private final VentaProductoDao ventaProductoDao;
    private final DatabaseUtils dbUtils;

    public DatabaseExample() {
        this.usuarioDao = new UsuarioDao();
        this.categoriaDao = new CategoriaDao();
        this.productoDao = new ProductoDao();
        this.ventaDao = new VentaDao();
        this.ventaProductoDao = new VentaProductoDao();
        this.dbUtils = new DatabaseUtils();
    }

    /**
     * Ejecuta ejemplos de todas las operaciones CRUD
     */
    public void runAllExamples() {
        System.out.println("=== Iniciando ejemplos de uso de la base de datos ===");

        // Verificar conexión
        if (!testDatabaseConnection()) {
            System.err.println("No se pudo conectar a la base de datos");
            return;
        }

        // Inicializar tablas
        initializeDatabase();

        // Ejemplos de CRUD
        ejemploUsuarios();
        ejemploCategorias();
        ejemploProductos();
        ejemploVentas();

        // Estadísticas
        dbUtils.printDatabaseStats();
    }

    /**
     * Prueba la conexión a la base de datos
     */
    public boolean testDatabaseConnection() {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        try {
            if (dbConn.isConnected()) {
                System.out.println("✓ Conexión a la base de datos exitosa");
                return true;
            } else {
                // Intentar conectar
                dbConn.getConnection();
                System.out.println("✓ Conexión a la base de datos establecida");
                return true;
            }
        } catch (Exception e) {
            System.err.println("✗ Error de conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicializa la base de datos con tablas y datos de ejemplo
     */
    public void initializeDatabase() {
        System.out.println("\n--- Inicializando Base de Datos ---");

        if (dbUtils.initializeTables()) {
            System.out.println("✓ Tablas creadas/verificadas correctamente");
        } else {
            System.err.println("✗ Error al crear las tablas");
        }

        if (dbUtils.insertSampleData()) {
            System.out.println("✓ Datos de ejemplo insertados");
        } else {
            System.out.println("ℹ Los datos de ejemplo ya existen o hubo un error");
        }
    }

    /**
     * Ejemplos de operaciones CRUD con Usuario
     */
    public void ejemploUsuarios() {
        System.out.println("\n--- Ejemplos de Usuarios ---");

        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario("cajero", "password123", "Ana", "Martínez",
                "Fernández", "ana@ejemplo.com", "5551111111", "M");
        Integer idUsuario = usuarioDao.insert(nuevoUsuario);

        if (idUsuario != null) {
            System.out.println("✓ Usuario creado con ID: " + idUsuario);

            // Buscar usuario por ID
            Usuario usuarioEncontrado = usuarioDao.findById(idUsuario);
            if (usuarioEncontrado != null) {
                System.out.println("✓ Usuario encontrado: " + usuarioEncontrado.getNombreCompleto());
            }

            // Actualizar usuario
            usuarioEncontrado.setTelefono("5552222222");
            if (usuarioDao.update(usuarioEncontrado)) {
                System.out.println("✓ Usuario actualizado");
            }

            // Buscar por correo
            Usuario usuarioPorCorreo = usuarioDao.findByEmail("ana@ejemplo.com");
            if (usuarioPorCorreo != null) {
                System.out.println("✓ Usuario encontrado por correo: " + usuarioPorCorreo.getCorreo());
            }
        }

        // Listar todos los usuarios
        List<Usuario> usuarios = usuarioDao.findAll();
        System.out.println("Total de usuarios: " + usuarios.size());

        // Autenticación
        Usuario usuarioAutenticado = usuarioDao.authenticate("admin@cobra_te.com", "admin123");
        if (usuarioAutenticado != null) {
            System.out.println("✓ Autenticación exitosa para: " + usuarioAutenticado.getNombreCompleto());
        }
    }

    /**
     * Ejemplos de operaciones CRUD con Categoria
     */
    public void ejemploCategorias() {
        System.out.println("\n--- Ejemplos de Categorías ---");

        // Crear nueva categoría
        Categoria nuevaCategoria = new Categoria("Pastelería", "Pasteles y productos de repostería");
        Integer idCategoria = categoriaDao.insert(nuevaCategoria);

        if (idCategoria != null) {
            System.out.println("✓ Categoría creada con ID: " + idCategoria);
        }

        // Listar todas las categorías
        List<Categoria> categorias = categoriaDao.findAll();
        System.out.println("Total de categorías: " + categorias.size());

        // Buscar por nombre
        List<Categoria> categoriasBebidas = categoriaDao.findByNombre("Bebidas");
        System.out.println("Categorías que contienen 'Bebidas': " + categoriasBebidas.size());
    }

    /**
     * Ejemplos de operaciones CRUD con Producto
     */
    public void ejemploProductos() {
        System.out.println("\n--- Ejemplos de Productos ---");

        // Crear nuevo producto
        Producto nuevoProducto = new Producto("Té Verde", "Té verde natural", "Grande", new BigDecimal("30.00"));
        Integer idProducto = productoDao.insert(nuevoProducto);

        if (idProducto != null) {
            System.out.println("✓ Producto creado con ID: " + idProducto);

            // Asignar categoría al producto
            List<Categoria> categorias = categoriaDao.findByNombre("Bebidas Calientes");
            if (!categorias.isEmpty()) {
                if (productoDao.asignarCategoria(idProducto, categorias.get(0).getIdCategoria())) {
                    System.out.println("✓ Categoría asignada al producto");
                }
            }
        }

        // Listar todos los productos
        List<Producto> productos = productoDao.findAll();
        System.out.println("Total de productos: " + productos.size());

        // Buscar productos por rango de precio
        List<Producto> productosEconomicos = productoDao.findByPrecioRange(
                new BigDecimal("20.00"), new BigDecimal("40.00"));
        System.out.println("Productos entre $20 y $40: " + productosEconomicos.size());
    }

    /**
     * Ejemplos de operaciones CRUD con Venta
     */
    public void ejemploVentas() {
        System.out.println("\n--- Ejemplos de Ventas ---");

        // Buscar un usuario para la venta
        List<Usuario> usuarios = usuarioDao.findByRol("cajero");
        if (usuarios.isEmpty()) {
            System.out.println("No hay cajeros disponibles para crear venta");
            return;
        }

        Usuario cajero = usuarios.get(0);

        // Crear nueva venta
        Venta nuevaVenta = new Venta(LocalDateTime.now(), cajero.getIdUsuario(), new BigDecimal("80.00"));
        Integer idVenta = ventaDao.insert(nuevaVenta);

        if (idVenta != null) {
            System.out.println("✓ Venta creada con ID: " + idVenta);

            // Agregar productos a la venta
            List<Producto> productos = productoDao.findAll();
            if (productos.size() >= 2) {
                // Agregar primer producto
                VentaProducto vp1 = new VentaProducto(idVenta, productos.get(0).getIdProducto(), 2);
                if (ventaProductoDao.insert(vp1)) {
                    System.out.println("✓ Producto 1 agregado a la venta");
                }

                // Agregar segundo producto
                VentaProducto vp2 = new VentaProducto(idVenta, productos.get(1).getIdProducto(), 1);
                if (ventaProductoDao.insert(vp2)) {
                    System.out.println("✓ Producto 2 agregado a la venta");
                }
            }
        }

        // Listar ventas de hoy
        List<Venta> ventasHoy = ventaDao.getVentasHoy();
        System.out.println("Ventas de hoy: " + ventasHoy.size());

        // Total de ventas del día
        BigDecimal totalHoy = ventaDao.getTotalVentasPorDia(LocalDateTime.now());
        System.out.println("Total vendido hoy: $" + totalHoy);

        // Productos más vendidos
        List<VentaProducto> productosMasVendidos = ventaProductoDao.getProductosMasVendidos(5);
        System.out.println("Top 5 productos más vendidos: " + productosMasVendidos.size());
        for (VentaProducto vp : productosMasVendidos) {
            if (vp.getProducto() != null) {
                System.out.println("- " + vp.getProducto().getNombre() +
                        " (vendido: " + vp.getCantidad() + " veces)");
            }
        }
    }

    /**
     * Método main para ejecutar el ejemplo
     */
    public static void main(String[] args) {
        DatabaseExample ejemplo = new DatabaseExample();
        ejemplo.runAllExamples();

        // Cerrar conexión al finalizar
        DatabaseConnection.getInstance().closeConnection();
        System.out.println("\n=== Ejemplo finalizado ===");
    }
}
