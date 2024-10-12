package ar.unrn.tp.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ar.unrn.tp.modelo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import ar.unrn.tp.api.VentaService;

@Service
@Transactional
public class VentaServiceJPA implements VentaService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void realizarVenta(Long idCliente, List<Long> productos, Long idTarjeta) {
        // Buscar el cliente
        Cliente cliente = em.find(Cliente.class, idCliente);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }

        // Verificar que la tarjeta pertenece al cliente
        TarjetaCredito tarjeta = em.find(TarjetaCredito.class, idTarjeta);
        if (tarjeta == null || !cliente.getTarjetas().contains(tarjeta)) {
            throw new IllegalArgumentException("La tarjeta no existe o no pertenece al cliente");
        }

        // Verificar que la lista de productos no esté vacía
        List<Producto> productosDeVenta = obtenerProductosPorIds(productos);
        if (productosDeVenta.isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío");
        }

        // Calcular el monto total
        BigDecimal montoTotal = BigDecimal.valueOf(calcularMonto(productos, idTarjeta));

        // Crear y persistir la venta
        Venta venta = new Venta(cliente, tarjeta, productosDeVenta, montoTotal,generarNumeroVenta());

        em.persist(venta);
    }

    // Método para generar el número de venta en formato N-AÑO
    private String generarNumeroVenta() {
        // Obtiene el año actual
        int year = LocalDate.now().getYear();
        // Aquí podrías agregar lógica para obtener el contador (N) del año actual.
        int n = obtenerContadorDeVentas(year); // Método que debes implementar para obtener el contador

        return n + "-" + year; // Retorna el número en formato N-AÑO
    }

    // Método para obtener el contador de ventas para el año actual
    private int obtenerContadorDeVentas(int year) {
        // Cuenta cuántas ventas hay en el año actual
        Long count = em.createQuery("SELECT COUNT(v) FROM Venta v WHERE YEAR(v.fecha) = :year", Long.class)
                .setParameter("year", year)
                .getSingleResult();
        return count.intValue() + 1; // Devuelve el número de ventas más 1
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

    private BigDecimal calcularTotalConDescuentos(List<Producto> productosDeVenta) {
        LocalDate hoy = LocalDate.now();
        BigDecimal total = BigDecimal.ZERO;

        // Obtener descuentos de productos válidos para hoy
        List<DescuentoProducto> descuentosProducto = em.createQuery(
                        "SELECT d FROM DescuentoProducto d WHERE :hoy BETWEEN d.fechaInicio AND d.fechaFin",
                        DescuentoProducto.class)
                .setParameter("hoy", hoy)
                .getResultList();

        for (Producto producto : productosDeVenta) {
            BigDecimal precioProducto = producto.getPrecio();
            // Aplicar descuentos a cada producto
            for (DescuentoProducto descuento : descuentosProducto) {
                if (descuento.aplicaA(producto)) {
                    precioProducto = descuento.aplicarDescuento(precioProducto);
                    break; // Solo aplicar un descuento por producto
                }
            }
            total = total.add(precioProducto);
        }
        return total;
    }

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
    public List<Venta> listarVentas() {
        return em.createQuery("SELECT v FROM Venta v", Venta.class).getResultList();
    }
}


