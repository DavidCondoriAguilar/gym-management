package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.inventory.*;
import java.util.UUID;
import com.gymmanagement.gym_app.dto.inventory.*;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", 
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Component
public interface InventoryMapper {

    // Mapeo de Producto
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "proveedorId", source = "proveedor.id")
    @Mapping(target = "proveedorNombre", source = "proveedor.razonSocial")
    ProductoDTO toProductoDTO(Producto producto);
    
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "proveedor", ignore = true)
    Producto toProducto(ProductoDTO productoDTO);
    
    List<ProductoDTO> toProductoDTOList(List<Producto> productos);

    // Mapeo de Categoria
    CategoriaDTO toCategoriaDTO(Categoria categoria);
    Categoria toCategoria(CategoriaDTO categoriaDTO);
    List<CategoriaDTO> toCategoriaDTOList(List<Categoria> categorias);

    // Mapeo de Proveedor
    ProveedorDTO toProveedorDTO(Proveedor proveedor);
    Proveedor toProveedor(ProveedorDTO proveedorDTO);
    List<ProveedorDTO> toProveedorDTOList(List<Proveedor> proveedores);

    // Mapeo de Venta
    @Mapping(target = "clienteId", expression = "java(venta.getCliente() != null ? Long.parseLong(venta.getCliente().getId().toString().replaceAll(\"[^0-9]\", \"\").substring(0, 15)) : null)")
    @Mapping(target = "usuarioId", expression = "java(Long.parseLong(venta.getUsuario().getId().toString().replaceAll(\"[^0-9]\", \"\").substring(0, 15)))")
    @Mapping(target = "usuarioNombre", source = "usuario.username")
    @Mapping(target = "detalles", source = "detalles")
    VentaDTO toVentaDTO(Venta venta);
    
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Venta toVenta(VentaDTO ventaDTO);
    
    List<VentaDTO> toVentaDTOList(List<Venta> ventas);

    // Mapeo de DetalleVenta
    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    @Mapping(target = "productoCodigo", source = "producto.codigo")
    DetalleVentaDTO toDetalleVentaDTO(DetalleVenta detalleVenta);
    
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "venta", ignore = true)
    DetalleVenta toDetalleVenta(DetalleVentaDTO detalleVentaDTO);
    
    List<DetalleVentaDTO> toDetalleVentaDTOList(List<DetalleVenta> detalles);

    // Mapeo de Inventario (Movimientos)
    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    @Mapping(target = "usuarioRegistroId", expression = "java(mapUuidToLong(inventario.getUsuarioRegistro() != null ? inventario.getUsuarioRegistro().getId() : null))")
    @Mapping(target = "usuarioRegistroNombre", source = "usuarioRegistro.username")
    @Mapping(target = "ventaId", source = "venta.id")
    InventarioDTO toInventarioDTO(Inventario inventario);
    
    default Long mapUuidToLong(UUID uuid) {
        if (uuid == null) return null;
        String numeric = uuid.toString().replaceAll("[^0-9]", "");
        return numeric.isEmpty() ? 0L : Long.parseLong(numeric.substring(0, Math.min(numeric.length(), 15)));
    }
    
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "usuarioRegistro", ignore = true)
    @Mapping(target = "venta", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaMovimiento", ignore = true)
    Inventario toInventario(InventarioDTO inventarioDTO);
    
    List<InventarioDTO> toInventarioDTOList(List<Inventario> movimientos);
    
    // Métodos de actualización
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductoFromDTO(ProductoDTO dto, @MappingTarget Producto entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoriaFromDTO(CategoriaDTO dto, @MappingTarget Categoria entity);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProveedorFromDTO(ProveedorDTO dto, @MappingTarget Proveedor entity);
}
