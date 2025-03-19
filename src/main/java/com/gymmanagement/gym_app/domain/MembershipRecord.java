package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MembershipRecord {

    @Id
    @GeneratedValue
    private UUID id;

    // Relación: Cada registro pertenece a un GymMember
    @ManyToOne
    @JoinColumn(name = "gym_member_id")
    private GymMember gymMember;

    @ManyToOne
    @JoinColumn(name = "membership_plan_id")
    private MembershipPlan membershipPlan;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private String estado;
}
