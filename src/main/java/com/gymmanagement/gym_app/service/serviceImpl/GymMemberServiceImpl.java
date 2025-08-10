package com.gymmanagement.gym_app.service.serviceImpl;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.request.GymMemberRequestDTO;
import com.gymmanagement.gym_app.dto.response.GymMemberResponseDTO;
import com.gymmanagement.gym_app.mapper.GymMemberMapper;
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
    public List<GymMemberResponseDTO> getAllMembers() {
        return gymMemberRepository.findAll().stream()
                .map(gymMemberMapper::toResponseDTO)
                .toList();
    }

    @Override
    public GymMemberResponseDTO getMemberById(UUID id) {
        GymMember gymMember = findMemberById(id);
        return gymMemberMapper.toResponseDTO(gymMember);
    }

    @Override
    public GymMemberResponseDTO createMember(@Valid GymMemberRequestDTO gymMemberRequestDTO) {
        // Si no se especifica fecha de finalización, se establece por defecto a 1 mes a partir de hoy
        if (gymMemberRequestDTO.getMembershipEndDate() == null) {
            gymMemberRequestDTO.setMembershipEndDate(LocalDate.now().plusMonths(1));
        }

        // Cargar MembershipPlan
        MembershipPlan membershipPlan = membershipPlanRepository.findById(gymMemberRequestDTO.getMembershipPlanId())
                .orElseThrow(() -> new RuntimeException("Membership Plan not found"));

        // Cargar las Promociones desde la base de datos usando los IDs enviados en el DTO
        List<Promotion> promotions = gymMemberRequestDTO.getPromotionIds() != null
                ? gymMemberRequestDTO.getPromotionIds().stream()
                .map(p -> promotionRepository.findById(p)
                        .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + p)))
                .collect(Collectors.toList())
                : List.of();

        // Crear entidad
        GymMember gymMember = gymMemberMapper.fromRequestDTO(gymMemberRequestDTO);
        gymMember.setMembershipPlan(membershipPlan);
        gymMember.setPromotions(promotions);

        // Guardar
        gymMember = gymMemberRepository.save(gymMember);

        // Convertir la entidad guardada a DTO para la respuesta
        GymMemberResponseDTO result = gymMemberMapper.toResponseDTO(gymMember);

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
    public GymMemberResponseDTO updateMember(UUID id, @Valid GymMemberRequestDTO gymMemberRequestDTO) {
        GymMember existingMember = findMemberById(id);

        existingMember.setName(gymMemberRequestDTO.getName());
        existingMember.setEmail(gymMemberRequestDTO.getEmail());
        existingMember.setPhone(gymMemberRequestDTO.getPhone());
        existingMember.setActive(gymMemberRequestDTO.getActive());
        existingMember.setRegistrationDate(gymMemberRequestDTO.getRegistrationDate());
        existingMember.setMembershipStartDate(gymMemberRequestDTO.getMembershipStartDate());
        existingMember.setMembershipEndDate(gymMemberRequestDTO.getMembershipEndDate());

        // Actualizar MembershipPlan
        MembershipPlan membershipPlan = membershipPlanRepository.findById(gymMemberRequestDTO.getMembershipPlanId())
                .orElseThrow(() -> new RuntimeException("Membership Plan not found"));
        existingMember.setMembershipPlan(membershipPlan);

        // Actualizar Promotions
        List<Promotion> promotions = gymMemberRequestDTO.getPromotionIds() != null
                ? gymMemberRequestDTO.getPromotionIds().stream()
                .map(p -> promotionRepository.findById(p)
                        .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + p)))
                .collect(Collectors.toList())
                : List.of();
        existingMember.setPromotions(promotions);

        // Guardar
        existingMember = gymMemberRepository.save(existingMember);
        return gymMemberMapper.toResponseDTO(existingMember);
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
