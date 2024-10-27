package ar.unrn.tp.web;

import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.dto.CategoriaDTO;
import ar.unrn.tp.dto.ProductoDTO;
import ar.unrn.tp.exception.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping("/crear")
    public ResponseEntity<String> crearProducto(@RequestBody ProductoDTO productoDTO) {
        try {
            productoService.crearProducto(
                    productoDTO.getCodigo(),
                    productoDTO.getNombre(),
                    productoDTO.getMarca(),
                    productoDTO.getPrecio(),
                    productoDTO.getCategoriaId()
            );
            return ResponseEntity.ok("Producto creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<String> modificarProducto(
            @PathVariable("id") Long idProducto,
            @RequestBody ProductoDTO productoDTO) {
        try {
            productoService.modificarProducto(
                    idProducto,
                    productoDTO.getNombre(),
                    productoDTO.getPrecio(),
                    productoDTO.getCategoriaId(),
                    productoDTO.getVersion()
            );
            return ResponseEntity.ok("Producto modificado exitosamente");
        } catch (OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/listar")
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    @DeleteMapping
    public ResponseEntity<String> borrarProducto(@RequestParam Long id) {
        try {
            productoService.borrarProducto(id);
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: El producto con ID " + id + " no existe.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el producto.");
        }
    }


    @PostMapping("/crear-categoria")
    public ResponseEntity <String> crearCategoria(@RequestBody CategoriaDTO categoriaDTO){
        productoService.crearCategoria(categoriaDTO.getNombre());
        return ResponseEntity.ok("Categoria creada exitosamente");
    }

    @GetMapping("/listar-categorias")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias(){
        List <CategoriaDTO> categorias = productoService.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }
}


