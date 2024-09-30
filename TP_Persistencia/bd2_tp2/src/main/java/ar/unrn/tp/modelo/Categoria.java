package ar.unrn.tp.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    public Categoria(String nombre){
        this.nombre = nombre;
    }

    public String getNombreCategoria(){
        return this.nombre;
    }

    public Long getId() {
        return id;
    }
}
