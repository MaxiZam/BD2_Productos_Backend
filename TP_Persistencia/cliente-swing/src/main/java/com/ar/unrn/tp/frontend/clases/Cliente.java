package com.ar.unrn.tp.frontend.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private List<TarjetaCredito> tarjetas;
}
