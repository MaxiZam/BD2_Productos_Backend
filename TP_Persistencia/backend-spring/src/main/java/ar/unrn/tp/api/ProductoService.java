package ar.unrn.tp.api;

import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Producto;

import java.util.List;

public interface ProductoService {
    void crearProducto(String codigo, String descripcion, String marca, float precio, Long idCategoria);
    void modificarProducto(Long idProducto, String descripcion, float precio, String marca, Long idCategoria, Long version);
    List listarProductos();
    Producto obtenerProductoPorId(Long id);
    Producto obtenerProductoPorCodigo(String codigo);
    void borrarProducto(Long id);
    void crearCategoria(String nombre);
    List obtenerCategorias();
}