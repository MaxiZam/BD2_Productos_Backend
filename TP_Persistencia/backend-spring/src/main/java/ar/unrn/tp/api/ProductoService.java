package ar.unrn.tp.api;

import java.util.List;

import ar.unrn.tp.modelo.Producto;

public interface ProductoService {
    void crearProducto(String codigo, String descripcion, String marca, float precio, Long idCategoria);
    void modificarProducto(Long idProducto, String descripcion, float precio, Long idCategoria);
    List listarProductos();
    void crearCategoria(String nombre);
}