-- Script completo para configurar la base de datos Cobra Te
-- Este script incluye la creación de la base de datos, tablas y datos de ejemplo

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS cobra_te CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cobra_te;

-- Crear tabla usuario
CREATE TABLE IF NOT EXISTS usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    rol ENUM('cajero', 'administrador') NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidoPaterno VARCHAR(100) NOT NULL,
    apellidoMaterno VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono CHAR(10) NOT NULL,
    sexo ENUM('H', 'M') NULL,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Crear tabla categoria
CREATE TABLE IF NOT EXISTS categoria (
    idCategoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Crear tabla producto
CREATE TABLE IF NOT EXISTS producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tamano VARCHAR(50) NULL,
    precio DECIMAL(10,2) NOT NULL CHECK (precio >= 0),
    fechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Crear tabla de relación producto-categoria
CREATE TABLE IF NOT EXISTS productoCategoria (
    idProducto INT NOT NULL,
    idCategoria INT NOT NULL,
    PRIMARY KEY (idProducto, idCategoria),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto) ON DELETE CASCADE,
    FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria) ON DELETE CASCADE
);

-- Crear tabla venta
CREATE TABLE IF NOT EXISTS venta (
    idVenta INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    idUsuario INT NOT NULL,
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    observaciones TEXT NULL,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
);

-- Crear tabla detalle de venta
CREATE TABLE IF NOT EXISTS ventaProducto (
    idVenta INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    precioUnitario DECIMAL(10,2) NOT NULL CHECK (precioUnitario >= 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    PRIMARY KEY (idVenta, idProducto),
    FOREIGN KEY (idVenta) REFERENCES venta(idVenta) ON DELETE CASCADE,
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
);

-- Insertar datos de ejemplo
-- Usuarios
INSERT IGNORE INTO usuario (rol, contrasena, nombres, apellidoPaterno, apellidoMaterno, correo, telefono, sexo) VALUES
('administrador', 'admin123', 'Carlos', 'García', 'López', 'admin@cobra_te.com', '5551234567', 'H'),
('cajero', 'cajero123', 'María', 'Rodríguez', 'Pérez', 'maria@cobra_te.com', '5552345678', 'M'),
('cajero', 'cajero456', 'José', 'Martínez', 'González', 'jose@cobra_te.com', '5553456789', 'H'),
('administrador', 'admin456', 'Ana', 'López', 'Sánchez', 'ana@cobra_te.com', '5554567890', 'M');

-- Categorías
INSERT IGNORE INTO categoria (nombre, descripcion) VALUES
('Bebidas Calientes', 'Café, té, chocolate caliente y otras bebidas calientes'),
('Bebidas Frías', 'Refrescos, jugos naturales, aguas frescas y bebidas frías'),
('Snacks Dulces', 'Galletas, pasteles, donas y otros productos dulces'),
('Snacks Salados', 'Papas, nachos, pretzels y botanas saladas'),
('Comida Rápida', 'Sándwiches, wraps, quesadillas y comida para llevar'),
('Postres', 'Helados, pasteles, flanes y postres especiales'),
('Panadería', 'Pan dulce, bolillos, croissants y productos de panadería'),
('Bebidas Especiales', 'Smoothies, frapés, bebidas de temporada');

-- Productos
INSERT IGNORE INTO producto (nombre, descripcion, tamano, precio) VALUES
-- Bebidas Calientes
('Café Americano', 'Café negro tradicional', 'Chico', 20.00),
('Café Americano', 'Café negro tradicional', 'Mediano', 25.00),
('Café Americano', 'Café negro tradicional', 'Grande', 30.00),
('Cappuccino', 'Café con espuma de leche', 'Mediano', 35.00),
('Cappuccino', 'Café con espuma de leche', 'Grande', 40.00),
('Latte', 'Café con leche vaporizada', 'Mediano', 38.00),
('Latte', 'Café con leche vaporizada', 'Grande', 43.00),
('Té Verde', 'Té verde natural', 'Taza', 25.00),
('Té Negro', 'Té negro tradicional', 'Taza', 25.00),
('Chocolate Caliente', 'Chocolate con leche caliente', 'Taza', 30.00),

-- Bebidas Frías
('Agua Natural', 'Agua purificada', '500ml', 15.00),
('Refresco Coca Cola', 'Refresco de cola', '355ml', 20.00),
('Refresco Pepsi', 'Refresco de cola', '355ml', 20.00),
('Jugo de Naranja', 'Jugo natural de naranja', '300ml', 25.00),
('Jugo de Manzana', 'Jugo natural de manzana', '300ml', 25.00),
('Agua de Jamaica', 'Agua fresca de jamaica', '400ml', 18.00),
('Agua de Horchata', 'Agua fresca de horchata', '400ml', 20.00),

-- Snacks y Comida
('Croissant Simple', 'Pan francés mantequilloso', 'Pieza', 22.00),
('Croissant de Jamón', 'Croissant relleno de jamón', 'Pieza', 28.00),
('Muffin de Chocolate', 'Panqué con chispas de chocolate', 'Pieza', 25.00),
('Muffin de Arándanos', 'Panqué con arándanos frescos', 'Pieza', 27.00),
('Dona Glaseada', 'Dona con glaseado de azúcar', 'Pieza', 18.00),
('Dona de Chocolate', 'Dona con cobertura de chocolate', 'Pieza', 20.00),
('Galletas de Avena', 'Galletas caseras de avena', '3 piezas', 15.00),
('Papas Sabritas', 'Papas fritas', '45g', 12.00),
('Cheetos', 'Palomitas de maíz con queso', '35g', 12.00),
('Sandwich Club', 'Sandwich de pollo, jamón y queso', 'Pieza', 45.00),
('Quesadilla', 'Quesadilla de queso', 'Pieza', 35.00),

-- Postres
('Cheesecake', 'Pastel de queso con frutos rojos', 'Rebanada', 55.00),
('Tiramisu', 'Postre italiano con café', 'Porción', 60.00),
('Helado Vainilla', 'Helado cremoso de vainilla', '2 bolas', 25.00),
('Helado Chocolate', 'Helado cremoso de chocolate', '2 bolas', 25.00),
('Flan Napolitano', 'Flan casero con caramelo', 'Porción', 30.00);

-- Relaciones producto-categoría
INSERT IGNORE INTO productoCategoria (idProducto, idCategoria) VALUES
-- Bebidas Calientes (categoría 1)
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1),
-- Bebidas Frías (categoría 2)
(11, 2), (12, 2), (13, 2), (14, 2), (15, 2), (16, 2), (17, 2),
-- Productos dulces (categoría 3)
(18, 3), (19, 3), (20, 3), (21, 3), (22, 3), (23, 3), (24, 3),
-- Snacks salados (categoría 4)
(25, 4), (26, 4),
-- Comida rápida (categoría 5)
(27, 5), (28, 5),
-- Postres (categoría 6)
(29, 6), (30, 6), (31, 6), (32, 6), (33, 6),
-- Algunos productos pueden estar en múltiples categorías
(18, 7), (19, 7), -- Croissants también en panadería
(20, 3), (21, 3); -- Muffins en snacks dulces

-- Crear índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_usuario_correo ON usuario(correo);
CREATE INDEX IF NOT EXISTS idx_usuario_rol ON usuario(rol);
CREATE INDEX IF NOT EXISTS idx_producto_nombre ON producto(nombre);
CREATE INDEX IF NOT EXISTS idx_producto_precio ON producto(precio);
CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha);
CREATE INDEX IF NOT EXISTS idx_venta_usuario ON venta(idUsuario);

-- Crear vistas útiles
CREATE OR REPLACE VIEW vista_productos_completos AS
SELECT 
    p.idProducto,
    p.nombre,
    p.descripcion,
    p.tamano,
    p.precio,
    GROUP_CONCAT(c.nombre SEPARATOR ', ') AS categorias,
    p.activo,
    p.fechaCreacion
FROM producto p
LEFT JOIN productoCategoria pc ON p.idProducto = pc.idProducto
LEFT JOIN categoria c ON pc.idCategoria = c.idCategoria
GROUP BY p.idProducto;

CREATE OR REPLACE VIEW vista_ventas_detalle AS
SELECT 
    v.idVenta,
    v.fecha,
    CONCAT(u.nombres, ' ', u.apellidoPaterno, ' ', u.apellidoMaterno) AS vendedor,
    u.rol,
    v.total,
    COUNT(vp.idProducto) AS total_productos,
    SUM(vp.cantidad) AS total_unidades
FROM venta v
INNER JOIN usuario u ON v.idUsuario = u.idUsuario
LEFT JOIN ventaProducto vp ON v.idVenta = vp.idVenta
GROUP BY v.idVenta;

-- Mostrar resumen de la configuración
SELECT 'Configuración completada exitosamente' AS status;
SELECT 
    'usuarios' AS tabla, COUNT(*) AS registros FROM usuario
UNION ALL
SELECT 
    'categorias' AS tabla, COUNT(*) AS registros FROM categoria
UNION ALL
SELECT 
    'productos' AS tabla, COUNT(*) AS registros FROM producto
UNION ALL
SELECT 
    'relaciones producto-categoria' AS tabla, COUNT(*) AS registros FROM productoCategoria;
