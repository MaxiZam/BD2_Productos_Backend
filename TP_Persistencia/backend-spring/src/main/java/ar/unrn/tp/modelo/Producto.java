package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String codigo;
    private String descripcion;
    private String marca;
    private BigDecimal precio;
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Version // Agrega esta anotación
    private Long version;

    public Producto(String codigo, String descripcion, Categoria categoria, String marca, BigDecimal precio) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto no puede ser nulo o vacío");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto no puede ser nula o vacía");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría del producto no puede ser nula");
        }
        if (marca == null|| descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca del producto no puede ser nula o vacía");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser un valor positivo");
        }
        
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.marca = marca;
        this.precio = precio;
    }
}
