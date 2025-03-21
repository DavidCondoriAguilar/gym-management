package com.gymmanagement.gym_app.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.gymmanagement.gym_app.domain.enums.PaymentMethod;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    private BigDecimal discountedAmount;

    public void applyDiscount(BigDecimal discountPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = amount.multiply(discountPercentage.divide(BigDecimal.valueOf(100)));
            this.discountedAmount = amount.subtract(discount);
        } else {
            this.discountedAmount = amount;
        }
    }
}
