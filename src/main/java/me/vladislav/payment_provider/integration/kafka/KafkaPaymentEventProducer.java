package me.vladislav.payment_provider.integration.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vladislav.payment_provider.exception.KafkaEventPublishingException;
import me.vladislav.payment_provider.integration.kafka.event.PaymentCreatedEvent;
import me.vladislav.payment_provider.integration.kafka.event.PaymentStatusChangedEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RequiredArgsConstructor

@Component
public class KafkaPaymentEventProducer {
    private final KafkaProducer<String, String> kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.payment-created}")
    private String paymentCreatedTopic;

    @Value("${kafka.topics.payment-status-changed}")
    private String paymentStatusChangedTopic;


    public void sendPaymentCreated(PaymentCreatedEvent paymentCreatedEvent) {
        String key = paymentCreatedEvent.paymentId().toString();
        String value;

        try {
            value = objectMapper.writeValueAsString(paymentCreatedEvent);
        } catch (JacksonException e) {
            throw new KafkaEventPublishingException("Failed to serialize payment created event", e);
        }

        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(paymentCreatedTopic, key, value);

        kafkaProducer.send(producerRecord, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to publish payment created event", exception);
                return;
            }

            log.info("Published payment created event. Topic: "
                    + metadata.topic()
                    + ", partition: "
                    + metadata.partition()
                    + ", offset: "
                    + metadata.offset());
        });
    }


    public void sendPaymentStatusChanged(PaymentStatusChangedEvent paymentStatusChangedEvent) {
        String key = paymentStatusChangedEvent.providerPaymentId();
        String value;

        try {
            value = objectMapper.writeValueAsString(paymentStatusChangedEvent);
        } catch(JacksonException e) {
            throw new KafkaEventPublishingException(
                    "Failed to serialize payment status changed event", e);
        }

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                paymentStatusChangedTopic,
                key,
                value
        );

        kafkaProducer.send(producerRecord, ((metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to publish payment status changed event", exception);
                return;
            }

            log.info(
                    "Published payment status changed event. Topic: {}, partition: {}, offset: {}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );
        }));
    }

}
