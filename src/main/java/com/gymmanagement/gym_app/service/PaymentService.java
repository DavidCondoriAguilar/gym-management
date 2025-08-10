package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.dto.request.PaymentRequestDTO;
import com.gymmanagement.gym_app.dto.response.PaymentResponseDTO;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDTO createPayment(PaymentRequestDTO requestDTO);
    PaymentResponseDTO getPaymentById(UUID id);
    List<PaymentResponseDTO> getAllPayments();
    PaymentResponseDTO updatePayment(UUID id, PaymentRequestDTO requestDTO);
    void deletePayment(UUID id);
}
