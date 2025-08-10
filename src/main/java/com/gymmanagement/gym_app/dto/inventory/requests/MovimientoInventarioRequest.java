package com.gymmanagement.gym_app.dto.inventory.requests;

import com.gymmanagement.gym_app.domain.enums.TipoMovimientoInventario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class MovimientoInventarioRequest {
    @NotNull(message = "El ID del producto es requerido")
    private Long productoId;
    
    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
    
    @NotNull(message = "El tipo de movimiento es requerido")
    private TipoMovimientoInventario tipoMovimiento;
    
    private String descripcion;
    
    @NotNull(message = "El ID del usuario es requerido")
    private Long usuarioId;
    
    private Long ventaId;
}
