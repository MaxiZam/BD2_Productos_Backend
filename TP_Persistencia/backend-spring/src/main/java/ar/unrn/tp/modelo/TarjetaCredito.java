package ar.unrn.tp.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class TarjetaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String numero;
    private String marca;
    private LocalDate fechaVencimiento;
    @ManyToOne
    @JoinColumn(name = "cliente_id")  // Clave foránea que conecta a Cliente
    @JsonIgnore // Evita la serialización en JSON
    private Cliente cliente;

    public TarjetaCredito(String numero, String marca, LocalDate fechaVencimiento, Cliente cliente) {
        this.numero = numero;
        this.marca = marca;
        this.fechaVencimiento = fechaVencimiento;
        this.cliente = cliente;
    }
}
