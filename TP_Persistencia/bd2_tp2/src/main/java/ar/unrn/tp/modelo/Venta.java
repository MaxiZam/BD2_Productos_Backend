package ar.unrn.tp.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
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

    public Cliente getCliente() {
        return this.cliente;
    }

    public TarjetaCredito geTarjeta() {
        return this.tarjeta;
    }
    
    public List<Producto> getProductos() {
        return this.productos;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    } 
    
    public BigDecimal getMontoTotal() {
        return this.montoTotal;
    }
}
