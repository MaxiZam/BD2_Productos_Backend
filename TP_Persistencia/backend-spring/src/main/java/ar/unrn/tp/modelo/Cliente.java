package ar.unrn.tp.modelo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
@Data
@NoArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String dni;
    private String email;
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TarjetaCredito> tarjetas;

    public Cliente(String nombre, String apellido, String dni, String email) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede ser nulo o vacío");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del cliente no puede ser nulo o vacío");
        }
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del cliente no puede ser nulo o vacío");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("El email del cliente no es válido");
        }
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.tarjetas = new ArrayList<>();
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public void agregarTarjeta(TarjetaCredito tarjeta) {
        tarjeta.setCliente(this);  // Establece la relación bidireccional
        this.tarjetas.add(tarjeta);
    }
}
