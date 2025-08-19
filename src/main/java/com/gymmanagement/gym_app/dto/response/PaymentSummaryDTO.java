package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for displaying payment summary information.
 * When a promotion is applied, the amount field will show the discounted amount.
 */
@Data
public class PaymentSummaryDTO {
    private UUID id;
    private BigDecimal amount; // This will be the discounted amount if a promotion was applied
    private LocalDate paymentDate;
}
