package ar.unrn.tp.api;

import java.util.List;

public interface ProductoService {
    void crearProducto(String codigo, String descripcion, String marca, float precio, Long idCategoria);
    void modificarProducto(Long idProducto, String descripcion, float precio, Long idCategoria);
    List listarProductos();
    void crearCategoria(String nombre);
}