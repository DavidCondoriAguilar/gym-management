package com.gymmanagement.gym_app.dto.response;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentResponseDTO {
    private UUID id;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String method;
    private String status;
    private UUID memberId;
    private UUID promotionId;
    private BigDecimal discountedAmount;
}
