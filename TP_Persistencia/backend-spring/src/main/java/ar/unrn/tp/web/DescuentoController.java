package ar.unrn.tp.web;

import ar.unrn.tp.api.DescuentoService;
import ar.unrn.tp.dto.DescuentoCompraDTO;
import ar.unrn.tp.dto.DescuentoProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/descuentos")
public class DescuentoController {

    @Autowired
    private DescuentoService descuentoService;

    @PostMapping("/crear-descuento-compra")
    public ResponseEntity<String> crearDescuentoSobreTotal(@RequestBody DescuentoCompraDTO descuentoCompraDTO) {
        try {
            descuentoService.crearDescuentoSobreTotal(
                    descuentoCompraDTO.getMarcaTarjeta(),
                    descuentoCompraDTO.getFechaInicio(),
                    descuentoCompraDTO.getFechaFin(),
                    descuentoCompraDTO.getPorcentajeDescuento()
            );
            return ResponseEntity.ok("Descuento sobre total creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/crear-descuento-producto")
    public ResponseEntity<String> crearDescuentoSobreProducto(@RequestBody DescuentoProductoDTO descuentoProductoDTO) {
        try {
            descuentoService.crearDescuentoSobreProducto(
                    descuentoProductoDTO.getMarcaProducto(),
                    descuentoProductoDTO.getFechaInicio(),
                    descuentoProductoDTO.getFechaFin(),
                    descuentoProductoDTO.getPorcentajeDescuento()
            );
            return ResponseEntity.ok("Descuento sobre producto creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


