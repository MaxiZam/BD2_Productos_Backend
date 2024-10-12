package com.ar.unrn.tp.frontend.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Long id;
    private String nombre;

    // Constructor adicional
    public Categoria(String nombre) {
        this.nombre = nombre;
    }
}
