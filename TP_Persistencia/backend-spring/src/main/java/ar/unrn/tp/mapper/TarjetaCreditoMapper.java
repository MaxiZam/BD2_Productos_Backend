package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.TarjetaCreditoDTO;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.TarjetaCredito;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TarjetaCreditoMapper {

    // Mapeo de tarjeta de crédito a DTO
    @Mapping(target = "clienteId", source = "cliente.id")
    TarjetaCreditoDTO tarjetaCreditoToTarjetaCreditoDTO(TarjetaCredito tarjetaCredito);

    // Mapeo de DTO a tarjeta de crédito
    @Mapping(target = "cliente", source = "clienteId")
    TarjetaCredito tarjetaCreditoDTOToTarjetaCredito(TarjetaCreditoDTO tarjetaCreditoDTO);

    // Método para mapear Long a Cliente
    default Cliente map(Long clienteId) {
        if (clienteId == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        return cliente;
    }
}



