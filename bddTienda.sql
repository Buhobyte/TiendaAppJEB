CREATE SCHEMA IF NOT EXISTS ew
    AUTHORIZATION postgres;

-- Cambiar al esquema 'ew'
SET search_path TO ew;

-- Crear tabla 'productos' en el esquema 'ew'
CREATE TABLE producto (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    precio NUMERIC(10, 2),
    stock INTEGER
);

-- Crear tabla 'clientes' en el esquema 'ew'
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    direccion VARCHAR(100),
    telefono VARCHAR(15)
);

-- Crear tabla 'ventas' en el esquema 'ew'
CREATE TABLE venta (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES ew.cliente (id),
    producto_id INTEGER REFERENCES ew.producto (id),
    cantidad INTEGER,
    fecha_venta DATE
);
