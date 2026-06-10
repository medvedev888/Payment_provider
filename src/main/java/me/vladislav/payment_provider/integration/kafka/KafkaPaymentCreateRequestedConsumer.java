package me.vladislav.payment_provider.integration.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vladislav.payment_provider.integration.kafka.event.PaymentCreateRequestedEvent;
import me.vladislav.payment_provider.service.BankPaymentService;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor

@Component
public class KafkaPaymentCreateRequestedConsumer {
    private final KafkaConsumer<String, String> kafkaConsumer;
    private final ObjectMapper objectMapper;
    private final BankPaymentService bankPaymentService;

    @Value("${kafka.topics.payment-create-requested}")
    private String paymentCreateRequestedTopic;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @PostConstruct
    public void start() {
        executorService.submit(this::listen);
    }


    private void listen() {
        kafkaConsumer.subscribe(List.of(paymentCreateRequestedTopic));

        while(!Thread.currentThread().isInterrupted()) {
            var records = kafkaConsumer.poll(Duration.ofMillis(1000));

            boolean allRecordsProcessedSuccessfully = true;

            for(var record : records) {
                try {
                    PaymentCreateRequestedEvent paymentCreateRequestedEvent =
                            objectMapper.readValue(record.value(), PaymentCreateRequestedEvent.class);

                    log.info("Received payment create requested event: {}", paymentCreateRequestedEvent);

                    bankPaymentService.createPayment(paymentCreateRequestedEvent);

                } catch (Exception e) {
                    allRecordsProcessedSuccessfully = false;
                    log.error(
                            "Failed to process payment create requested event. Topic: {}, partition: {}, offset: {}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            e
                    );
                    break;
                }
            }

            if (allRecordsProcessedSuccessfully && !records.isEmpty()) {
                kafkaConsumer.commitSync();
            }

        }
    }


    @PreDestroy
    public void stop() {
        kafkaConsumer.wakeup();
        executorService.shutdown();
    }

}
