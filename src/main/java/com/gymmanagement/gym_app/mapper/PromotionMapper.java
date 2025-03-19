package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.model.PromotionModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    PromotionModel toModel(Promotion entity);
    Promotion toEntity(PromotionModel model);
}
