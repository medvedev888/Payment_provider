package me.vladislav.payment_provider.dto;

import lombok.Data;

@Data
public class CompletePaymentRequest {
    private String failureReason;
}