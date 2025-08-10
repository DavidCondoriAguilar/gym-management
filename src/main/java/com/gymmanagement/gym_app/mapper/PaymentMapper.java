package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import com.gymmanagement.gym_app.model.PaymentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "gymMemberId", source = "gymMember.id")
    PaymentModel toModel(Payment payment);

    @Mapping(target = "gymMember", ignore = true)
    Payment toEntity(PaymentModel paymentModel);

    PaymentResponseDTO toResponseDTO(Payment entity);

    Payment fromRequestDTO(PaymentRequestDTO dto);
}
