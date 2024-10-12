package ar.unrn.tp.api;

import java.util.List;

public interface VentaService {
    void realizarVenta(Long idCliente, List<Long> productos, Long idTarjeta);
    float calcularMonto(List<Long> productos, Long idTarjeta);
    List  listarVentas();
}
