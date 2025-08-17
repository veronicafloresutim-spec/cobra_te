-- Script de creación de base de datos para MariaDB
-- Carpeta: db

CREATE TABLE usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    rol ENUM('cajero', 'administrador') NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidoPaterno VARCHAR(100) NOT NULL,
    apellidoMaterno VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    telefono CHAR(10) NOT NULL,
    sexo ENUM('H', 'M') NULL
);

CREATE TABLE categoria (
    idCategoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE producto (
    idProducto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tamano VARCHAR(50) NULL,
    precio DECIMAL(10,2) NOT NULL
);

CREATE TABLE productoCategoria (
    idProducto INT NOT NULL,
    idCategoria INT NOT NULL,
    PRIMARY KEY (idProducto, idCategoria),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto),
    FOREIGN KEY (idCategoria) REFERENCES categoria(idCategoria)
);

CREATE TABLE venta (
    idVenta INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    idUsuario INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario)
);

CREATE TABLE ventaProducto (
    idVenta INT NOT NULL,
    idProducto INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (idVenta, idProducto),
    FOREIGN KEY (idVenta) REFERENCES venta(idVenta),
    FOREIGN KEY (idProducto) REFERENCES producto(idProducto)
);
