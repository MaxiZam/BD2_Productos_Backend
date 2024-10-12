package com.ar.unrn.tp.frontend.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoProducto implements Descuento {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String marcaProducto;
    private BigDecimal porcentajeDescuento;
}