package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.MembershipRecord;
import com.gymmanagement.gym_app.dto.request.MembershipRecordRequestDTO;
import com.gymmanagement.gym_app.dto.response.MembershipRecordResponseDTO;
import com.gymmanagement.gym_app.model.MembershipRecordModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MembershipRecordMapper {

    MembershipRecordMapper INSTANCE = Mappers.getMapper(MembershipRecordMapper.class);

    @Mapping(source = "gymMember.id", target = "gymMemberId")
    @Mapping(source = "membershipPlan.id", target = "membershipPlanId")
    @Mapping(source = "status", target = "status")
    MembershipRecordModel toModel(MembershipRecord entity);

    @Mapping(source = "gymMemberId", target = "gymMember.id")
    @Mapping(source = "membershipPlanId", target = "membershipPlan.id")
    @Mapping(source = "status", target = "status")
    MembershipRecord toEntity(MembershipRecordModel model);

    MembershipRecordResponseDTO toResponseDTO(MembershipRecord entity);
    MembershipRecord fromRequestDTO(MembershipRecordRequestDTO dto);

    @Named("booleanToStatus")
    default String booleanToStatus(boolean active) {
        return active ? "ACTIVE" : "INACTIVE";
    }

    @Named("statusToBoolean")
    default boolean statusToBoolean(String status) {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
