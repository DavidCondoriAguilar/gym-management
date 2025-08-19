package com.gymmanagement.gym_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPaymentStatusDTO {
    private UUID memberId;
    private String memberName;
    private BigDecimal totalPaid;
    private BigDecimal totalWithDiscount;
    private BigDecimal remainingBalance;
    private boolean isFullyPaid;
    private UUID activePromotionId;
    private String activePromotionName;
    private BigDecimal discountPercentage;
}
