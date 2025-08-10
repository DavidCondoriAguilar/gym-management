package com.gymmanagement.gym_app.dto.inventory.requests;

import com.gymmanagement.gym_app.domain.enums.TipoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class VentaRequest {
    private UUID clienteId;
    
    @NotNull(message = "El usuario es requerido")
    private UUID usuarioId;
    
    @Valid
    @NotNull(message = "Los detalles de la venta son requeridos")
    private List<DetalleVentaRequest> detalles;
    
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @NotNull(message = "El tipo de pago es requerido")
    private TipoPago tipoPago;
    
    private String observaciones;
}
