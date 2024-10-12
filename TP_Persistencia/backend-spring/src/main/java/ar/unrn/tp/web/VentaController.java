package ar.unrn.tp.web;

import ar.unrn.tp.api.VentaService;
import ar.unrn.tp.dto.VentaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Método para realizar una venta
    @PostMapping("/crear")
    public ResponseEntity<Void> realizarVenta(@RequestBody VentaDTO ventaDTO) {
        try {
            ventaService.realizarVenta(ventaDTO.getClienteId(), ventaDTO.getProductos(), ventaDTO.getTarjetaId());
            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    // Método para calcular el monto
    @PutMapping("/calcular-monto")
    public ResponseEntity<Float> calcularMonto(@RequestBody List<Long> productosId, @RequestParam Long tarjetaId) {
        try {
            float monto = ventaService.calcularMonto(productosId, tarjetaId);
            return ResponseEntity.ok(monto); // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    // Método para listar todas las ventas
    @GetMapping("/listar")
    public ResponseEntity<List<VentaDTO>> listarVentas() {
        try {
            List<VentaDTO> ventas = ventaService.listarVentas();
            return ResponseEntity.ok(ventas); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
}

