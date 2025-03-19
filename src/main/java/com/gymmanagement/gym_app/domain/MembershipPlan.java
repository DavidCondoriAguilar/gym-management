package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.gymmanagement.gym_app.domain.enums.MembershipType;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipPlan {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    private Integer duracionMeses;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MembershipType tipo;

    // Relación: Un plan puede estar asignado a muchos clientes
    @OneToMany(mappedBy = "membershipPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GymMember> gymMembers;
}
