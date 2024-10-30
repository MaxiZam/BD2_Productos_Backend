package ar.unrn.tp.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fecha;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @ManyToOne
    @JoinColumn(name = "tarjeta_id")
    private TarjetaCredito tarjeta;
    @ManyToMany
    private List<Producto> productos;
    private BigDecimal montoTotal;
    @Column(unique = true)
    private String numeroVenta;

    public Venta(Cliente cliente, TarjetaCredito tarjeta, List<Producto> productos, BigDecimal montoTotal, String numeroVenta) {
        this.fecha = LocalDateTime.now();
        this.cliente = cliente;
        this.tarjeta = tarjeta;
        this.productos = productos;
        this.montoTotal = montoTotal;
        this.numeroVenta = numeroVenta;
    }
}
