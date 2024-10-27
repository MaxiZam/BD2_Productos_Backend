package ar.unrn.tp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String codigo;
    private Float precio;
    private String nombre;
    private String marca;
    private Long categoriaId;
    private Long version;
}

