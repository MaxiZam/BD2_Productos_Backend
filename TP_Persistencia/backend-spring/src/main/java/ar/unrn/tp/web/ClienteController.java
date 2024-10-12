package ar.unrn.tp.web;

import ar.unrn.tp.api.ClienteService;
import ar.unrn.tp.dto.ClienteDTO;
import ar.unrn.tp.dto.TarjetaCreditoDTO;
import ar.unrn.tp.mapper.ClienteMapper;
import ar.unrn.tp.modelo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteMapper clienteMapper;

    @PostMapping("/crear")
    public ResponseEntity<String> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            clienteService.crearCliente(clienteDTO.getNombre(), clienteDTO.getApellido(), clienteDTO.getDni(), clienteDTO.getEmail());
            return ResponseEntity.ok("Cliente creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<String> modificarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        try {
            clienteService.modificarCliente(id, clienteDTO.getNombre(), clienteDTO.getApellido(), clienteDTO.getDni(), clienteDTO.getEmail());
            return ResponseEntity.ok("Cliente modificado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/agregar-tarjeta/{idCliente}")
    public ResponseEntity<String> agregarTarjeta(
            @PathVariable Long idCliente,
            @RequestBody TarjetaCreditoDTO tarjetaCreditoDTO) {
        try {
            // Llama al servicio pasando el número y la marca desde el DTO
            clienteService.agregarTarjeta(idCliente, tarjetaCreditoDTO.getNumero(), tarjetaCreditoDTO.getMarca());
            return ResponseEntity.ok("Tarjeta agregada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody ClienteDTO clienteDTO) {
        String nombre = clienteDTO.getNombre();
        String contrasena = clienteDTO.getDni();

        Cliente cliente = clienteService.buscarClientePorNombreYDNI(nombre, contrasena);

        if (cliente != null) {
            // Si se encuentra el cliente, puedes retornar un token o información del cliente
            return ResponseEntity.ok(cliente); // Retorna el cliente encontrado
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodosLosClientes() {
        try {
            List<Cliente> clientes = clienteService.obtenerClientes(); // Llama al servicio para obtener la lista de clientes
            return ResponseEntity.ok(clientes.stream()
                    .map(clienteMapper::clienteToClienteDTO) // Mapea a ClienteDTO
                    .collect(Collectors.toList())); // Retorna la lista de clientes en formato JSON
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Retorna un error 500 si algo sale mal
        }
    }

}

