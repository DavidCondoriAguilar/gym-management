package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentSummaryDTO {
    private UUID id;
    private BigDecimal amount;
    private LocalDate paymentDate;
}
