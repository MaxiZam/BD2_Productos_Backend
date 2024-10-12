package ar.unrn.tp.backend;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.unrn.tp.api.ClienteService;
import ar.unrn.tp.api.DescuentoService;
import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.api.VentaService;
import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.DescuentoCompra;
import ar.unrn.tp.modelo.Producto;
import ar.unrn.tp.modelo.TarjetaCredito;
import ar.unrn.tp.modelo.Venta;

@SpringBootTest
@Transactional
class ServiciosJPATest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private DescuentoService descuentoService;
    @Autowired
    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        // No es necesario manejar manualmente las transacciones aquí porque @Transactional se encarga de esto.
    }

    @Test
    void testCrearCliente() {
        // Crear el cliente
        clienteService.crearCliente("Juan", "Pérez", "12345678", "juan@example.com");

        // Intentar recuperar el cliente
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni", Cliente.class)
                .setParameter("dni", "12345678")
                .getSingleResult();

        // Validar los datos del cliente
        assertNotNull(cliente);
        assertEquals("Juan", cliente.getNombre());
        assertEquals("Pérez", cliente.getApellido());
    }

    @Test
    void testCrearClienteDNIDuplicado() {
        clienteService.crearCliente("Juan", "Pérez", "12345678", "juan@example.com");
        
        // Verifica que al intentar crear un cliente con el mismo DNI, se lance la excepción esperada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.crearCliente("María", "González", "12345678", "maria@example.com");
        });
        
        assertEquals("Ya existe un cliente con ese DNI", exception.getMessage());
    }

    @Test
    void testAgregarTarjeta() {
        // Crear el cliente
        clienteService.crearCliente("Juan", "Pérez", "12345678", "juan@example.com");

        // Recuperar el cliente
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni", Cliente.class)
                .setParameter("dni", "12345678")
                .getSingleResult();

        System.out.println("Cliente obtenido: " + cliente.getNombre() + ", DNI:" + cliente.getDni());

        // Agregar la tarjeta
        clienteService.agregarTarjeta(cliente.getId(), "1234567890123456", "VISA");

        // Listar las tarjetas del cliente
        List<TarjetaCredito> tarjetas = clienteService.listarTarjetas(cliente.getId());

        // Afirmar que se ha agregado una tarjeta
        assertEquals(1, tarjetas.size());
        assertEquals("1234567890123456", tarjetas.get(0).getNumero());
    }

    @Test
    void testCrearDescuentoSobreTotal() {
        LocalDate fechaDesde = LocalDate.now();
        LocalDate fechaHasta = fechaDesde.plusDays(30);
        
        descuentoService.crearDescuentoSobreTotal("VISA", fechaDesde, fechaHasta, 10f);
        
        DescuentoCompra descuento = em.createQuery("SELECT d FROM DescuentoCompra d WHERE d.marcaTarjeta = :marca", DescuentoCompra.class)
                                          .setParameter("marca", "VISA")
                                          .getSingleResult();
        
        assertNotNull(descuento);
        BigDecimal num = new BigDecimal(10);
        assertEquals(num, descuento.getPorcentajeDescuento());
    }

    @Test
    void testRealizarVenta() {
        // Crear cliente y producto
        clienteService.crearCliente("Juan", "Pérez", "12345678", "juan@example.com");
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.dni = :dni", Cliente.class)
                            .setParameter("dni", "12345678")
                            .getSingleResult();
        
        Categoria categoria = new Categoria("Celulares");
        em.persist(categoria);
        
        productoService.crearProducto("PROD001", "Smartphone","ACME", 500f, categoria.getId());
        Producto producto = em.createQuery("SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                              .setParameter("codigo", "PROD001")
                              .getSingleResult();
        
        clienteService.agregarTarjeta(cliente.getId(), "1234567890123456", "VISA");
        List<TarjetaCredito> tarjetas = clienteService.listarTarjetas(cliente.getId());

        if (!tarjetas.isEmpty()) {
            TarjetaCredito tarjeta = tarjetas.get(0);
            ventaService.realizarVenta(cliente.getId(), Arrays.asList(producto.getId()), tarjeta.getId());
        } else {
            fail("No se encontró ninguna tarjeta para el cliente.");
        }
        
        List<Venta> ventas = ventaService.listarVentas();
        assertEquals(1, ventas.size());
        BigDecimal num = new BigDecimal(500);
        assertEquals(num, ventas.get(0).getMontoTotal());
    }
}

