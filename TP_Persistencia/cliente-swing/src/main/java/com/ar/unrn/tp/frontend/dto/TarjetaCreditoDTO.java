package com.ar.unrn.tp.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TarjetaCreditoDTO {
    private Long id;
    private String numero;
    private String marca;
    private LocalDate fechaVencimiento;
    private Long clienteId;
}

