package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.dto.request.MembershipPlanRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipPlanResponseDTO;
import com.gymmanagement.gym_app.model.MembershipPlanModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MembershipPlanMapper {
    MembershipPlanMapper INSTANCE = Mappers.getMapper(MembershipPlanMapper.class);

    MembershipPlanModel toModel(MembershipPlan entity);
    List<MembershipPlanModel> toModelList(List<MembershipPlan> entities);
    @Mapping(source = "type", target = "type")
    MembershipPlan toEntity(MembershipPlanModel membershipPlanModel);
    MembershipPlanResponseDTO toResponseDTO(MembershipPlan entity);
    MembershipPlan fromRequestDTO(MembershipPlanRequestDTO dto);
}
