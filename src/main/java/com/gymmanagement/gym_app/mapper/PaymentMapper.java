package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import com.gymmanagement.gym_app.dto.response.PaymentSummaryDTO;
import com.gymmanagement.gym_app.model.PaymentModel;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "gymMemberId", source = "gymMember.id")
    @Mapping(target = "promotionId", source = "promotion.id")
    PaymentModel toModel(Payment payment);

    @Mapping(target = "gymMember", ignore = true)
    @Mapping(target = "promotion", ignore = true)
    Payment toEntity(PaymentModel paymentModel);

    @Mapping(target = "method", source = "paymentMethod")
    @Mapping(target = "memberId", source = "gymMember.id")
    @Mapping(target = "promotionId", source = "promotion.id")
    @Mapping(target = "amount", expression = "java(entity.getPromotion() != null && entity.getDiscountedAmount() != null ? entity.getDiscountedAmount() : entity.getAmount())")
    PaymentResponseDTO toResponseDTO(Payment entity);
    
    /**
     * Maps a Payment entity to a PaymentSummaryDTO.
     * If the payment has a promotion and a discounted amount, it will use the discounted amount.
     * Otherwise, it will use the regular amount.
     *
     * @param payment The payment entity to map
     * @return A PaymentSummaryDTO with the appropriate amount
     */
    default PaymentSummaryDTO toPaymentSummaryDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentSummaryDTO dto = new PaymentSummaryDTO();
        dto.setId(payment.getId());
        dto.setPaymentDate(payment.getPaymentDate());
        
        // Use discounted amount if available and payment has a promotion
        if (payment.getPromotion() != null && payment.getDiscountedAmount() != null) {
            dto.setAmount(payment.getDiscountedAmount());
        } else {
            dto.setAmount(payment.getAmount());
        }
        
        return dto;
    }

    default Payment fromRequestDTO(PaymentRequestDTO dto, 
                                 @Context GymMemberRepository gymMemberRepository,
                                 @Context PromotionRepository promotionRepository) {
        if (dto == null) {
            return null;
        }
        
        Payment.PaymentBuilder payment = Payment.builder()
            .paymentMethod(dto.getMethod())
            .amount(dto.getAmount())
            .paymentDate(dto.getPaymentDate())
            .status(dto.getStatus());
            
        // Set gymMember if memberId is provided
        if (dto.getMemberId() != null) {
            GymMember member = gymMemberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("GymMember not found"));
            payment.gymMember(member);
        }
        
        // Set promotion if promotionId is provided
        if (dto.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(dto.getPromotionId()).orElse(null);
            payment.promotion(promotion);
        }
        
        return payment.build();
    }
}
