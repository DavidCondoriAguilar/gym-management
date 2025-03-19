package com.gymmanagement.gym_app.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PromotionModel {

    private UUID id;
    private String nombre;
    private BigDecimal descuentoPorcentaje;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
