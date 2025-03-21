package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.model.PromotionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(target = "gymMemberIds", expression = "java(mapGymMemberIds(entity))")
    PromotionModel toModel(Promotion entity);

    Promotion toEntity(PromotionModel model);

    default List<UUID> mapGymMemberIds(Promotion promotion) {
        if (promotion.getGymMembers() == null) {
            return null;
        }
        return promotion.getGymMembers().stream()
                .map(GymMember::getId)
                .collect(Collectors.toList());
    }

}
