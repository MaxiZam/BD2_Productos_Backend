package ar.unrn.tp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DescuentoCompraDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String marcaTarjeta;
    private Float porcentajeDescuento;
}

