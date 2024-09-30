package ar.unrn.tp.api;

import java.util.List;

import ar.unrn.tp.modelo.TarjetaCredito;

public interface ClienteService {
    void crearCliente(String nombre, String apellido, String dni, String email);
    void modificarCliente(Long idCliente, String nombre, String apellido,String dni, String email);
    void agregarTarjeta(Long idCliente, String nro, String marca);
    List<TarjetaCredito> listarTarjetas(Long idCliente);
}
