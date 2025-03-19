package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.model.PromotionModel;
import com.gymmanagement.gym_app.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionModel> createPromotion(@RequestBody PromotionModel promotionModel) {
        return ResponseEntity.ok(promotionService.createPromotion(promotionModel));
    }

    @GetMapping
    public ResponseEntity<List<PromotionModel>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromotionModel> getPromotionById(@PathVariable UUID id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionModel> updatePromotion(@PathVariable UUID id, @RequestBody PromotionModel promotionModel) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, promotionModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}
