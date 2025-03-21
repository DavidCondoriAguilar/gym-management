package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.mapper.PromotionMapper;
import com.gymmanagement.gym_app.model.PromotionModel;
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
    public PromotionModel createPromotion(PromotionModel promotionModel) {
        // 1️⃣ Convertir el modelo a entidad
        Promotion promotion = promotionMapper.toEntity(promotionModel);

        // 2️⃣ Guardar la promoción para asignarle un ID en la BD
        promotion = promotionRepository.save(promotion);

        // 3️⃣ Procesar los GymMembers asociados (si existen) y actualizar pagos
        if (promotionModel.getGymMemberIds() != null && !promotionModel.getGymMemberIds().isEmpty()) {
            List<GymMember> gymMembers = gymMemberRepository.findAllById(promotionModel.getGymMemberIds());
            for (GymMember gymMember : gymMembers) {
                // Verificar que el GymMember tenga un MembershipPlan válido
                if (gymMember.getMembershipPlan() == null) {
                    throw new IllegalStateException("El miembro del gimnasio no tiene un plan de membresía asignado.");
                }
                // Aplicar descuento a los pagos existentes (si hay)
                List<Payment> payments = gymMember.getPayments();
                if (payments != null && !payments.isEmpty()) {
                    for (Payment payment : payments) {
                        payment.applyDiscount(promotion.getDiscountPercentage());
                    }
                    paymentRepository.saveAll(payments);
                }
                // Asociar la promoción al miembro
                gymMember.getPromotions().add(promotion);
                gymMemberRepository.save(gymMember);
            }
            promotion.setGymMembers(gymMembers);
        }

        // 4️⃣ Guardar nuevamente la promoción para sincronizar la relación
        Promotion savedPromotion = promotionRepository.save(promotion);
        PromotionModel result = promotionMapper.toModel(savedPromotion);

        // 5️⃣ Calcular el total con descuento a partir del plan del primer GymMember asociado (si existe)
        if (savedPromotion.getGymMembers() != null && !savedPromotion.getGymMembers().isEmpty()) {
            GymMember firstMember = savedPromotion.getGymMembers().get(0);
            if (firstMember.getMembershipPlan() != null) {
                BigDecimal originalCost = firstMember.getMembershipPlan().getCost();
                BigDecimal discount = originalCost.multiply(promotion.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal totalWithDiscount = originalCost.subtract(discount);
                result.setTotalWithDiscount(totalWithDiscount);
            }
        } else {
            result.setTotalWithDiscount(null);
        }

        return result;
    }

    @Override
    public List<PromotionModel> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::addTotalWithDiscount)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionModel getPromotionById(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        return addTotalWithDiscount(promotion);
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
        return addTotalWithDiscount(updatedPromotion);
    }

    @Override
    public void deletePromotion(UUID id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));
        promotionRepository.delete(promotion);
    }

    /**
     * Método auxiliar para calcular y asignar el total con descuento a la promoción.
     */
    private PromotionModel addTotalWithDiscount(Promotion promotion) {
        PromotionModel model = promotionMapper.toModel(promotion);
        if (promotion.getGymMembers() != null && !promotion.getGymMembers().isEmpty()) {
            GymMember firstMember = promotion.getGymMembers().get(0);
            if (firstMember.getMembershipPlan() != null) {
                BigDecimal originalCost = firstMember.getMembershipPlan().getCost();
                BigDecimal discount = originalCost.multiply(promotion.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal totalWithDiscount = originalCost.subtract(discount);
                model.setTotalWithDiscount(totalWithDiscount);
            } else {
                model.setTotalWithDiscount(null);
            }
        } else {
            model.setTotalWithDiscount(null);
        }
        return model;
    }
}
