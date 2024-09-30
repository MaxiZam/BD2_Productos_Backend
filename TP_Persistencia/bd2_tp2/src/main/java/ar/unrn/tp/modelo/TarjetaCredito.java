package ar.unrn.tp.modelo;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
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
    private Cliente cliente;

    public TarjetaCredito(){}

    public TarjetaCredito(String numero, String marca, LocalDate fechaVencimiento) {
        this.numero = numero;
        this.marca = marca;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getMarca() {
        return this.marca;
    }

    public String getNumero(){
        return this.numero;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
