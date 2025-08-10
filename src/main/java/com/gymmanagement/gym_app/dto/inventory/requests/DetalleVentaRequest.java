package com.gymmanagement.gym_app.dto.inventory.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaRequest {
    @NotNull(message = "El ID del producto es requerido")
    private Long productoId;
    
    @NotNull(message = "La cantidad es requerida")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es requerido")
    private BigDecimal precioUnitario;
    
    private BigDecimal descuento = BigDecimal.ZERO;
}
