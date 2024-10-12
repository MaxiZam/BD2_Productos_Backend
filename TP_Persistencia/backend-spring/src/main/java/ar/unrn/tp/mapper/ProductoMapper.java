package ar.unrn.tp.mapper;

import ar.unrn.tp.dto.ProductoDTO;
import ar.unrn.tp.modelo.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    ProductoMapper INSTANCE = Mappers.getMapper(ProductoMapper.class);

    @Mapping(target = "categoria.id", source = "categoriaId") // Asumiendo que Producto tiene una referencia a Categoria
    Producto productoDTOToProducto(ProductoDTO productoDTO);

    @Mapping(target = "categoriaId", source = "categoria.id") // Asumiendo que ProductoDTO tiene una referencia a CategoriaDTO
    ProductoDTO productoToProductoDTO(Producto producto);
}

