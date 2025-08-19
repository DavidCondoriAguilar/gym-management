package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;
import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.MemberPaymentStatusDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import com.gymmanagement.gym_app.mapper.PaymentMapper;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.PaymentRepository;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import com.gymmanagement.gym_app.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.gymmanagement.gym_app.domain.MembershipRecord;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final GymMemberRepository gymMemberRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO) {
        // 1. Validate request DTO
        validatePaymentRequest(requestDTO);
        
        // 2. Check if member exists and get membership plan
        GymMember member = gymMemberRepository.findByIdWithMembershipPlan(requestDTO.getMemberId())
            .orElseThrow(() -> new EntityNotFoundException("Miembro no encontrado con ID: " + requestDTO.getMemberId()));
            
        if (member.getMembershipPlan() == null) {
            throw new IllegalStateException("El miembro no tiene un plan de membresía asignado");
        }
        
        // 3. Get and validate promotion if provided
        Promotion promotion = null;
        if (requestDTO.getPromotionId() != null) {
            promotion = promotionRepository.findById(requestDTO.getPromotionId())
                .orElseThrow(() -> new EntityNotFoundException("Promoción no encontrada con ID: " + requestDTO.getPromotionId()));
            
            // Check if promotion is active
            LocalDate today = LocalDate.now();
            if ((promotion.getStartDate() != null && today.isBefore(promotion.getStartDate())) ||
                (promotion.getEndDate() != null && today.isAfter(promotion.getEndDate()))) {
                throw new IllegalArgumentException("La promoción no está vigente");
            }
            
            // Check if promotion has reached max uses
            if (promotion.getMaxUses() != null && promotion.getUsageCount() != null && 
                promotion.getUsageCount() >= promotion.getMaxUses()) {
                throw new IllegalArgumentException("La promoción ha alcanzado su límite de usos");
            }
        }
        
        // 4. Calculate expected payment amount
        BigDecimal expectedAmount = member.getMembershipPlan().getCost();
        if (promotion != null && promotion.getDiscountPercentage() != null) {
            // Apply discount if promotion exists and has a discount percentage
            BigDecimal discount = expectedAmount.multiply(
                promotion.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
            expectedAmount = expectedAmount.subtract(discount);
        }
        
        // 5. Validate payment amount matches exactly
        if (requestDTO.getAmount().compareTo(expectedAmount) != 0) {
            String expectedAmountStr = String.format("%.2f", expectedAmount).replace(".", ",");
            String actualAmountStr = String.format("%.2f", requestDTO.getAmount()).replace(".", ",");
            
            if (promotion != null) {
                BigDecimal originalAmount = member.getMembershipPlan().getCost();
                String originalAmountStr = String.format("%.2f", originalAmount).replace(".", ",");
                throw new IllegalArgumentException(String.format(
                    "El monto del pago (%s) no coincide con el monto requerido (%s) para la promoción aplicada (precio original: %s, descuento: %.0f%%)",
                    actualAmountStr, expectedAmountStr, originalAmountStr, promotion.getDiscountPercentage()
                ));
            } else {
                throw new IllegalArgumentException(String.format(
                    "El monto del pago (%s) no coincide con el monto requerido (%s) para el plan de membresía",
                    actualAmountStr, expectedAmountStr
                ));
            }
        }
        
        // 6. Check for duplicate payment (same member, same amount, same day)
        if (paymentRepository.existsByGymMemberIdAndAmountAndPaymentDate(
                requestDTO.getMemberId(), 
                requestDTO.getAmount(), 
                requestDTO.getPaymentDate())) {
            throw new IllegalArgumentException("Ya existe un pago idéntico para este miembro en la misma fecha");
        }
        
        // 5. Create payment with the correct amounts
        Payment payment = new Payment();
        payment.setGymMember(member);
        payment.setPaymentDate(requestDTO.getPaymentDate());
        payment.setPaymentMethod(requestDTO.getMethod());
        payment.setStatus(requestDTO.getStatus());
        
        // 6. Set amounts and promotion
        if (promotion != null) {
            // For promotions, store the original amount and the discounted amount
            payment.setAmount(member.getMembershipPlan().getCost()); // Original amount
            payment.setDiscountedAmount(expectedAmount); // Amount after discount
            payment.setPromotion(promotion);
            
            // Increment promotion usage count
            promotion.setUsageCount(promotion.getUsageCount() == null ? 1 : promotion.getUsageCount() + 1);
            promotionRepository.save(promotion);
        } else {
            // No promotion, both amounts are the same
            payment.setAmount(expectedAmount);
            payment.setDiscountedAmount(expectedAmount);
        }
        
        // 7. Update member's payment history
        if (member.getPayments() == null) {
            member.setPayments(new ArrayList<>());
        }
        member.getPayments().add(payment);
        
        // 8. Save the payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // 9. Update member's membership dates if this is a new membership payment
        updateMembershipDatesIfNeeded(member, requestDTO.getPaymentDate());
        
        // 10. Map and return the response
        return paymentMapper.toResponseDTO(savedPayment);
    }

    private void validatePaymentRequest(PaymentRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("La solicitud de pago no puede ser nula");
        }
        
        if (requestDTO.getMemberId() == null) {
            throw new IllegalArgumentException("El ID del miembro es requerido");
        }
        
        if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }
        
        if (requestDTO.getPaymentDate() == null) {
            throw new IllegalArgumentException("La fecha de pago es requerida");
        }
        
        if (requestDTO.getMethod() == null) {
            throw new IllegalArgumentException("El método de pago es requerido");
        }
        
        if (requestDTO.getStatus() == null) {
            requestDTO.setStatus(PaymentStatus.PENDIENTE); // Default status if not provided
        }
    }

    @Override
    public PaymentResponseDTO getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public MemberPaymentStatusDTO getMemberPaymentStatus(UUID memberId) {
        // Get basic member info
        GymMember member = gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));
                
        // Get member with memberships
        GymMember memberWithMemberships = gymMemberRepository.findByIdWithMemberships(memberId)
                .orElse(member);
                
        // Get member with promotions
        GymMember memberWithPromotions = gymMemberRepository.findByIdWithPromotions(memberId)
                .orElse(member);
                
        // Get member with payments and filter completed ones
        List<Payment> completedPayments = gymMemberRepository.findByIdWithPayments(memberId)
                .map(gm -> gm.getPayments() != null ? 
                    gm.getPayments().stream()
                            .filter(p -> p.getStatus() == PaymentStatus.COMPLETADO)
                            .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                            .toList() :
                    Collections.<Payment>emptyList())
                .orElse(Collections.emptyList());
                
        // Calculate total paid amount
        BigDecimal totalPaid = completedPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        // Get active promotion if any
        Promotion activePromotion = memberWithPromotions.getPromotions() != null ? 
                memberWithPromotions.getPromotions().stream()
                        .filter(p -> p.getEndDate() == null || p.getEndDate().isAfter(java.time.LocalDate.now()))
                        .findFirst()
                        .orElse(null) :
                null;
                
        // Get active membership record
        MembershipRecord activeMembership = memberWithMemberships.getMembershipRecords() != null ?
                memberWithMemberships.getMembershipRecords().stream()
                        .filter(mr -> mr.getEndDate() == null || mr.getEndDate().isAfter(java.time.LocalDate.now()))
                        .findFirst()
                        .orElse(null) :
                null;
                
        // Calculate membership cost from active membership record
        BigDecimal membershipCost = BigDecimal.ZERO;
        if (activeMembership != null && activeMembership.getMembershipPlan() != null) {
            membershipCost = activeMembership.getMembershipPlan().getCost() != null ?
                    activeMembership.getMembershipPlan().getCost() : BigDecimal.ZERO;
        }
        
        // Calculate discount if promotion exists
        BigDecimal discountPercentage = BigDecimal.ZERO;
        BigDecimal totalWithDiscount = membershipCost;
        
        if (activePromotion != null && activePromotion.getDiscountPercentage() != null) {
            discountPercentage = activePromotion.getDiscountPercentage();
            if (membershipCost.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = membershipCost.multiply(
                        discountPercentage.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP));
                totalWithDiscount = membershipCost.subtract(discountAmount).setScale(2, java.math.RoundingMode.HALF_UP);
                totalWithDiscount = totalWithDiscount.max(BigDecimal.ZERO);
            }
        }
        
        // Calculate remaining balance (can be negative if overpaid)
        BigDecimal remainingBalance = totalWithDiscount.subtract(totalPaid);
        
        return MemberPaymentStatusDTO.builder()
                .memberId(memberId)
                .memberName(member.getName())
                .totalPaid(totalPaid)
                .totalWithDiscount(totalWithDiscount)
                .remainingBalance(remainingBalance)
                .isFullyPaid(remainingBalance.compareTo(BigDecimal.ZERO) <= 0)
                .activePromotionId(activePromotion != null ? activePromotion.getId() : null)
                .activePromotionName(activePromotion != null ? activePromotion.getName() : null)
                .discountPercentage(discountPercentage)
                .build();
    }

    @Override
    public PaymentResponseDTO updatePayment(UUID id, PaymentRequestDTO requestDTO) {
        // Get existing payment
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Map the updated fields from DTO to entity using the mapper with context
        Payment updatedPayment = paymentMapper.fromRequestDTO(
            requestDTO,
            gymMemberRepository,
            promotionRepository
        );
        
        // Preserve the ID and other fields that shouldn't be updated
        updatedPayment.setId(existingPayment.getId());
        
        // Save the updated payment
        updatedPayment = paymentRepository.save(updatedPayment);
        return paymentMapper.toResponseDTO(updatedPayment);
    }

    @Override
    public void deletePayment(UUID id) {
        paymentRepository.deleteById(id);
    }
    
    /**
     * Updates the membership dates for a member based on their payment.
     * If this is the first payment, sets the membership start and end dates.
     * If it's a renewal, extends the membership end date.
     * 
     * @param member The gym member
     * @param paymentDate The date when the payment was made
     */
    private void updateMembershipDatesIfNeeded(GymMember member, LocalDate paymentDate) {
        if (member == null || paymentDate == null) {
            return;
        }
        
        // If no membership plan, nothing to update
        if (member.getMembershipPlan() == null || member.getMembershipPlan().getDurationMonths() == null) {
            return;
        }
        
        // Calculate new end date based on plan duration
        int durationMonths = member.getMembershipPlan().getDurationMonths();
        LocalDate newEndDate = paymentDate.plusMonths(durationMonths);
        
        // If member already has an active membership, extend from the current end date
        if (member.getMembershipEndDate() != null && 
            member.getMembershipEndDate().isAfter(paymentDate)) {
            newEndDate = member.getMembershipEndDate().plusMonths(durationMonths);
        }
        
        // If this is the first payment, set the start date
        if (member.getMembershipStartDate() == null) {
            member.setMembershipStartDate(paymentDate);
        }
        
        // Update the end date
        member.setMembershipEndDate(newEndDate);
        
        // Save the updated member
        gymMemberRepository.save(member);
    }
}
