package me.vladislav.payment_provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBankPaymentResponse {
    private String providerPaymentId;
    private String paymentUrl;
}