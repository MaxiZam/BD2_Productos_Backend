package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.ClienteDTO;
import ar.unrn.tp.dto.TarjetaCreditoDTO;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.TarjetaCredito;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-30T15:58:27-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Autowired
    private TarjetaCreditoMapper tarjetaCreditoMapper;

    @Override
    public ClienteDTO clienteToClienteDTO(Cliente cliente) {
        if ( cliente == null ) {
            return null;
        }

        List<TarjetaCreditoDTO> tarjetas = null;
        Long id = null;
        String nombre = null;
        String apellido = null;
        String dni = null;
        String email = null;

        tarjetas = tarjetaCreditoListToTarjetaCreditoDTOList( cliente.getTarjetas() );
        id = cliente.getId();
        nombre = cliente.getNombre();
        apellido = cliente.getApellido();
        dni = cliente.getDni();
        email = cliente.getEmail();

        ClienteDTO clienteDTO = new ClienteDTO( id, nombre, apellido, dni, email, tarjetas );

        return clienteDTO;
    }

    @Override
    public Cliente clienteDTOToCliente(ClienteDTO clienteDTO) {
        if ( clienteDTO == null ) {
            return null;
        }

        Cliente cliente = new Cliente();

        cliente.setTarjetas( tarjetaCreditoDTOListToTarjetaCreditoList( clienteDTO.getTarjetas() ) );
        cliente.setId( clienteDTO.getId() );
        cliente.setNombre( clienteDTO.getNombre() );
        cliente.setApellido( clienteDTO.getApellido() );
        cliente.setDni( clienteDTO.getDni() );
        cliente.setEmail( clienteDTO.getEmail() );

        return cliente;
    }

    protected List<TarjetaCreditoDTO> tarjetaCreditoListToTarjetaCreditoDTOList(List<TarjetaCredito> list) {
        if ( list == null ) {
            return null;
        }

        List<TarjetaCreditoDTO> list1 = new ArrayList<TarjetaCreditoDTO>( list.size() );
        for ( TarjetaCredito tarjetaCredito : list ) {
            list1.add( tarjetaCreditoMapper.tarjetaCreditoToTarjetaCreditoDTO( tarjetaCredito ) );
        }

        return list1;
    }

    protected List<TarjetaCredito> tarjetaCreditoDTOListToTarjetaCreditoList(List<TarjetaCreditoDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<TarjetaCredito> list1 = new ArrayList<TarjetaCredito>( list.size() );
        for ( TarjetaCreditoDTO tarjetaCreditoDTO : list ) {
            list1.add( tarjetaCreditoMapper.tarjetaCreditoDTOToTarjetaCredito( tarjetaCreditoDTO ) );
        }

        return list1;
    }
}
