# Interfaces Gráficas - Sistema POS Cobra Te

Este documento describe las interfaces gráficas del sistema POS desarrollado con JavaFX.

## 📋 Estructura de las Vistas

### Paquetes Principales

```
org.example.cobra_te.controllers/
├── LoginController.java         # Controlador de inicio de sesión
├── MainController.java          # Controlador principal de la aplicación
├── POSController.java           # Controlador del punto de venta
├── UsuariosController.java      # Controlador de gestión de usuarios
└── CategoriasController.java    # Controlador de gestión de categorías

org.example.cobra_te.views/      # Archivos FXML
├── login-view.fxml              # Pantalla de inicio de sesión
├── main-view.fxml               # Ventana principal
├── pos-view.fxml                # Punto de venta
├── usuarios-view.fxml           # Gestión de usuarios
└── categorias-view.fxml         # Gestión de categorías

org.example.cobra_te.utils/
└── SessionManager.java          # Gestión de sesión de usuario
```

## 🔐 Sistema de Autenticación

### Pantalla de Login
- **Archivo**: `login-view.fxml` + `LoginController.java`
- **Funcionalidades**:
  - Autenticación por correo y contraseña
  - Validación de credenciales contra la base de datos
  - Indicador de carga durante autenticación
  - Usuarios de demostración mostrados en pantalla
  - Manejo de errores de conexión

### Usuarios de Demostración
```
Administrador:
- Correo: admin@cobra_te.com
- Contraseña: admin123

Cajero:
- Correo: maria@cobra_te.com  
- Contraseña: cajero123
```

## 🏠 Ventana Principal

### Menú Principal
- **Archivo**: `main-view.fxml` + `MainController.java`
- **Estructura**:
  - Barra de menú con navegación
  - Información del usuario actual
  - Fecha y hora en tiempo real
  - Botón de cerrar sesión
  - Área de contenido dinámico

### Control de Acceso por Rol
- **Administrador**: Acceso completo a todas las funciones
- **Cajero**: Solo acceso al punto de venta

## 💰 Punto de Venta (POS)

### Funcionalidades Principales
- **Archivo**: `pos-view.fxml` + `POSController.java`
- **Características**:
  - Catálogo de productos con filtros por categoría
  - Búsqueda de productos en tiempo real
  - Carrito de compras interactivo
  - Cálculo automático de totales
  - Procesamiento de ventas
  - Generación de tickets de venta

### Flujo de Trabajo
1. **Selección de Productos**: Click en botones de productos
2. **Gestión del Carrito**: 
   - Modificar cantidades con spinner
   - Eliminar items con doble click
   - Ver total en tiempo real
3. **Procesamiento**: 
   - Validar carrito no vacío
   - Crear venta en base de datos
   - Mostrar ticket de compra
   - Limpiar carrito automáticamente

## 👥 Gestión de Usuarios (Solo Administradores)

### Funcionalidades
- **Archivo**: `usuarios-view.fxml` + `UsuariosController.java`
- **Operaciones CRUD**:
  - ✅ **Crear**: Formulario completo con validaciones
  - 📖 **Leer**: Tabla con todos los usuarios
  - ✏️ **Actualizar**: Edición en formulario lateral
  - 🗑️ **Eliminar**: Con confirmación (excepto usuario actual)

### Características Especiales
- Búsqueda en tiempo real por nombre, apellido o correo
- Filtrado por rol (cajero/administrador)
- Validaciones de formulario completas
- Prevención de auto-eliminación
- Doble click para edición rápida

## 📂 Gestión de Categorías (Solo Administradores)

### Funcionalidades
- **Archivo**: `categorias-view.fxml` + `CategoriasController.java`
- **Operaciones CRUD**:
  - ✅ **Crear**: Formulario simple con nombre y descripción
  - 📖 **Leer**: Tabla con todas las categorías
  - ✏️ **Actualizar**: Edición en formulario lateral
  - 🗑️ **Eliminar**: Con confirmación

### Características
- Búsqueda por nombre de categoría
- Interfaz simple y funcional
- Validación de campos requeridos

## 🎨 Diseño e Interfaz

### Esquema de Colores
- **Color Principal**: #8B4513 (Marrón café)
- **Fondo**: #f5f5f5 (Gris claro)
- **Acentos**: 
  - Verde: #28a745 (Botones de confirmación)
  - Rojo: #dc3545 (Botones de eliminación)
  - Azul: #007bff (Botones de edición)
  - Gris: #6c757d (Botones neutros)

### Iconografía
- ☕ Café (Logo/tema principal)
- 👥 Usuarios
- 📂 Categorías  
- 💰 Ventas
- 🛒 Carrito
- ✏️ Editar
- 🗑️ Eliminar
- ➕ Agregar
- 💾 Guardar

### Tipografía
- **Títulos**: System Bold, 18-24px
- **Contenido**: System Regular, 12-14px
- **Etiquetas**: System Regular, 11-12px

## 🔧 Funcionalidades Técnicas

### SessionManager
```java
// Verificar si el usuario está logueado
SessionManager.getInstance().isLoggedIn()

// Verificar rol de administrador
SessionManager.getInstance().isAdmin()

// Obtener usuario actual
SessionManager.getInstance().getCurrentUser()

// Cerrar sesión
SessionManager.getInstance().logout()
```

### Carga Dinámica de Vistas
```java
// En MainController
private void loadView(String fxmlPath, String title) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
    Parent view = loader.load();
    centerContent.getChildren().clear();
    centerContent.getChildren().add(view);
}
```

### Validaciones de Formulario
- Campos requeridos marcados con asterisco (*)
- Validación en tiempo real
- Mensajes de error específicos
- Focus automático en campo con error

## 🚀 Flujo de la Aplicación

### 1. Inicio
```
HelloApplication → LoginController → Autenticación → MainController
```

### 2. Navegación
```
MainController → [POSController | UsuariosController | CategoriasController]
```

### 3. Operaciones CRUD
```
Controlador → DAO → Base de Datos → Actualización de Vista
```

## 📱 Responsividad

### Ventanas Principales
- **Login**: 600x500px (fijo)
- **Principal**: 1200x800px (maximizable)
- **Contenido**: Adaptativo según la vista cargada

### Componentes Adaptativos
- Tablas con scrolling automático
- Formularios con tamaños fijos
- Botones con anchos consistentes
- Espaciado proporcional

## 🔒 Seguridad

### Control de Acceso
- Verificación de rol en cada acción sensible
- Deshabilitación de botones según permisos
- Validación tanto en cliente como en servidor
- Prevención de acciones no autorizadas

### Validaciones
- Autenticación obligatoria
- Verificación de sesión activa
- Validación de datos de entrada
- Confirmación para acciones destructivas

## 🐛 Manejo de Errores

### Estrategias Implementadas
- Try-catch en todas las operaciones de BD
- Mensajes de error amigables al usuario
- Logging de errores en consola para depuración
- Graceful degradation en caso de fallos

### Tipos de Alertas
- **Información**: Confirmaciones de éxito
- **Advertencia**: Validaciones fallidas
- **Confirmación**: Acciones destructivas
- **Error**: Fallos de sistema/conexión

## 📋 Lista de Características Implementadas

### ✅ Completadas
- [x] Sistema de login funcional
- [x] Ventana principal con navegación
- [x] Punto de venta completo
- [x] Gestión de usuarios (CRUD)
- [x] Gestión de categorías (CRUD)
- [x] Control de acceso por roles
- [x] Manejo de sesiones
- [x] Diseño responsivo
- [x] Validaciones de formulario

### 🔄 Por Implementar (Futuras Versiones)
- [ ] Gestión de productos (CRUD)
- [ ] Reportes y estadísticas
- [ ] Gestión de inventario
- [ ] Configuración de sistema
- [ ] Backup y restauración
- [ ] Impresión de tickets
- [ ] Gestión de descuentos
- [ ] Múltiples métodos de pago

## 🔧 Instalación y Configuración

### Requisitos
1. Java 17 o superior
2. JavaFX SDK
3. MariaDB Server
4. IDE compatible (IntelliJ IDEA, Eclipse, etc.)

### Configuración de Base de Datos
1. Ejecutar `db/setup_completo.sql`
2. Configurar `database.properties` con credenciales correctas
3. Verificar conectividad

### Ejecución
```bash
# Compilar proyecto
mvn clean compile

# Ejecutar aplicación
mvn javafx:run
```

O ejecutar directamente la clase `HelloApplication` desde el IDE.

La aplicación iniciará automáticamente en la pantalla de login, y podrás usar las credenciales de demostración para acceder al sistema.
