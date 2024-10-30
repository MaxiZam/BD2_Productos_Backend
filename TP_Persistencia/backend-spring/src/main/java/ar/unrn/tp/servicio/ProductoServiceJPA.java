package ar.unrn.tp.servicio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ar.unrn.tp.dto.CategoriaDTO;
import ar.unrn.tp.exception.OptimisticLockException;
import ar.unrn.tp.mapper.ProductoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Producto;

@Service
@Transactional
public class ProductoServiceJPA implements ProductoService { //docker exec -it a39dca4055c8 psql -U postgres productos_db (como ingresar a la base de datos)

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProductoMapper productoMapper;

    @Override
    public void crearProducto(String codigo, String descripcion, String marca, float precio, Long idCategoria) {
        if (codigoYaExistente(codigo)) {
            throw new IllegalArgumentException("Ya existe un producto con ese código");
        }

        Categoria categoria = em.find(Categoria.class, idCategoria);
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría especificada no existe");
        }

        Producto producto = new Producto(codigo, descripcion, categoria, marca, BigDecimal.valueOf(precio));
        em.persist(producto);
    }

    @Override
    public void modificarProducto(Long idProducto, String nombre, float precio, String marca, Long idCategoria, Long version) {
        if (idProducto == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        if (idCategoria == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo.");
        }

        Producto producto = em.find(Producto.class, idProducto);
        if (producto == null) {
            throw new IllegalArgumentException("El producto especificado no existe.");
        }

        Categoria categoria = em.find(Categoria.class, idCategoria);
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría especificada no existe.");
        }

        // Comprobación de la versión para evitar conflictos de concurrencia
        if (!producto.getVersion().equals(version)) {
            throw new OptimisticLockException("El producto ha sido modificado por otro usuario. Por favor, actualiza la página.");
        }

        // Actualización de los campos
        producto.setNombre(nombre);
        producto.setPrecio(BigDecimal.valueOf(precio));
        producto.setMarca(marca);
        producto.setCategoria(categoria);

        em.merge(producto);
    }


    @Override
    public List<Producto> listarProductos() {
        return em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        Producto producto = em.find(Producto.class, id);
        if (producto == null) {
            throw new IllegalArgumentException("El producto con ID " + id + " no existe");
        }
        return producto;
    }


    @Override
    public Producto obtenerProductoPorCodigo(String codigo) {
        try {

            return em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                    .setParameter("codigo", codigo)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new IllegalArgumentException("El producto no existe");
        } catch (PersistenceException e) {
            throw new RuntimeException("Error al obtener el producto: " + e.getMessage(), e);
        }
    }


    @Override
    public void borrarProducto(Long id) {
        try {
            Producto producto = em.find(Producto.class, id);
            if (producto == null) {
                throw new IllegalArgumentException("El producto no existe");
            }
            em.remove(producto);
        } catch (PersistenceException e) {
            throw new RuntimeException("Error al eliminar el producto: " + e.getMessage(), e);
        }
    }

    @Override
    public void crearCategoria(String nombre) {
        if(nombre.isEmpty()){
            throw new IllegalArgumentException("No se ingreso ningun nombre para la categoria");
        }
        Categoria nuevaCategoria = new Categoria(nombre);
        em.persist(nuevaCategoria);
    }

    private boolean codigoYaExistente(String codigo) {
        return !em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                .setParameter("codigo", codigo)
                .getResultList()
                .isEmpty();
    }

    // Función para obtener los productos a partir de una lista de IDs
    public List<Producto> obtenerProductosPorIds(List<Long> productosIds) {
        if (productosIds == null || productosIds.isEmpty()) {
            return new ArrayList<>(); // Retorna lista vacía si no hay IDs
        }

        // Realizar la consulta con los IDs de productos
        return em.createQuery("SELECT p FROM Producto p WHERE p.id IN :ids", Producto.class)
                .setParameter("ids", productosIds)
                .getResultList();
    }

    public List<Categoria> obtenerCategorias(){
        return em.createQuery("SELECT p FROM Categoria p", Categoria.class)
                .getResultList();
    }
}


