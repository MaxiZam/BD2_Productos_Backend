package ar.unrn.tp.modelo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

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

    public Venta(Cliente cliente, TarjetaCredito tarjeta, List<Producto> productos, BigDecimal montoTotal) {
        this.fecha = LocalDateTime.now();
        this.cliente = cliente;
        this.tarjeta = tarjeta;
        this.productos = productos;
        this.montoTotal = montoTotal;
    }
}
