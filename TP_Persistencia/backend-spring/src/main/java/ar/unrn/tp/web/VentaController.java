package ar.unrn.tp.web;

import ar.unrn.tp.api.VentaService;
import ar.unrn.tp.dto.ProductoDTO;
import ar.unrn.tp.dto.VentaDTO;
import ar.unrn.tp.mapper.VentaMapper;
import ar.unrn.tp.modelo.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaMapper ventaMapper;

    @PostMapping
    public ResponseEntity<Void> realizarVenta(@RequestBody VentaDTO ventaDTO) {
        if (ventaDTO == null || ventaDTO.getProductos() == null || ventaDTO.getClienteId() == null || ventaDTO.getTarjetaId() == null) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request si hay datos nulos
        }

        try {
            // Extraer los IDs de los productos del DTO de venta
            List<Long> productosId = ventaDTO.getProductos().stream()
                    .map(ProductoDTO::getId)
                    .collect(Collectors.toList());

            // Llamar al servicio para realizar la venta
            ventaService.realizarVenta(ventaDTO.getClienteId(), productosId, ventaDTO.getTarjetaId());

            return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            // Aquí podrías registrar la excepción para análisis posterior
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
    @GetMapping
    public ResponseEntity<List<VentaDTO>> listarVentas() {
        try {
            List<VentaDTO> ventas = ventaService.listarVentas();
            return ResponseEntity.ok(ventas); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> borrarVenta(@PathVariable Long id) {
        try {
            ventaService.borrarVenta(id);
            return ResponseEntity.ok("Venta eliminada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: La venta con ID " + id + " no existe.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la venta.");
        }
    }

    @GetMapping("/ultimas/{id}")
    public ResponseEntity<List<VentaDTO>> ultimasVentas(@PathVariable Long id) {
        try {
            // Llama al servicio para obtener las últimas ventas desde cache o base de datos
            List<Venta> ultimasVentas = ventaService.obtenerUltimasVentasCliente(id);
            // Convierte las Ventas a VentaDTO
            List<VentaDTO> ultimasVentasDTO = ultimasVentas.stream()
                    .map(ventaMapper::ventaToVentaDTO) // Mapea cada Venta a VentaDTO
                    .collect(Collectors.toList()); // Colecta los resultados en una lista

            return ResponseEntity.ok(ultimasVentasDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

}

