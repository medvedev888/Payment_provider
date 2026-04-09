package me.vladislav.payment_provider.dto;

import lombok.Data;
import me.vladislav.payment_provider.model.BankPaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankPaymentResponse {
    private String providerPaymentId;
    private Long enrollmentId;
    private BigDecimal amount;
    private String currency;
    private BankPaymentStatus status;
    private String callbackUrl;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Integer retryCount;
}