package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.PromotionRequestDTO;
import com.gymmanagement.gym_app.dto.response.PromotionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PromotionService {
    PromotionResponseDTO createPromotion(PromotionRequestDTO requestDTO);
    List<PromotionResponseDTO> getAllPromotions();
    PromotionResponseDTO getPromotionById(UUID id);
    PromotionResponseDTO updatePromotion(UUID id, PromotionRequestDTO requestDTO);
    void deletePromotion(UUID id);
}
