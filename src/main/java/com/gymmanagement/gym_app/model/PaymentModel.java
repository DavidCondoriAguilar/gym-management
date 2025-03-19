package com.gymmanagement.gym_app.model;

import com.gymmanagement.gym_app.domain.enums.PaymentMethod;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentModel {

    private UUID id;
    private UUID gymMemberId;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private PaymentMethod metodoPago;
    private PaymentStatus estado;
}
