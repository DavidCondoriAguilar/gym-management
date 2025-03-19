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

    // Relationship: Each payment belongs to a single GymMember
    @ManyToOne
    @JoinColumn(name = "gym_member_id", nullable = false)
    private GymMember gymMember;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, length = 20)
    private String status;
}
