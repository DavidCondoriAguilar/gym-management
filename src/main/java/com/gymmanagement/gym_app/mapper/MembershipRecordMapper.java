package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.MembershipRecord;
import com.gymmanagement.gym_app.model.MembershipRecordModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MembershipRecordMapper {
    MembershipRecordModel toModel(MembershipRecord entity);
    MembershipRecord toEntity(MembershipRecordModel model);
}
