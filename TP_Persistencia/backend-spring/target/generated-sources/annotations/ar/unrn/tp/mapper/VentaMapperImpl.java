package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.ProductoDTO;
import ar.unrn.tp.dto.VentaDTO;
import ar.unrn.tp.modelo.Producto;
import ar.unrn.tp.modelo.Venta;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-30T20:04:40-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class VentaMapperImpl implements VentaMapper {

    @Override
    public Venta ventaDTOToVenta(VentaDTO ventaDTO) {
        if ( ventaDTO == null ) {
            return null;
        }

        Venta venta = new Venta();

        venta.setId( ventaDTO.getId() );
        venta.setFecha( ventaDTO.getFecha() );
        venta.setProductos( productoDTOListToProductoList( ventaDTO.getProductos() ) );
        venta.setMontoTotal( ventaDTO.getMontoTotal() );
        venta.setNumeroVenta( ventaDTO.getNumeroVenta() );

        return venta;
    }

    @Override
    public VentaDTO ventaToVentaDTO(Venta venta) {
        if ( venta == null ) {
            return null;
        }

        VentaDTO ventaDTO = new VentaDTO();

        ventaDTO.setId( venta.getId() );
        ventaDTO.setFecha( venta.getFecha() );
        ventaDTO.setProductos( productoListToProductoDTOList( venta.getProductos() ) );
        ventaDTO.setMontoTotal( venta.getMontoTotal() );
        ventaDTO.setNumeroVenta( venta.getNumeroVenta() );

        return ventaDTO;
    }

    protected Producto productoDTOToProducto(ProductoDTO productoDTO) {
        if ( productoDTO == null ) {
            return null;
        }

        Producto producto = new Producto();

        producto.setId( productoDTO.getId() );
        producto.setCodigo( productoDTO.getCodigo() );
        producto.setNombre( productoDTO.getNombre() );
        producto.setMarca( productoDTO.getMarca() );
        if ( productoDTO.getPrecio() != null ) {
            producto.setPrecio( BigDecimal.valueOf( productoDTO.getPrecio() ) );
        }
        producto.setVersion( productoDTO.getVersion() );

        return producto;
    }

    protected List<Producto> productoDTOListToProductoList(List<ProductoDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<Producto> list1 = new ArrayList<Producto>( list.size() );
        for ( ProductoDTO productoDTO : list ) {
            list1.add( productoDTOToProducto( productoDTO ) );
        }

        return list1;
    }

    protected ProductoDTO productoToProductoDTO(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoDTO productoDTO = new ProductoDTO();

        productoDTO.setId( producto.getId() );
        productoDTO.setCodigo( producto.getCodigo() );
        if ( producto.getPrecio() != null ) {
            productoDTO.setPrecio( producto.getPrecio().floatValue() );
        }
        productoDTO.setNombre( producto.getNombre() );
        productoDTO.setMarca( producto.getMarca() );
        productoDTO.setVersion( producto.getVersion() );

        return productoDTO;
    }

    protected List<ProductoDTO> productoListToProductoDTOList(List<Producto> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductoDTO> list1 = new ArrayList<ProductoDTO>( list.size() );
        for ( Producto producto : list ) {
            list1.add( productoToProductoDTO( producto ) );
        }

        return list1;
    }
}
