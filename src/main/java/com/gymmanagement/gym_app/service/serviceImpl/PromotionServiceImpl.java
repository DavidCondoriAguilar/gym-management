package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.request.PromotionRequestDTO;
import com.gymmanagement.gym_app.dto.response.PromotionResponseDTO;
import com.gymmanagement.gym_app.dto.response.PromotionSummaryDTO;
import com.gymmanagement.gym_app.mapper.PromotionMapper;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.PaymentRepository;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import com.gymmanagement.gym_app.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final GymMemberRepository gymMemberRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PromotionResponseDTO createPromotion(PromotionRequestDTO requestDTO) {
        Promotion promotion = promotionMapper.fromRequestDTO(requestDTO);
        if(requestDTO.getMemberIds() != null && !requestDTO.getMemberIds().isEmpty()) {
            List<GymMember> gymMembers = gymMemberRepository.findAllById(requestDTO.getMemberIds());
            promotion.setGymMembers(gymMembers);
        }
        promotion = promotionRepository.save(promotion);
        return promotionMapper.toResponseDTO(promotion);
    }

    @Override
    public List<PromotionResponseDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
            .map(promotionMapper::toResponseDTO)
            .toList();
    }

    @Override
    public PromotionResponseDTO getPromotionById(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promotion not found"));
        return promotionMapper.toResponseDTO(promotion);
    }

    @Override
    public PromotionResponseDTO updatePromotion(UUID id, PromotionRequestDTO requestDTO) {
        Promotion existingPromotion = promotionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Promotion not found"));
        existingPromotion.setName(requestDTO.getName());
        existingPromotion.setDiscountPercentage(requestDTO.getDiscountPercentage());
        existingPromotion.setStartDate(requestDTO.getStartDate());
        existingPromotion.setEndDate(requestDTO.getEndDate());
        existingPromotion.setMaxUses(requestDTO.getMaxUses());
        existingPromotion.setDescription(requestDTO.getDescription());
        if(requestDTO.getMemberIds() != null && !requestDTO.getMemberIds().isEmpty()) {
            List<GymMember> gymMembers = gymMemberRepository.findAllById(requestDTO.getMemberIds());
            existingPromotion.setGymMembers(gymMembers);
        }
        existingPromotion = promotionRepository.save(existingPromotion);
        return promotionMapper.toResponseDTO(existingPromotion);
    }

    @Override
    public List<PromotionSummaryDTO> getPromotionsSummary() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream()
                .map(promotionMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePromotion(UUID id) {
        promotionRepository.deleteById(id);
    }
}
