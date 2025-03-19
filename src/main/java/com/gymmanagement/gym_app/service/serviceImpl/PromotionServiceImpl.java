package com.gymmanagement.gym_app.service.impl;

import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.mapper.PromotionMapper;
import com.gymmanagement.gym_app.model.PromotionModel;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import com.gymmanagement.gym_app.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionModel createPromotion(PromotionModel promotionModel) {
        Promotion promotion = promotionMapper.toEntity(promotionModel);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return promotionMapper.toModel(savedPromotion);
    }

    @Override
    public List<PromotionModel> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(promotionMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionModel getPromotionById(UUID id) {
        return promotionRepository.findById(id)
                .map(promotionMapper::toModel)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
    }

    @Override
    public PromotionModel updatePromotion(UUID id, PromotionModel promotionModel) {
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));

        existingPromotion.setName(promotionModel.getName());
        existingPromotion.setDiscountPercentage(promotionModel.getDiscountPercentage());
        existingPromotion.setStartDate(promotionModel.getStartDate());
        existingPromotion.setEndDate(promotionModel.getEndDate());

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return promotionMapper.toModel(updatedPromotion);
    }

    @Override
    public void deletePromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        promotionRepository.delete(promotion);
    }
}
