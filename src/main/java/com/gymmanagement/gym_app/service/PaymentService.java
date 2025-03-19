package com.gymmanagement.gym_app.service;

import com.gymmanagement.gym_app.model.PaymentModel;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentModel createPayment(PaymentModel paymentModel);
    PaymentModel getPaymentById(UUID id);
    List<PaymentModel> getAllPayments();
    PaymentModel updatePayment(UUID id, PaymentModel paymentModel);
    void deletePayment(UUID id);
}
