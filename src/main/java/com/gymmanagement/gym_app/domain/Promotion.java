package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Promotion {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Relación inversa: Las promociones aplicadas a los GymMember
    @ManyToMany(mappedBy = "promociones")
    private List<GymMember> gymMembers;
}
