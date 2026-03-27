package me.vladislav.payment_provider.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.vladislav.payment_provider.model.BankPaymentStatus;

@Data
@AllArgsConstructor
public class CallbackRequest {
    private String providerPaymentId;
    private BankPaymentStatus status;
    private String failureReason;
}