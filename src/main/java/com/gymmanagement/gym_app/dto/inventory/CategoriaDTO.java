// CategoriaDTO.java
package com.gymmanagement.gym_app.dto.inventory;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}