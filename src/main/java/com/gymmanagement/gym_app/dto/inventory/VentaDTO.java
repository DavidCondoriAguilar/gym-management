package com.gymmanagement.gym_app.dto.inventory;

import com.gymmanagement.gym_app.domain.enums.EstadoVenta;
import com.gymmanagement.gym_app.domain.enums.TipoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaDTO {
    private Long id;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal descuento;
    private BigDecimal total;
    private EstadoVenta estado;
    private TipoPago tipoPago;
    private String observaciones;
    private List<DetalleVentaDTO> detalles;
}
