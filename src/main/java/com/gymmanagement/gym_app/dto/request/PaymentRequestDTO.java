package com.gymmanagement.gym_app.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.gymmanagement.gym_app.domain.enums.PaymentMethod;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;

@Data
public class PaymentRequestDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate paymentDate;
    @NotNull
    private PaymentMethod method;
    @NotNull
    private PaymentStatus status;
    @NotNull
    private UUID memberId;
    private UUID promotionId;
}
