package ar.unrn.tp.api;

import java.time.LocalDate;

public interface DescuentoService {
    void crearDescuentoSobreTotal(String marcaTarjeta, LocalDate fechaDesde, LocalDate fechaHasta, Float porcentaje);
    void crearDescuentoSobreProducto(String marcaProducto, LocalDate fechaDesde, LocalDate fechaHasta, Float porcentaje);
}
