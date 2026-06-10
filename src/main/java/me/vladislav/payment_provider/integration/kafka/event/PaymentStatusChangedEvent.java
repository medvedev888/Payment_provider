package me.vladislav.payment_provider.integration.kafka.event;

public record PaymentStatusChangedEvent(
        String providerPaymentId,
        String status,
        String failureReason
) {
}
