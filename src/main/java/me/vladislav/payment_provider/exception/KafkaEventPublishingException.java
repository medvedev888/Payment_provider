package me.vladislav.payment_provider.exception;


public class KafkaEventPublishingException extends RuntimeException {
    public KafkaEventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}