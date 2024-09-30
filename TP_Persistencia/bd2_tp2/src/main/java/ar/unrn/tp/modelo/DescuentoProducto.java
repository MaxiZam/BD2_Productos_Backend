package ar.unrn.tp.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DescuentoProducto implements Descuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String marcaProducto;
    private BigDecimal porcentajeDescuento;

    public DescuentoProducto(LocalDate fechaInicio, LocalDate fechaFin, String marcaProducto, BigDecimal porcentajeDescuento) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.marcaProducto = marcaProducto;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public boolean esValido(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    @Override
    public BigDecimal aplicarDescuento(BigDecimal monto) {
        return monto.subtract(monto.multiply(porcentajeDescuento));
    }

    public boolean aplicaA(Producto producto){
        return (this.marcaProducto.equals(producto.getMarca()));
    }

    public String getMarcaProducto() {
       return this.marcaProducto;
    }

}
