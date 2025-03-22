package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.mapper.GymMemberMapper;
import com.gymmanagement.gym_app.model.GymMemberModel;
import com.gymmanagement.gym_app.model.PromotionModel;
import com.gymmanagement.gym_app.repository.GymMemberRepository;
import com.gymmanagement.gym_app.repository.MembershipPlanRepository;
import com.gymmanagement.gym_app.repository.PromotionRepository;
import com.gymmanagement.gym_app.service.GymMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymMemberServiceImpl implements GymMemberService {

    private final GymMemberRepository gymMemberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PromotionRepository promotionRepository;
    private final GymMemberMapper gymMemberMapper;

    @Override
    public List<GymMemberModel> getAllMembers() {
        return gymMemberRepository.findAll().stream()
                .map(member -> {
                    GymMemberModel model = gymMemberMapper.toModel(member);
                    calculateTotalWithDiscount(model);
                    return model;
                })
                .toList();
    }

    private void calculateTotalWithDiscount(GymMemberModel model) {
        if (model.getMembershipPlan() != null && model.getPromotions() != null) {
            BigDecimal originalCost = model.getMembershipPlan().getCost();
            for (PromotionModel promotion : model.getPromotions()) {
                BigDecimal discount = originalCost.multiply(promotion.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal totalWithDiscount = originalCost.subtract(discount);
                promotion.setTotalWithDiscount(totalWithDiscount);
            }
        }
    }

    @Override
    public GymMemberModel getMemberById(UUID id) {
        GymMember gymMember = findMemberById(id);
        return gymMemberMapper.toModel(gymMember);
    }
    @Override
    public GymMemberModel createMember(@Valid GymMemberModel gymMemberModel) {
        // Si no se especifica fecha de finalización, se establece por defecto a 1 mes a partir de hoy
        if (gymMemberModel.getMembershipEnd() == null) {
            gymMemberModel.setMembershipEnd(LocalDate.now().plusMonths(1));
        }

        // Cargar el MembershipPlan desde la base de datos usando el ID enviado en el DTO
        MembershipPlan membershipPlan = null;
        if (gymMemberModel.getMembershipPlan() != null && gymMemberModel.getMembershipPlan().getId() != null) {
            membershipPlan = membershipPlanRepository.findById(gymMemberModel.getMembershipPlan().getId())
                    .orElseThrow(() -> new RuntimeException("Membership Plan not found"));
        }

        // Cargar las Promociones desde la base de datos usando los IDs enviados en el DTO
        List<Promotion> promotions = gymMemberModel.getPromotions() != null
                ? gymMemberModel.getPromotions().stream()
                .map(p -> promotionRepository.findById(p.getId())
                        .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + p.getId())))
                .collect(Collectors.toList())
                : List.of();

        // Convertir el DTO a entidad usando el mapper
        GymMember gymMember = gymMemberMapper.toEntity(gymMemberModel);
        gymMember.setMembershipPlan(membershipPlan);
        gymMember.setPromotions(promotions);

        // Guardar el GymMember en la base de datos
        gymMember = gymMemberRepository.save(gymMember);

        // Convertir la entidad guardada a DTO para la respuesta
        GymMemberModel result = gymMemberMapper.toModel(gymMember);

        // Si el miembro tiene un MembershipPlan y se asociaron promociones, calculamos el total con descuento
        if (membershipPlan != null && promotions != null && !promotions.isEmpty()) {
            BigDecimal originalCost = membershipPlan.getCost();
            // Por cada promoción, calcular el total con descuento y actualizar el DTO correspondiente
            for (Promotion promotion : promotions) {
                BigDecimal discount = originalCost.multiply(promotion.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal totalWithDiscount = originalCost.subtract(discount);
                // Actualizamos el total con descuento en el DTO de promoción del resultado
                if (result.getPromotions() != null) {
                    result.getPromotions().stream()
                            .filter(promo -> promo.getId().equals(promotion.getId()))
                            .findFirst()
                            .ifPresent(promo -> promo.setTotalWithDiscount(totalWithDiscount));
                }
            }
        } else {
            // Si no se asociaron promociones, se asigna null o 0 según lo que decidas
            if (result.getPromotions() != null) {
                result.getPromotions().forEach(promo -> promo.setTotalWithDiscount(BigDecimal.ZERO));
            }
        }

        return result;
    }


    @Override
    public GymMemberModel updateMember(UUID id, @Valid GymMemberModel gymMemberModel) {
        GymMember existingMember = findMemberById(id);

        existingMember.setName(gymMemberModel.getName());
        existingMember.setEmail(gymMemberModel.getEmail());
        existingMember.setPhone(gymMemberModel.getPhone());
        existingMember.setActive(gymMemberModel.getActive());
        existingMember.setRegistrationDate(gymMemberModel.getRegistrationDate());
        existingMember.setMembershipStartDate(gymMemberModel.getMembershipStart());
        existingMember.setMembershipEndDate(gymMemberModel.getMembershipEnd());

        // Actualizar MembershipPlan si se proporciona
        if (gymMemberModel.getMembershipPlan() != null && gymMemberModel.getMembershipPlan().getId() != null) {
            MembershipPlan membershipPlan = membershipPlanRepository.findById(gymMemberModel.getMembershipPlan().getId())
                    .orElseThrow(() -> new RuntimeException("Membership Plan not found"));
            existingMember.setMembershipPlan(membershipPlan);
        }

        // Actualizar Promotions si se proporcionan
        if (gymMemberModel.getPromotions() != null) {
            List<Promotion> promotions = gymMemberModel.getPromotions().stream()
                    .map(p -> promotionRepository.findById(p.getId())
                            .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + p.getId())))
                    .collect(Collectors.toList());
            existingMember.setPromotions(promotions);
        }

        existingMember = gymMemberRepository.save(existingMember);
        return gymMemberMapper.toModel(existingMember);
    }

    @Override
    public void deleteMember(UUID id) {
        GymMember gymMember = findMemberById(id);
        gymMemberRepository.delete(gymMember);
    }

    private GymMember findMemberById(UUID id) {
        return gymMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + id));
    }
}
