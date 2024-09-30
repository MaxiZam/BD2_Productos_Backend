package ar.unrn.tp.jpa.servicio;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Producto;

@Service
@Transactional
public class ProductoServiceJPA implements ProductoService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void crearProducto(String codigo, String descripcion,String marca, float precio, Long idCategoria) {
        // Validar que el código no se repita y que la categoría exista
        if (em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo")
              .setParameter("codigo", codigo)
              .getResultList()
              .isEmpty()) {
            Categoria categoria = em.find(Categoria.class, idCategoria);
            if (categoria != null) {
                Producto producto = new Producto(codigo, descripcion, categoria, marca,BigDecimal.valueOf(precio));
                em.persist(producto);
            } else {
                throw new IllegalArgumentException("La categoría especificada no existe");
            }
        } else {
            throw new IllegalArgumentException("Ya existe un producto con ese código");
        }
    }


    public void modificarProducto(Long idProducto, String descripcion, float precio, Long idCategoria) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Producto> listarProductos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
