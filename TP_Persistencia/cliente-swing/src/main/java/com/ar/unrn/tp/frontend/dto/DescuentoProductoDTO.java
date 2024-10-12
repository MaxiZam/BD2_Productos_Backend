package com.ar.unrn.tp.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DescuentoProductoDTO extends DescuentoDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String marcaProducto;
    private Float porcentajeDescuento;
}

