package ar.unrn.tp.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Descuento {
    boolean esValido(LocalDate fecha);
    BigDecimal aplicarDescuento(BigDecimal monto);
}