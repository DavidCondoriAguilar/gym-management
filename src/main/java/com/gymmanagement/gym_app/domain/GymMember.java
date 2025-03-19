package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymMember {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(length = 9)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false)
    private LocalDate membershipStart;

    @Column(nullable = false)
    private LocalDate membershipEnd;

    // Relación: Un miembro tiene un plan de membresía
    @ManyToOne
    @JoinColumn(name = "membership_plan_id", nullable = false)
    private MembershipPlan membershipPlan;

    @OneToMany(mappedBy = "gymMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> pagos;

    // Un GymMember puede tener un historial de membresías
    @OneToMany(mappedBy = "gymMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MembershipRecord> membershipRecords;

    // Relación: Un GymMember puede tener varias promociones activas
    @ManyToMany
    @JoinTable(
            name = "gymmember_promotion",
            joinColumns = @JoinColumn(name = "gymmember_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promociones;
}
