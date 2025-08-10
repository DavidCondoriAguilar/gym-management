package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.inventory.DetalleVenta;
import com.gymmanagement.gym_app.domain.inventory.Venta;
import com.gymmanagement.gym_app.dto.inventory.DetalleVentaDTO;
import com.gymmanagement.gym_app.dto.inventory.VentaDTO;
// Using fully qualified name org.modelmapper.ModelMapper to avoid conflict with class name
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

public class ModelMapper {
    private final org.modelmapper.ModelMapper modelMapper;

    public ModelMapper() {
        this.modelMapper = new org.modelmapper.ModelMapper();
        configureMappings();
    }

    @PostConstruct
    public void configureMappings() {
        // Configuración para mapear Venta a VentaDTO
        modelMapper.typeMap(Venta.class, VentaDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getCliente() != null ? src.getCliente().getId() : null, VentaDTO::setClienteId);
            mapper.map(src -> src.getUsuario() != null ? src.getUsuario().getId() : null, VentaDTO::setUsuarioId);
            mapper.map(src -> src.getUsuario() != null ? src.getUsuario().getUsername() : null, 
                    VentaDTO::setUsuarioNombre);
            
            // Mapear detalles de venta
            mapper.<List<DetalleVenta>>map(src -> src.getDetalles(), (dest, v) -> dest.setDetalles(
                v.stream()
                    .map(this::convertToDetalleVentaDTO)
                    .collect(Collectors.toList())
            ));
        });

        // Configuración para mapear VentaDTO a Venta
        modelMapper.typeMap(VentaDTO.class, Venta.class).addMappings(mapper -> {
            mapper.skip(Venta::setId);
            mapper.skip(Venta::setDetalles);
        });
    }

    private DetalleVentaDTO convertToDetalleVentaDTO(DetalleVenta detalle) {
        if (detalle == null) {
            return null;
        }
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setId(detalle.getId());
        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId());
            dto.setProductoNombre(detalle.getProducto().getNombre());
            dto.setProductoCodigo(detalle.getProducto().getCodigo());
        }
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuento(detalle.getDescuento());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    /**
     * Método genérico para mapear un objeto de origen a un objeto de destino
     */
    public <D> D map(Object source, Class<D> destinationType) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationType);
    }

    /**
     * Método para mapear una lista de objetos
     */
    public <D, T> List<D> mapList(List<T> sourceList, Class<D> destinationType) {
        if (sourceList == null) {
            return null;
        }
        return sourceList.stream()
                .map(source -> map(source, destinationType))
                .collect(Collectors.toList());
    }
}
