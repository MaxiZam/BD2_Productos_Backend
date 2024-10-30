package ar.unrn.tp.backend;

import ar.unrn.tp.api.ProductoService;
import ar.unrn.tp.dto.ProductoDTO;
import ar.unrn.tp.exception.OptimisticLockException;
import ar.unrn.tp.modelo.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductoServiceConcurrencyTest {

    @Autowired
    private ProductoService productoService;

    private Long productoId;

    @BeforeEach
    public void setUp() {
        // Crear un producto de prueba inicial antes de cada prueba
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setNombre("Producto Test");
        productoDTO.setPrecio(100.0f);
        productoDTO.setMarca("Marca Test");
        productoDTO.setCategoriaId(1L);
        productoDTO.setCodigo("TEST123");

        Producto producto = productoService.obtenerProductoPorCodigo(productoDTO.getCodigo());
        if(producto == null){
            productoService.crearProducto(productoDTO.getCodigo(),productoDTO.getNombre(),productoDTO.getMarca(), productoDTO.getPrecio(), productoDTO.getCategoriaId());
            producto = productoService.obtenerProductoPorCodigo(productoDTO.getCodigo());
        }
        productoId = producto.getId();
    }

    @Test
    public void testConcurrentModification() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Tarea 1 - intenta modificar el producto con un precio de 200
        Runnable task1 = () -> {
            try {
                productoService.modificarProducto(productoId, "Producto Test", 200.0f, "Marca Test", 1L, 0L);
            } catch (OptimisticLockException e) {
                System.out.println("Tarea 1: Error de concurrencia - " + e.getMessage());
            }
        };

        // Tarea 2 - intenta modificar el mismo producto con un precio de 300
        Runnable task2 = () -> {
            try {
                productoService.modificarProducto(productoId, "Producto Test", 300.0f, "Marca Test", 1L, 0L);
            } catch (OptimisticLockException e) {
                System.out.println("Tarea 2: Error de concurrencia - " + e.getMessage());
            }
        };

        // Ejecutar ambas tareas de manera concurrente
        executor.submit(task1);
        executor.submit(task2);

        executor.shutdown();
        boolean tasksCompleted = executor.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(tasksCompleted, "Las tareas concurrentes no se completaron a tiempo.");

        // Verificar si al menos una tarea fue exitosa
        Producto productoFinal = productoService.obtenerProductoPorId(productoId);
        BigDecimal precioFinal = productoFinal.getPrecio();
        assertTrue(precioFinal.equals(BigDecimal.valueOf(200)) || precioFinal.equals(BigDecimal.valueOf(300)),
                "El precio final no coincide con ninguno de los valores modificados.");
    }
}

