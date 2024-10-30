package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.ClienteDTO;
import ar.unrn.tp.modelo.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", uses = TarjetaCreditoMapper.class)
public interface ClienteMapper {

    // Mapeo de cliente a DTO
    @Mapping(target = "tarjetas", source = "tarjetas")
    ClienteDTO clienteToClienteDTO(Cliente cliente);

    // Mapeo de DTO a cliente
    @Mapping(target = "tarjetas", source = "tarjetas")
    Cliente clienteDTOToCliente(ClienteDTO clienteDTO);
}


