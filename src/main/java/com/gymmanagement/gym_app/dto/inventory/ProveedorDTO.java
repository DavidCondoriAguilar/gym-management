package com.gymmanagement.gym_app.dto.inventory;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProveedorDTO {
    private Long id;
    private String ruc;
    private String razonSocial;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}