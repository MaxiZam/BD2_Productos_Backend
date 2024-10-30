package ar.unrn.tp.mapper;

import org.mapstruct.Mapper;
import ar.unrn.tp.modelo.Venta;
import ar.unrn.tp.dto.VentaDTO;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    // Mapea de VentaDTO a Venta
    Venta ventaDTOToVenta(VentaDTO ventaDTO);

    // Mapea de Venta a VentaDTO
    VentaDTO ventaToVentaDTO(Venta venta);
}




