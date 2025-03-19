package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.model.GymMemberModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = { MembershipPlanMapper.class, PaymentMapper.class, MembershipRecordMapper.class, PromotionMapper.class })
public interface GymMemberMapper {

    GymMemberMapper INSTANCE = Mappers.getMapper(GymMemberMapper.class);

    @Mapping(target = "membershipStart", source = "membershipStartDate") // Diferente nombre
    @Mapping(target = "membershipEnd", source = "membershipEndDate") // Diferente nombre
    GymMemberModel toModel(GymMember entity);

    @Mapping(target = "membershipStartDate", source = "membershipStart")
    @Mapping(target = "membershipEndDate", source = "membershipEnd")
    GymMember toEntity(GymMemberModel model);
}
