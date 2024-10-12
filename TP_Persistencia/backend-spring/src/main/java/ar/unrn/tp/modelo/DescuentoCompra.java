package ar.unrn.tp.modelo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class DescuentoCompra implements Descuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String marcaTarjeta;
    private BigDecimal porcentajeDescuento;

    public DescuentoCompra(LocalDate fechaInicio, LocalDate fechaFin, String medioPago, BigDecimal porcentajeDescuento) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.marcaTarjeta = medioPago;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public boolean esValido(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    @Override
    public BigDecimal aplicarDescuento(BigDecimal monto) {
        return monto.subtract(monto.multiply(porcentajeDescuento));
    }
}