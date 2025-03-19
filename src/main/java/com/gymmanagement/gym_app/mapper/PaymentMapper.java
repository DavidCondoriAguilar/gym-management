package com.gymmanagement.gym_app.mapper;

import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.model.PaymentModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentModel toModel(Payment entity);
    Payment toEntity(PaymentModel model);
}

