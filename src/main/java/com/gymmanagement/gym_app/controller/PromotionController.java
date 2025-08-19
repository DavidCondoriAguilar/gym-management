package com.gymmanagement.gym_app.controller;

import com.gymmanagement.gym_app.dto.request.PromotionRequestDTO;
import com.gymmanagement.gym_app.dto.response.PromotionResponseDTO;
import com.gymmanagement.gym_app.dto.response.PromotionSummaryDTO;
import com.gymmanagement.gym_app.model.PromotionModel;
import com.gymmanagement.gym_app.service.PromotionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PreAuthorize("permitAll")
    @GetMapping
    public ResponseEntity<List<PromotionResponseDTO>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/summary")
    public ResponseEntity<List<PromotionSummaryDTO>> getPromotionsSummary() {
        return ResponseEntity.ok(promotionService.getPromotionsSummary());
    }

    @PreAuthorize("permitAll")
    @GetMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> getPromotionById(@PathVariable UUID id) {
        return ResponseEntity.ok(promotionService.getPromotionById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<PromotionResponseDTO> createPromotion(@Valid @RequestBody PromotionRequestDTO requestDTO) {
        return ResponseEntity.ok(promotionService.createPromotion(requestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDTO> updatePromotion(@PathVariable UUID id, @Valid @RequestBody PromotionRequestDTO requestDTO) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, requestDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }
}
