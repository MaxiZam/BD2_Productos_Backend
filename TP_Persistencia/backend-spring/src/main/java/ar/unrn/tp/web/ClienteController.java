package ar.unrn.tp.web;

import ar.unrn.tp.api.ClienteService;
import ar.unrn.tp.dto.ClienteDTO;
import ar.unrn.tp.dto.LoginRequestDTO;
import ar.unrn.tp.dto.TarjetaCreditoDTO;
import ar.unrn.tp.mapper.ClienteMapper;
import ar.unrn.tp.mapper.TarjetaCreditoMapper;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.TarjetaCredito;
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

    @Autowired
    private TarjetaCreditoMapper tarjetaCreditoMapper;

    @PostMapping
    public ResponseEntity<String> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            clienteService.crearCliente(clienteDTO.getNombre(), clienteDTO.getApellido(), clienteDTO.getDni(), clienteDTO.getEmail());
            return ResponseEntity.ok("Cliente creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequestDTO loginRequest) {
        String email = loginRequest.getEmail(); // Obtén el email del objeto LoginRequest
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Retorna bad request si el email es nulo o vacío
        }

        Cliente cliente = clienteService.buscarClientePorEmail(email);
        ClienteDTO clienteDTO = clienteMapper.clienteToClienteDTO(cliente); // Usar la inyección en lugar de la instancia estática

        if (clienteDTO != null) {
            return ResponseEntity.ok(clienteDTO); // Retorna el cliente encontrado si la autenticación es exitosa
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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

    @GetMapping("/listar-tarjetas/{clienteId}")
    public ResponseEntity<List<TarjetaCreditoDTO>> obtenerTarjetasCredito(@PathVariable Long clienteId) {
        List<TarjetaCredito> tarjetas = clienteService.listarTarjetas(clienteId);
        return ResponseEntity.ok(tarjetas.stream()
                .map(tarjetaCreditoMapper::tarjetaCreditoToTarjetaCreditoDTO)
                .collect(Collectors.toList()));
    }
}


