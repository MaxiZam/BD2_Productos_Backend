package ar.unrn.tp.api;

import ar.unrn.tp.modelo.Venta;

import java.util.List;

public interface VentaService {
    void realizarVenta(Long clienteId, List<Long> productos, Long idTarjeta);
    float calcularMonto(List<Long> productos, Long idTarjeta);
    List  listarVentas();
    void borrarVenta(Long id);
    List obtenerUltimasVentasCliente(Long clienteId);
}
