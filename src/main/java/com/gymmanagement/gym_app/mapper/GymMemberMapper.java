package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.model.GymMemberModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GymMemberMapper {

    GymMemberMapper INSTANCE = Mappers.getMapper(GymMemberMapper.class);

    @Mapping(target = "membershipPlan", source = "membershipPlan")
    @Mapping(target = "pagos", source = "pagos")
    @Mapping(target = "membershipRecords", source = "membershipRecords")
    @Mapping(target = "promociones", source = "promociones")
    GymMemberModel toModel(GymMember entity);

    @Mapping(target = "membershipPlan", source = "membershipPlan")
    @Mapping(target = "pagos", source = "pagos")
    @Mapping(target = "membershipRecords", source = "membershipRecords")
    @Mapping(target = "promociones", source = "promociones")
    GymMember toEntity(GymMemberModel model);
}
