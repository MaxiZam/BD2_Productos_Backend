package com.ar.unrn.tp.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String codigo;
    private Float precio;
    private String descripcion;
    private String marca;
    private Long categoriaId;
}

