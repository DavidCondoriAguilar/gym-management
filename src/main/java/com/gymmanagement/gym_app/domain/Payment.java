package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.gymmanagement.gym_app.domain.enums.PaymentMethod;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    // Relación: Cada pago pertenece a un único GymMember
    @ManyToOne
    @JoinColumn(name = "gym_member_id", nullable = false)
    private GymMember gymMember;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod metodoPago;

    @Column(nullable = false, length = 20)
    private String estado;
}
