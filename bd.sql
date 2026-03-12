-- ============================================
-- SCRIPT PARA CREAR LA BASE DE DATOS COMPLETA
-- PROYECTO BIBLIOTECA ONLINE
-- ============================================

-- 1. Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- 2. Eliminar tablas si existen (para empezar limpio)
DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS usuario;

-- 3. Crear tabla de usuarios
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) DEFAULT 'usuario'
);

-- 4. Crear tabla de libros
CREATE TABLE libro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255),
    editorial VARCHAR(255),
    cantidad INT DEFAULT 1,
    cantidad_disponible INT DEFAULT 1,
    disponible BOOLEAN DEFAULT TRUE
);

-- 5. Crear tabla de reservas
CREATE TABLE reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    libro_id BIGINT NOT NULL,
    fecha_reserva DATE NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (libro_id) REFERENCES libro(id)
);

-- 6. Insertar usuarios de prueba
INSERT INTO usuario (nombre, password, tipo) VALUES 
('jesus', '12345', 'admin'),
('admin', '123456', 'admin');

-- 7. Insertar libros de prueba
INSERT INTO libro (titulo, autor, editorial, cantidad, cantidad_disponible) VALUES 
('El Principito', 'Antoine de Saint-Exupéry', 'Reynal & Hitchcock', 10, 10),
('Don Quijote', 'Miguel de Cervantes', 'Planeta', 10, 10),
('Cien años de soledad', 'Gabriel García Márquez', 'Sudamericana', 10, 10),
('1984', 'George Orwell', 'Debolsillo', 10, 10),
('Clean Code', 'Robert Martin', 'Prentice Hall', 10, 10);

-- 8. Verificar que todo se creó correctamente
SELECT '✅ USUARIOS CREADOS:' AS mensaje;
SELECT * FROM usuario;

SELECT '✅ LIBROS CREADOS:' AS mensaje;
SELECT * FROM libro;

SELECT '✅ BASE DE DATOS LISTA PARA USAR' AS mensaje;