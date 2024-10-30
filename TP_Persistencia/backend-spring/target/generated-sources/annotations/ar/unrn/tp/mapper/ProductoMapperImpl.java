package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.ProductoDTO;
import ar.unrn.tp.modelo.Categoria;
import ar.unrn.tp.modelo.Producto;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-30T10:03:42-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public Producto productoDTOToProducto(ProductoDTO productoDTO) {
        if ( productoDTO == null ) {
            return null;
        }

        Producto producto = new Producto();

        producto.setCategoria( productoDTOToCategoria( productoDTO ) );
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

    @Override
    public ProductoDTO productoToProductoDTO(Producto producto) {
        if ( producto == null ) {
            return null;
        }

        ProductoDTO productoDTO = new ProductoDTO();

        productoDTO.setCategoriaId( productoCategoriaId( producto ) );
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

    protected Categoria productoDTOToCategoria(ProductoDTO productoDTO) {
        if ( productoDTO == null ) {
            return null;
        }

        Categoria categoria = new Categoria();

        categoria.setId( productoDTO.getCategoriaId() );

        return categoria;
    }

    private Long productoCategoriaId(Producto producto) {
        if ( producto == null ) {
            return null;
        }
        Categoria categoria = producto.getCategoria();
        if ( categoria == null ) {
            return null;
        }
        Long id = categoria.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
