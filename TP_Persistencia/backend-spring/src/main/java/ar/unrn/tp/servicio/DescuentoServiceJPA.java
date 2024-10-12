package ar.unrn.tp.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import ar.unrn.tp.api.DescuentoService;
import ar.unrn.tp.modelo.DescuentoCompra;
import ar.unrn.tp.modelo.DescuentoProducto;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DescuentoServiceJPA implements DescuentoService {

    @PersistenceContext
    private EntityManager em;

    private final BigDecimal minimo = new BigDecimal(0);
    private final BigDecimal maximo = new BigDecimal(100);

    @Override
    public void crearDescuentoSobreTotal(
            String marcaTarjeta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Float porcentaje1) {

        BigDecimal porcentaje = new BigDecimal(porcentaje1);

        // Validar fechas
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // Validar porcentaje
        if (porcentaje.compareTo(minimo) < 0 || porcentaje.compareTo(maximo) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }

        // Crear el descuento sobre el total
        DescuentoCompra descuento = new DescuentoCompra(fechaDesde, fechaHasta, marcaTarjeta, porcentaje);
        em.persist(descuento);
    }

    @Override
    public void crearDescuentoSobreProducto(
            String marcaProducto,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Float porcentaje1) {

        BigDecimal porcentaje = new BigDecimal(porcentaje1);

        // Validar fechas
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // Validar porcentaje
        if (porcentaje.compareTo(minimo) < 0 || porcentaje.compareTo(maximo) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }

        // Crear el descuento por producto
        DescuentoProducto descuento = new DescuentoProducto(fechaDesde, fechaHasta, marcaProducto, porcentaje);
        em.persist(descuento);
    }
}


