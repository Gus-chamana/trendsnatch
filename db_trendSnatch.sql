-- 1. Crear la base de datos
DROP DATABASE trendsnatch_db;
CREATE DATABASE trendsnatch_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
DROP USER 'trendsnatch_user'@'localhost';
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

ALTER TABLE pedidos
ADD COLUMN codigo_referencia VARCHAR(20) NULL UNIQUE AFTER total;

-- Inserción de 8 productos de ejemplo en la tabla 'productos'
INSERT INTO productos (nombre, descripcion, precio, stock, categoria, url_video_fuente, url_imagen_referencia, red_social_origen, visible) VALUES
(
    'Top Corset Floral Aesthetic',
    'Top estilo corset con estampado floral, perfecto para un look coquette y aesthetic. Visto en los hauls más virales de TikTok. Material: Algodón y Spandex.',
    89.90,
    50,
    'Blusas',
    'https://www.tiktok.com/embed/75283593470445501500',
    'https://img-1.kwcdn.com/thumbnail/s/01d83092ec4a60ba545669be161fe115_12b2b626c7e5.jpg?imageView2/2/w/1300/q/80/format/webp',
    'TikTok',
    TRUE
),
(
    'Pantalón Cargo Ancho Streetwear',
    'El pantalón cargo que no puede faltar en tu armario. Estilo ancho y cómodo, ideal para un look urbano y relajado. Popularizado en Reels de Instagram. Múltiples bolsillos funcionales.',
    149.90,
    35,
    'Pantalones',
    'https://www.tiktok.com/embed/7358608396808981765',
    'https://www.projectxparis.com/74487-thickbox_default/pantalon-cargo-tecnico.jpg',
    'TikTok',
    TRUE
),
(
    'Blusa Oversize Vintage',
    'Blusa de algodón estilo oversize, tendencia TikTok verano 2025.',
    449.00,
    25,
    'Blusas',
    'https://www.tiktok.com/embed/v2/7226272281239469314',
    'https://images.unsplash.com/photo-1517841905240-472988babdf9',
    'tiktok',
    TRUE
),
(
    'Sudadera Minimalista',
    'Sudadera básica con estampado minimalista, moda urbana.',
    699.90,
    15,
    'Sudaderas',
    'https://www.tiktok.com/embed/v2/7245763211179093250',
    'https://images.unsplash.com/photo-1503342217505-b0a15ec3261c',
    'tiktok',
    TRUE
),
(
    'Vestido Midi Floral',
    'Vestido midi estampado floral, tendencia primavera.',
    799.00,
    20,
    'Vestidos',
    'https://www.tiktok.com/embed/7528359347044550150',
    'https://img-1.kwcdn.com/thumbnail/s/f39a1ba62a6a1d59a7720fec7e42bf2f_39003417c10f.jpg?imageView2/2/w/1300/q/80/format/webp',
    'tiktok',
    TRUE
);