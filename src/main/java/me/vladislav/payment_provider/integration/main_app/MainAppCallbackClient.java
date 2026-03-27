package me.vladislav.payment_provider.integration.main_app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.vladislav.payment_provider.dto.CallbackRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
@Slf4j
public class MainAppCallbackClient {

    private final RestTemplate restTemplate;

    public void sendCallback(String callbackUrl, CallbackRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CallbackRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity(callbackUrl, entity, Void.class);
            log.info("Callback sent to {} for payment {}", callbackUrl, request.getProviderPaymentId());
        } catch (RestClientException e) {
            log.error("Failed to send callback to {} for payment {}: {}",
                    callbackUrl, request.getProviderPaymentId(), e.getMessage());
        }
    }

}