package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.inventory.DetalleVenta;
import com.gymmanagement.gym_app.domain.inventory.Venta;
import com.gymmanagement.gym_app.dto.inventory.DetalleVentaDTO;
import com.gymmanagement.gym_app.dto.inventory.VentaDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Venta entities and DTOs using builder pattern.
 */
@Component
public class CustomModelMapper {

    /**
     * Converts a Venta entity to VentaDTO using direct property setting.
     */
    public VentaDTO toVentaDTO(Venta venta) {
        if (venta == null) {
            return null;
        }

        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setCodigo(venta.getCodigo());
        
        if (venta.getCliente() != null) {
            dto.setClienteId(convertUuidToLong(venta.getCliente().getId()));
            dto.setClienteNombre(venta.getCliente().getUsername());
        }
        
        if (venta.getUsuario() != null) {
            dto.setUsuarioId(convertUuidToLong(venta.getUsuario().getId()));
            dto.setUsuarioNombre(venta.getUsuario().getUsername());
        }
        
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setSubtotal(venta.getSubtotal());
        dto.setIgv(venta.getIgv());
        dto.setDescuento(venta.getDescuento());
        dto.setTotal(venta.getTotal());
        dto.setTipoPago(venta.getTipoPago());
        dto.setEstado(venta.getEstado());
        
        if (venta.getDetalles() != null) {
            dto.setDetalles(
                venta.getDetalles().stream()
                    .map(this::toDetalleVentaDTO)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }

    /**
     * Converts a VentaDTO to Venta entity.
     */
    public Venta toVenta(VentaDTO ventaDTO) {
        if (ventaDTO == null) {
            return null;
        }

        Venta venta = new Venta();
        venta.setId(ventaDTO.getId());
        venta.setCodigo(ventaDTO.getCodigo());
        // Note: cliente y usuario se deben establecer desde el servicio
        venta.setFechaVenta(ventaDTO.getFechaVenta());
        venta.setSubtotal(ventaDTO.getSubtotal());
        venta.setIgv(ventaDTO.getIgv());
        venta.setDescuento(ventaDTO.getDescuento());
        venta.setTotal(ventaDTO.getTotal());
        venta.setTipoPago(ventaDTO.getTipoPago());
        venta.setEstado(ventaDTO.getEstado());
        // Los detalles se deben establecer desde el servicio
        
        return venta;
    }
    
    /**
     * Converts a UUID to a Long by taking the most significant bits.
     * This is a simple conversion that ensures we get a consistent Long value from a UUID.
     * Note: This is a one-way conversion and cannot be reversed to the original UUID.
     */
    private Long convertUuidToLong(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        // Take the most significant bits and ensure it's positive
        return Math.abs(uuid.getMostSignificantBits());
    }

    /**
     * Converts a DetalleVenta entity to DetalleVentaDTO using direct property setting.
     */
    public DetalleVentaDTO toDetalleVentaDTO(DetalleVenta detalle) {
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
     * Converts a DetalleVentaDTO to DetalleVenta entity.
     */
    public DetalleVenta toDetalleVenta(DetalleVentaDTO detalleDTO) {
        if (detalleDTO == null) {
            return null;
        }
        
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(detalleDTO.getId());
        // Note: producto se debe establecer desde el servicio
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
        detalle.setDescuento(detalleDTO.getDescuento());
        detalle.setSubtotal(detalleDTO.getSubtotal());
        
        return detalle;
    }

    /**
     * Mapea una lista de Venta a una lista de VentaDTO
     */
    public List<VentaDTO> toVentaDTOList(List<Venta> ventas) {
        if (ventas == null) {
            return null;
        }
        return ventas.stream()
                .map(this::toVentaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapea una lista de DetalleVenta a una lista de DetalleVentaDTO
     */
    public List<DetalleVentaDTO> toDetalleVentaDTOList(List<DetalleVenta> detalles) {
        if (detalles == null) {
            return null;
        }
        return detalles.stream()
                .map(this::toDetalleVentaDTO)
                .collect(Collectors.toList());
    }
}
