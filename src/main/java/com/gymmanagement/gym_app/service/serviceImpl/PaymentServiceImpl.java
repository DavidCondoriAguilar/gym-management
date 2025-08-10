package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import com.gymmanagement.gym_app.mapper.PaymentMapper;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.PaymentRepository;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import com.gymmanagement.gym_app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
        GymMember gymMember = gymMemberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("GymMember not found"));
        Payment payment = paymentMapper.fromRequestDTO(requestDTO);
        payment.setGymMember(gymMember);
        if(requestDTO.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(requestDTO.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));
            payment.setPromotion(promotion);
        }
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(payment);
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
    public PaymentResponseDTO updatePayment(UUID id, PaymentRequestDTO requestDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setAmount(requestDTO.getAmount());
        payment.setPaymentDate(requestDTO.getPaymentDate());
        payment.setPaymentMethod(requestDTO.getMethod());
        payment.setStatus(requestDTO.getStatus());
        if(requestDTO.getPromotionId() != null) {
            Promotion promotion = promotionRepository.findById(requestDTO.getPromotionId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));
            payment.setPromotion(promotion);
        } else {
            payment.setPromotion(null);
        }
        payment = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(payment);
    }

    @Override
    public void deletePayment(UUID id) {
        paymentRepository.deleteById(id);
    }
}
