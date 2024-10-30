package ar.unrn.tp.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ar.unrn.tp.modelo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import ar.unrn.tp.api.VentaService;

@Service
@Transactional
public class VentaServiceJPA implements VentaService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private RedisTemplate<String, List<Venta>> redisTemplate;

    private static final String VENTAS_CACHE_KEY_PREFIX = "ventas_";

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
        Venta venta = new Venta(cliente, tarjeta, productosDeVenta, montoTotal, generarNumeroVenta());

        registrarNuevaVenta(venta);
    }

    // Método para generar el número de venta en formato N-AÑO
    private String generarNumeroVenta() {
        int year = LocalDate.now().getYear();
        int n = obtenerContadorDeVentas(year);
        return n + "-" + year;
    }

    private int obtenerContadorDeVentas(int year) {
        Long count = em.createQuery("SELECT COUNT(v) FROM Venta v WHERE YEAR(v.fecha) = :year", Long.class)
                .setParameter("year", year)
                .getSingleResult();
        return count.intValue() + 1;
    }

    @Override
    public float calcularMonto(List<Long> productos, Long idTarjeta) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío");
        }

        List<Producto> productosDeVenta = obtenerProductosPorIds(productos);
        if (productosDeVenta.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron productos en el carrito");
        }

        TarjetaCredito tarjeta = em.find(TarjetaCredito.class, idTarjeta);
        if (tarjeta == null) {
            throw new IllegalArgumentException("La tarjeta no existe");
        }

        BigDecimal total = calcularTotalConDescuentos(productosDeVenta);
        return aplicarDescuentoTotal(total, tarjeta).floatValue();
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

        List<DescuentoProducto> descuentosProducto = em.createQuery(
                        "SELECT d FROM DescuentoProducto d WHERE :hoy BETWEEN d.fechaInicio AND d.fechaFin",
                        DescuentoProducto.class)
                .setParameter("hoy", hoy)
                .getResultList();

        for (Producto producto : productosDeVenta) {
            BigDecimal precioProducto = producto.getPrecio();
            for (DescuentoProducto descuento : descuentosProducto) {
                if (descuento.aplicaA(producto)) {
                    precioProducto = descuento.aplicarDescuento(precioProducto);
                    break;
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

    @Override
    public void borrarVenta(Long id) {
        try {
            Venta venta = em.find(Venta.class, id);
            if (venta == null) {
                throw new IllegalArgumentException("La venta no existe");
            }
            em.remove(venta);
        } catch (PersistenceException e) {
            throw new RuntimeException("Error al eliminar la venta: " + e.getMessage(), e);
        }
    }

    // Método para obtener las últimas 3 ventas de un cliente, primero intentando en caché
    public List<Venta> obtenerUltimasVentasCliente(Long clienteId) {
        String cacheKey = VENTAS_CACHE_KEY_PREFIX + clienteId;

        // Intentar obtener ventas desde la cache
        List<Venta> ventasCacheadas = redisTemplate.opsForValue().get(cacheKey);
        if (ventasCacheadas != null && !ventasCacheadas.isEmpty()) {
            return ventasCacheadas;
        }

        // Si no están en cache, obtener de la base de datos y almacenar en cache
        List<Venta> ultimasVentas = em.createQuery("SELECT v FROM Venta v WHERE v.cliente.id = :clienteId ORDER BY v.fecha DESC", Venta.class)
                .setParameter("clienteId", clienteId)
                .setMaxResults(3)
                .getResultList();

        // Guardar el resultado en la cache para futuras consultas
        redisTemplate.opsForValue().set(cacheKey, ultimasVentas);

        return ultimasVentas;
    }

    // Método para registrar una nueva venta y actualizar la cache
    private void registrarNuevaVenta(Venta venta) {
        em.persist(venta);
        actualizarCacheUltimasVentas(venta.getCliente().getId());
    }

    // Método privado para actualizar el cache con la nueva venta
    private void actualizarCacheUltimasVentas(Long clienteId) {
        String cacheKey = VENTAS_CACHE_KEY_PREFIX + clienteId;

        // Obtener la lista actualizada de últimas ventas
        List<Venta> ultimasVentas = em.createQuery("SELECT v FROM Venta v WHERE v.cliente.id = :clienteId ORDER BY v.fecha DESC", Venta.class)
                .setParameter("clienteId", clienteId)
                .setMaxResults(3)
                .getResultList();

        // Actualizar cache con la nueva lista de ventas
        redisTemplate.opsForValue().set(cacheKey, ultimasVentas);
    }
}



