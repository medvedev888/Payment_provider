package me.vladislav.payment_provider.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBankPaymentRequest {
    @NotNull(message = "enrollmentId must not be null")
    private Long enrollmentId;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "currency must not be null")
    private String currency;

    @NotNull(message = "callbackUrl must not be null")
    private String callbackUrl;
}