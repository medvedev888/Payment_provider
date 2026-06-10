package me.vladislav.payment_provider.integration.kafka.event;

import java.math.BigDecimal;

public record PaymentCreateRequestedEvent(
        Long paymentId,
        Long enrollmentId,
        BigDecimal amount,
        String currency
) {
}
