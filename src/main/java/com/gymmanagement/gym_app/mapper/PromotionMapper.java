package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.dto.request.PromotionRequestDTO;
import com.gymmanagement.gym_app.dto.response.PromotionResponseDTO;
import com.gymmanagement.gym_app.dto.response.PromotionSummaryDTO;
import com.gymmanagement.gym_app.model.PromotionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(target = "gymMemberIds", expression = "java(mapGymMemberIds(entity))")
    PromotionModel toModel(Promotion entity);

    Promotion toEntity(PromotionModel model);

    /**
     * Convierte una entidad Promotion a un DTO de resumen con información detallada de descuentos.
     * 
     * @param entity La entidad Promotion a convertir
     * @return DTO con el resumen de la promoción
     */
    default PromotionSummaryDTO toSummaryDTO(Promotion entity) {
        if (entity == null) {
            return null;
        }
        
        PromotionSummaryDTO dto = new PromotionSummaryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDiscountPercentage(entity.getDiscountPercentage());
        
        if (entity.getGymMembers() != null) {
            // Contar miembros con esta promoción
            dto.setMemberCount(entity.getGymMembers().size());
            
            // Calcular total original y descuentos
            BigDecimal totalOriginal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            
            for (var member : entity.getGymMembers()) {
                if (member.getMembershipPlan() != null && member.getMembershipPlan().getCost() != null) {
                    BigDecimal planCost = member.getMembershipPlan().getCost();
                    totalOriginal = totalOriginal.add(planCost);
                    
                    // Calcular descuento para este miembro
                    BigDecimal memberDiscount = planCost.multiply(
                        entity.getDiscountPercentage()
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                    );
                    totalDiscount = totalDiscount.add(memberDiscount);
                }
            }
            
            dto.setTotalOriginal(totalOriginal.setScale(2, RoundingMode.HALF_UP));
            dto.setTotalDiscount(totalDiscount.setScale(2, RoundingMode.HALF_UP));
            dto.setTotalWithDiscount(totalOriginal.subtract(totalDiscount).setScale(2, RoundingMode.HALF_UP));
        }
        
        return dto;
    }

    PromotionResponseDTO toResponseDTO(Promotion entity);

    Promotion fromRequestDTO(PromotionRequestDTO dto);

    default List<UUID> mapGymMemberIds(Promotion promotion) {
        if (promotion.getGymMembers() == null) {
            return null;
        }
        return promotion.getGymMembers().stream()
                .map(GymMember::getId)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el costo total con descuento para una promoción, considerando el costo total
     * del plan de membresía de cada miembro asociado a la promoción.
     * 
     * @param promotion La promoción para la cual calcular el total con descuento
     * @return El monto total con descuento aplicado
     */
    default BigDecimal calculateTotalWithDiscount(Promotion promotion) {
        if (promotion == null || promotion.getGymMembers() == null || 
            promotion.getDiscountPercentage() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return promotion.getGymMembers().stream()
            .map(member -> {
                // Obtener el costo total del plan del miembro
                if (member.getMembershipPlan() == null || member.getMembershipPlan().getCost() == null) {
                    return BigDecimal.ZERO;
                }
                
                BigDecimal planCost = member.getMembershipPlan().getCost();
                
                // Calcular el descuento como porcentaje del costo total del plan
                BigDecimal discount = planCost.multiply(
                    promotion.getDiscountPercentage()
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                );
                
                // Aplicar descuento al costo total del plan
                return planCost.subtract(discount);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP); // Asegurar 2 decimales
    }
}
