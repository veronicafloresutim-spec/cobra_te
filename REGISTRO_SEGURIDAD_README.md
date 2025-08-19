# Sistema de Registro y Autenticación Mejorado

## 🔐 Características de Seguridad Implementadas

### 1. **Hashing SHA-256**
- Todas las contraseñas se almacenan hasheadas con SHA-256
- Verificación segura durante la autenticación
- No se almacenan contraseñas en texto plano

### 2. **Sistema de Registro**
- Interfaz de registro para nuevos cajeros
- Validación de contraseñas en tiempo real
- Verificación de correos únicos
- Validaciones de formulario completas

### 3. **Control de Acceso Refinado**

#### Administradores - Acceso Completo:
- ✅ Gestión de usuarios (CRUD)
- ✅ Gestión de categorías (CRUD)  
- ✅ Gestión de productos (CRUD)
- ✅ Punto de venta
- ✅ Reportes y estadísticas
- ✅ Configuración del sistema

#### Cajeros - Acceso Limitado:
- ✅ Punto de venta
- ✅ Consulta de productos
- ✅ Procesamiento de ventas
- ✅ Consulta de ventas realizadas
- ❌ Gestión de usuarios
- ❌ Gestión de categorías
- ❌ Gestión de productos
- ❌ Reportes administrativos

## 🚀 Flujo de Registro

### 1. Pantalla de Login
- Botón "Registrar Nuevo Cajero" añadido
- Mantiene usuarios de demostración
- Navegación fluida entre login/registro

### 2. Pantalla de Registro
- **Información Personal**: Nombres, apellidos
- **Contacto**: Correo, teléfono, sexo
- **Seguridad**: Contraseña con validaciones
- **Validaciones en Tiempo Real**:
  - Fortaleza de contraseña
  - Confirmación de contraseña
  - Disponibilidad de correo
  - Formato de correo

### 3. Validaciones de Contraseña
- **Mínimo 6 caracteres**
- **Al menos una letra**
- **Al menos un número**
- **Confirmación obligatoria**

## 🛡️ Utilidades de Seguridad

### PasswordUtils.java
```java
// Hashear contraseña
String hash = PasswordUtils.hashPassword("miContraseña");

// Verificar contraseña
boolean esCorrecta = PasswordUtils.verifyPassword("miContraseña", hash);

// Validar fortaleza
boolean esValida = PasswordUtils.isValidPassword("miContraseña");
```

### SessionManager Mejorado
```java
// Verificar permisos específicos
SessionManager.getInstance().canManageUsers()      // Solo admins
SessionManager.getInstance().canAccessPOS()        // Admins y cajeros
SessionManager.getInstance().canViewProducts()     // Admins y cajeros
SessionManager.getInstance().canManageProducts()   // Solo admins
```

## 📊 Base de Datos Actualizada

### Script de Migración
```sql
-- Archivo: db/update_passwords_sha256.sql
-- Convierte contraseñas existentes a SHA-256

UPDATE usuario 
SET contrasena = 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3' 
WHERE correo = 'admin@cobra_te.com';
```

### Credenciales Post-Migración
```
Administrador:
- Correo: admin@cobra_te.com
- Contraseña: admin123

Cajeros:
- Correo: maria@cobra_te.com / Contraseña: cajero123
- Correo: carlos@cobra_te.com / Contraseña: cajero123
```

## 🎯 Funcionalidades por Rol

### 🔥 Administrador
1. **Dashboard Completo**
   - Gestión de usuarios
   - Gestión de categorías
   - Gestión de productos
   - Reportes y estadísticas

2. **Configuración del Sistema**
   - Creación de usuarios administradores
   - Configuración de productos
   - Gestión de inventario

3. **Punto de Venta**
   - Acceso completo al POS
   - Procesamiento de ventas
   - Consulta de historial

### 💼 Cajero
1. **Punto de Venta**
   - Interfaz de venta simplificada
   - Catálogo de productos
   - Procesamiento de transacciones

2. **Consultas Permitidas**
   - Ver productos disponibles
   - Consultar historial de ventas propias
   - Ver detalles de productos

3. **Restricciones**
   - No puede modificar usuarios
   - No puede gestionar productos
   - No puede acceder a reportes administrativos

## 🔧 Archivos Nuevos/Modificados

### Nuevos Archivos
- `PasswordUtils.java` - Utilidades de hashing
- `RegistroController.java` - Controlador de registro
- `registro-view.fxml` - Vista de registro
- `update_passwords_sha256.sql` - Script de migración
- `PasswordTestExample.java` - Pruebas de hashing

### Archivos Modificados
- `UsuarioDao.java` - Hashing automático y métodos de registro
- `LoginController.java` - Botón de registro
- `login-view.fxml` - UI del botón de registro
- `SessionManager.java` - Control de acceso refinado

## 🚦 Estados de Validación

### Registro de Usuario
- ✅ **Verde**: Campo válido
- ⚠️ **Amarillo**: Advertencia (campo opcional)
- ❌ **Rojo**: Error de validación
- 🔄 **Azul**: Verificando disponibilidad

### Contraseña
- **Muy Débil**: Solo números o solo letras
- **Débil**: Menos de 6 caracteres
- **Aceptable**: 6+ caracteres con letras y números
- **Fuerte**: Cumple todos los requisitos

## 📱 Flujo de Navegación

```
Login Screen
├── [Iniciar Sesión] → Main Window
└── [Registrar Cajero] → Registration Screen
                        ├── [Registrar] → Success → Login Screen
                        └── [Cancelar] → Login Screen

Main Window (Admin)
├── Gestión de Usuarios
├── Gestión de Categorías  
├── Gestión de Productos
├── Punto de Venta
└── Reportes

Main Window (Cajero)
├── Punto de Venta (únicamente)
└── Consulta de Productos
```

## 🔒 Consideraciones de Seguridad

### Implementadas
- ✅ Hashing SHA-256 para contraseñas
- ✅ Validación de contraseñas fuertes
- ✅ Control de acceso basado en roles
- ✅ Verificación de correos únicos
- ✅ Sesiones seguras

### Por Implementar (Futuras Versiones)
- [ ] Salt en el hashing
- [ ] Límites de intentos de login
- [ ] Expiración de sesiones
- [ ] Logs de auditoría
- [ ] Encriptación de datos sensibles

## 🧪 Pruebas

### Ejecutar Pruebas de Hashing
```java
// Ejecutar PasswordTestExample.java
java org.example.cobra_te.examples.PasswordTestExample
```

### Probar Registro
1. Ejecutar aplicación
2. Click en "Registrar Nuevo Cajero"
3. Llenar formulario con datos válidos
4. Verificar registro exitoso
5. Login con nuevas credenciales

### Verificar Control de Acceso
1. Login como cajero
2. Verificar acceso solo a POS
3. Login como admin
4. Verificar acceso completo

## 📝 Notas de Migración

1. **Ejecutar script de migración** antes de usar el sistema actualizado
2. **Backup de base de datos** recomendado antes de migración
3. **Verificar credenciales** después de la migración
4. **Probar flujo completo** de registro y login

El sistema ahora ofrece seguridad mejorada con autenticación hasheada y control de acceso granular por roles.
