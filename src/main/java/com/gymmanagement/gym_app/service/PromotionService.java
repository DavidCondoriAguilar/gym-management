package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.model.PromotionModel;

import java.util.List;
import java.util.UUID;

public interface PromotionService {
    PromotionModel createPromotion(PromotionModel promotionModel);
    List<PromotionModel> getAllPromotions();
    PromotionModel getPromotionById(UUID id);
    PromotionModel updatePromotion(UUID id, PromotionModel promotionModel);
    void deletePromotion(UUID id);
}
