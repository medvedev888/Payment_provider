package me.vladislav.payment_provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.vladislav.payment_provider.dto.CallbackRequest;
import me.vladislav.payment_provider.integration.main_app.MainAppCallbackClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackService {

    private final MainAppCallbackClient callbackClient;

    public void sendPaymentResult(String callbackUrl, CallbackRequest request) {
        callbackClient.sendCallback(callbackUrl, request);
    }
}