package me.vladislav.payment_provider.integration.kafka.event;

public record PaymentCreatedEvent(
        Long paymentId,
        String providerPaymentId,
        String paymentUrl
) {
}
