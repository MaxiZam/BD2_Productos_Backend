package ar.unrn.tp.jpa.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import ar.unrn.tp.api.VentaService;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.DescuentoCompra;
import ar.unrn.tp.modelo.DescuentoProducto;
import ar.unrn.tp.modelo.Producto;
import ar.unrn.tp.modelo.TarjetaCredito;
import ar.unrn.tp.modelo.Venta;

@Service
public class VentaServiceJPA implements VentaService{

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void realizarVenta(Long idCliente, List<Long> productos, Long idTarjeta) {
        // Buscar el cliente
        Cliente cliente = em.getReference(Cliente.class, idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }

        // Verificar que la tarjeta pertenece al cliente
        TarjetaCredito tarjeta = em.getReference(TarjetaCredito.class, idTarjeta);
        if (tarjeta == null || !cliente.getTarjetas().contains(tarjeta)) {
            throw new IllegalArgumentException("La tarjeta no existe o no pertenece al cliente");
        }

        // Verificar que la lista de items no esté vacía
        List<Producto> productosDeVenta = new  ArrayList<Producto>();
        for(Long id: productos){
            productosDeVenta.add(em.find(Producto.class, id));
        }
        if (productosDeVenta.isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío");
        }

        // Calcular el monto total
        BigDecimal montoTotal = new BigDecimal(calcularMonto(productos, idTarjeta));

        // Crear y persistir la venta
        Venta venta = new Venta(cliente, tarjeta, productosDeVenta, montoTotal);
        em.persist(venta);
    }

    @Override
    public float calcularMonto(List<Long> productos, Long idTarjeta) {
        // Verificar que la lista de productos no esté vacía
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío");
        }

        // Obtener productos de la base de datos
        List<Producto> productosDeVenta = obtenerProductosPorIds(productos);

        // Verificar que se hayan encontrado productos
        if (productosDeVenta.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron productos en el carrito");
        }

        // Verificar la existencia de la tarjeta
        TarjetaCredito tarjeta = em.find(TarjetaCredito.class, idTarjeta);
        if (tarjeta == null) {
            throw new IllegalArgumentException("La tarjeta no existe");
        }

        // Calcular el total aplicando descuentos de productos
        BigDecimal total = calcularTotalConDescuentos(productosDeVenta);

        // Aplicar descuento sobre el total
        total = aplicarDescuentoTotal(total, tarjeta);

        return total.floatValue();
    }

    // Método para obtener productos por sus IDs
    private List<Producto> obtenerProductosPorIds(List<Long> productos) {
        List<Producto> productosDeVenta = new ArrayList<>();
        for (Long id : productos) {
            Producto producto = em.find(Producto.class, id);
            if (producto != null) {
                productosDeVenta.add(producto);
            }
        }
        return productosDeVenta;
    }

    // Método para calcular el total con descuentos de productos
    private BigDecimal calcularTotalConDescuentos(List<Producto> productosDeVenta) {
        LocalDate hoy = LocalDate.now();
        BigDecimal total = BigDecimal.ZERO;

        // Obtener descuentos de productos válidos para hoy
        List<DescuentoProducto> descuentosProducto = em.createQuery(
                        "SELECT d FROM DescuentoProducto d WHERE :hoy BETWEEN d.fechaInicio AND d.fechaFin",
                        DescuentoProducto.class)
                .setParameter("hoy", hoy)
                .getResultList();

        for (Producto product : productosDeVenta) {
            BigDecimal precioProducto = product.getPrecio();
            boolean descuentoAplicado = false;

            // Aplicar descuentos a cada producto
            for (DescuentoProducto descuento : descuentosProducto) {
                if (descuento.aplicaA(product)) {
                    precioProducto = descuento.aplicarDescuento(precioProducto);
                    descuentoAplicado = true;
                    break; // Solo aplicar un descuento por producto
                }
            }

            total = total.add(precioProducto);
        }
        return total;
    }

    // Método para aplicar el descuento sobre el total
    private BigDecimal aplicarDescuentoTotal(BigDecimal total, TarjetaCredito tarjeta) {
        LocalDate hoy = LocalDate.now();

        DescuentoCompra descuentoTotal = em.createQuery(
                        "SELECT d FROM DescuentoCompra d WHERE :hoy BETWEEN d.fechaInicio AND d.fechaFin AND d.marcaTarjeta = :marca",
                        DescuentoCompra.class)
                .setParameter("hoy", hoy)
                .setParameter("marca", tarjeta.getMarca())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (descuentoTotal != null) {
            total = descuentoTotal.aplicarDescuento(total);
        }
        return total;
    }

    @Override
    public List<Venta> ventas() {
        return em.createQuery("SELECT v FROM Venta v", Venta.class).getResultList();
    }
}
