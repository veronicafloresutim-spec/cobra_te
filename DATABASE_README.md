# Módulo de Base de Datos - Cobra Te

Este módulo proporciona una capa de acceso a datos completa para la aplicación Cobra Te, usando JDBC con MariaDB.

## Estructura del Módulo

### Paquetes Principales

```
org.example.cobra_te.database/
├── DatabaseConnection.java    # Gestión de conexiones (Singleton)
└── DatabaseUtils.java        # Utilidades para BD y transacciones

org.example.cobra_te.models/
├── Usuario.java              # Modelo de Usuario
├── Categoria.java            # Modelo de Categoría
├── Producto.java             # Modelo de Producto
├── Venta.java               # Modelo de Venta
└── VentaProducto.java       # Modelo de relación Venta-Producto

org.example.cobra_te.dao/
├── CrudDao.java             # Interfaz genérica CRUD
├── UsuarioDao.java          # DAO para Usuario
├── CategoriaDao.java        # DAO para Categoría
├── ProductoDao.java         # DAO para Producto
├── VentaDao.java           # DAO para Venta
└── VentaProductoDao.java   # DAO para VentaProducto
```

## Configuración

### 1. Configuración de Base de Datos

Edita el archivo `src/main/resources/database.properties`:

```properties
database.url=jdbc:mariadb://localhost:3306/cobra_te
database.username=root
database.password=tu_password
database.driver=org.mariadb.jdbc.Driver
```

### 2. Crear Base de Datos

Ejecuta el script SQL ubicado en `db/scriptMariaDB.sql` para crear las tablas necesarias, o usa:

```java
DatabaseUtils dbUtils = new DatabaseUtils();
dbUtils.initializeTables();
dbUtils.insertSampleData(); // Datos de ejemplo opcionales
```

## Uso Básico

### Conexión a la Base de Datos

```java
// Obtener instancia singleton
DatabaseConnection dbConn = DatabaseConnection.getInstance();

// Verificar conexión
if (dbConn.isConnected()) {
    System.out.println("Conectado a la base de datos");
}

// Configurar parámetros manualmente (opcional)
dbConn.setConnectionParameters("jdbc:mariadb://servidor:3306/bd", "usuario", "password");
```

### Operaciones CRUD - Usuario

```java
UsuarioDao usuarioDao = new UsuarioDao();

// Crear usuario
Usuario usuario = new Usuario("cajero", "password", "Juan", "Pérez", "García", 
                             "juan@email.com", "5551234567", "H");
Integer id = usuarioDao.insert(usuario);

// Buscar por ID
Usuario encontrado = usuarioDao.findById(id);

// Buscar por email
Usuario porEmail = usuarioDao.findByEmail("juan@email.com");

// Autenticar usuario
Usuario autenticado = usuarioDao.authenticate("juan@email.com", "password");

// Obtener todos los usuarios
List<Usuario> usuarios = usuarioDao.findAll();

// Actualizar
usuario.setTelefono("5559876543");
usuarioDao.update(usuario);

// Eliminar
usuarioDao.delete(id);
```

### Operaciones CRUD - Producto

```java
ProductoDao productoDao = new ProductoDao();
CategoriaDao categoriaDao = new CategoriaDao();

// Crear producto
Producto producto = new Producto("Café Americano", "Café negro", "Grande", 
                                new BigDecimal("25.00"));
Integer idProducto = productoDao.insert(producto);

// Asignar categoría
Integer idCategoria = 1; // ID de categoría existente
productoDao.asignarCategoria(idProducto, idCategoria);

// Buscar productos por precio
List<Producto> productosEconomicos = productoDao.findByPrecioRange(
    new BigDecimal("20.00"), new BigDecimal("40.00"));

// Buscar por categoría
List<Producto> productosCategoria = productoDao.findByCategoria(idCategoria);
```

### Operaciones de Venta

```java
VentaDao ventaDao = new VentaDao();
VentaProductoDao ventaProductoDao = new VentaProductoDao();

// Crear venta
Venta venta = new Venta(LocalDateTime.now(), idUsuario, new BigDecimal("50.00"));
Integer idVenta = ventaDao.insert(venta);

// Agregar productos a la venta
VentaProducto ventaProducto = new VentaProducto(idVenta, idProducto, 2); // 2 unidades
ventaProductoDao.insert(ventaProducto);

// Obtener ventas de hoy
List<Venta> ventasHoy = ventaDao.getVentasHoy();

// Total de ventas del día
BigDecimal totalDia = ventaDao.getTotalVentasPorDia(LocalDateTime.now());

// Productos más vendidos
List<VentaProducto> topProductos = ventaProductoDao.getProductosMasVendidos(10);
```

## Funcionalidades Avanzadas

### Transacciones

```java
DatabaseUtils dbUtils = new DatabaseUtils();

// Ejecutar operaciones en transacción
boolean exito = dbUtils.executeTransaction(
    () -> {
        // Operación 1
        usuarioDao.insert(usuario1);
        // Operación 2
        usuarioDao.insert(usuario2);
        // Si alguna falla, se hace rollback automático
    }
);
```

### Utilidades de Base de Datos

```java
DatabaseUtils dbUtils = new DatabaseUtils();

// Verificar si existe una tabla
boolean exists = dbUtils.tableExists("usuario");

// Inicializar todas las tablas
dbUtils.initializeTables();

// Insertar datos de ejemplo
dbUtils.insertSampleData();

// Limpiar todas las tablas (¡CUIDADO!)
dbUtils.clearAllTables();

// Mostrar estadísticas
dbUtils.printDatabaseStats();
```

## Métodos Específicos por DAO

### UsuarioDao
- `findByEmail(String correo)` - Buscar por correo electrónico
- `authenticate(String correo, String password)` - Autenticar usuario
- `findByRol(String rol)` - Buscar usuarios por rol

### CategoriaDao
- `findByNombre(String nombre)` - Búsqueda parcial por nombre
- `findByProductoId(Integer idProducto)` - Categorías de un producto

### ProductoDao
- `findByNombre(String nombre)` - Búsqueda parcial por nombre
- `findByCategoria(Integer idCategoria)` - Productos por categoría
- `findByPrecioRange(BigDecimal min, BigDecimal max)` - Por rango de precio
- `asignarCategoria(Integer idProducto, Integer idCategoria)` - Asignar categoría
- `desasignarCategoria(Integer idProducto, Integer idCategoria)` - Quitar categoría

### VentaDao
- `findByUsuario(Integer idUsuario)` - Ventas por usuario
- `findByFechaRange(LocalDateTime inicio, LocalDateTime fin)` - Por rango de fechas
- `getTotalVentasPorDia(LocalDateTime fecha)` - Total del día
- `getVentasHoy()` - Ventas del día actual

### VentaProductoDao
- `findByVentaId(Integer idVenta)` - Productos de una venta
- `findByProductoId(Integer idProducto)` - Ventas que incluyen un producto
- `updateCantidad(Integer idVenta, Integer idProducto, Integer cantidad)` - Actualizar cantidad
- `getProductosMasVendidos(int limite)` - Top de productos más vendidos
- `getCantidadTotalVendida(Integer idProducto)` - Total vendido de un producto

## Ejemplo Completo

Para ver un ejemplo completo de uso, ejecuta:

```java
public static void main(String[] args) {
    DatabaseExample ejemplo = new DatabaseExample();
    ejemplo.runAllExamples();
}
```

## Manejo de Errores

- Todos los métodos DAO manejan excepciones SQL internamente
- Los errores se imprimen en consola con `System.err.println()`
- Los métodos retornan `null`, `false` o listas vacías en caso de error
- Las transacciones incluyen rollback automático en caso de fallo

## Dependencias

El módulo requiere:
- MariaDB JDBC Driver (ya incluido en `pom.xml`)
- Java 8 o superior
- MariaDB Server 10.x o superior

## Configuración de MariaDB

1. Instalar MariaDB Server
2. Crear base de datos: `CREATE DATABASE cobra_te;`
3. Crear usuario (opcional): `CREATE USER 'cobra_user'@'localhost' IDENTIFIED BY 'password';`
4. Otorgar permisos: `GRANT ALL PRIVILEGES ON cobra_te.* TO 'cobra_user'@'localhost';`
