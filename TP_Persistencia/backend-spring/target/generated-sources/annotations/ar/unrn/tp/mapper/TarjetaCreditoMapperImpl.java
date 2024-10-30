package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.TarjetaCreditoDTO;
import ar.unrn.tp.modelo.Cliente;
import ar.unrn.tp.modelo.TarjetaCredito;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-30T15:58:27-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class TarjetaCreditoMapperImpl implements TarjetaCreditoMapper {

    @Override
    public TarjetaCreditoDTO tarjetaCreditoToTarjetaCreditoDTO(TarjetaCredito tarjetaCredito) {
        if ( tarjetaCredito == null ) {
            return null;
        }

        Long clienteId = null;
        Long id = null;
        String numero = null;
        String marca = null;
        LocalDate fechaVencimiento = null;

        clienteId = tarjetaCreditoClienteId( tarjetaCredito );
        id = tarjetaCredito.getId();
        numero = tarjetaCredito.getNumero();
        marca = tarjetaCredito.getMarca();
        fechaVencimiento = tarjetaCredito.getFechaVencimiento();

        TarjetaCreditoDTO tarjetaCreditoDTO = new TarjetaCreditoDTO( id, numero, marca, fechaVencimiento, clienteId );

        return tarjetaCreditoDTO;
    }

    @Override
    public TarjetaCredito tarjetaCreditoDTOToTarjetaCredito(TarjetaCreditoDTO tarjetaCreditoDTO) {
        if ( tarjetaCreditoDTO == null ) {
            return null;
        }

        TarjetaCredito tarjetaCredito = new TarjetaCredito();

        tarjetaCredito.setCliente( map( tarjetaCreditoDTO.getClienteId() ) );
        tarjetaCredito.setId( tarjetaCreditoDTO.getId() );
        tarjetaCredito.setNumero( tarjetaCreditoDTO.getNumero() );
        tarjetaCredito.setMarca( tarjetaCreditoDTO.getMarca() );
        tarjetaCredito.setFechaVencimiento( tarjetaCreditoDTO.getFechaVencimiento() );

        return tarjetaCredito;
    }

    private Long tarjetaCreditoClienteId(TarjetaCredito tarjetaCredito) {
        if ( tarjetaCredito == null ) {
            return null;
        }
        Cliente cliente = tarjetaCredito.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
