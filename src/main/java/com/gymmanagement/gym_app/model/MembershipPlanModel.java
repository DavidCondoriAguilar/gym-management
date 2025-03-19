package com.gymmanagement.gym_app.model;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipPlanModel {

    private UUID id;
    private String nombre;
    private Integer duracionMeses;
    private BigDecimal costo;
    private String descripcion;
}
