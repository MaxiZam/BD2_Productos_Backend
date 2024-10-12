package com.ar.unrn.tp.frontend.clases;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    private Long id;
    private LocalDateTime fecha;
    private Cliente cliente;
    private TarjetaCredito tarjeta;
    private List<Producto> productos;
    private BigDecimal montoTotal;
}
