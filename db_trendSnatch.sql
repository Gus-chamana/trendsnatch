-- 1. Crear la base de datos
CREATE DATABASE trendsnatch_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Crear un usuario y darle permisos (reemplaza 'admin123' por una contraseña segura)
CREATE USER 'trendsnatch_user'@'localhost' IDENTIFIED BY 'admin1234';
GRANT ALL PRIVILEGES ON trendsnatch_db.* TO 'trendsnatch_user'@'localhost';
FLUSH PRIVILEGES;

-- 3. Usar la base de datos creada
USE trendsnatch_db;

-- 4. Crear las tablas 

-- Tabla de Roles para la seguridad
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla de Usuarios (combina Admin y Cliente)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol_id BIGINT,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- Tabla de Productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria VARCHAR(100),
    url_video_fuente VARCHAR(255), -- Enlace al TikTok, Instagram Reel, etc.
    url_imagen_referencia VARCHAR(255), -- Screenshot o imagen del producto
    red_social_origen VARCHAR(50), -- 'tiktok', 'instagram', 'facebook'
    visible BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Pedidos
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) NOT NULL, -- 'PENDIENTE', 'EN_PROCESO', 'ENVIADO', 'CANCELADO'
    total DOUBLE NOT NULL,
    usuario_id INT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Tabla de Detalle de Pedido (la tabla intermedia entre Pedidos y Productos)
CREATE TABLE detalles_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    pedido_id INT,
    producto_id INT,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Insertar roles básicos
INSERT INTO roles(nombre) VALUES('ROLE_ADMIN');
INSERT INTO roles(nombre) VALUES('ROLE_USER');

-- Insertar campo activo
ALTER TABLE usuarios
ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;