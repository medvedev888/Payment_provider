package me.vladislav.payment_provider.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.vladislav.payment_provider.dto.BankPaymentResponse;
import me.vladislav.payment_provider.dto.CallbackRequest;
import me.vladislav.payment_provider.dto.CreateBankPaymentRequest;
import me.vladislav.payment_provider.dto.CreateBankPaymentResponse;
import me.vladislav.payment_provider.exception.BusinessException;
import me.vladislav.payment_provider.exception.NotFoundException;
import me.vladislav.payment_provider.model.BankPayment;
import me.vladislav.payment_provider.model.BankPaymentStatus;
import me.vladislav.payment_provider.repository.BankPaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankPaymentService {

    private final BankPaymentRepository bankPaymentRepository;
    private final CallbackService callbackService;

    @Value("${app.payment.base-url}")
    private String baseUrl;

    @Transactional
    public CreateBankPaymentResponse createPayment(CreateBankPaymentRequest request) {
        String providerPaymentId = "bank-pay-" + UUID.randomUUID();

        BankPayment payment = new BankPayment();
        payment.setProviderPaymentId(providerPaymentId);
        payment.setEnrollmentId(request.getEnrollmentId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency().toUpperCase());
        payment.setStatus(BankPaymentStatus.CREATED);
        payment.setCallbackUrl(request.getCallbackUrl());

        bankPaymentRepository.save(payment);

        String paymentUrl = baseUrl + "/api/bank/payments/" + providerPaymentId;

        log.info("Created payment with providerPaymentId: {}", providerPaymentId);
        return new CreateBankPaymentResponse(providerPaymentId, paymentUrl);
    }


    public BankPaymentResponse getPaymentByProviderPaymentId(String providerPaymentId) {
        BankPayment payment = bankPaymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + providerPaymentId));

        return mapToResponse(payment);
    }


    @Transactional
    public void markAsPaid(String providerPaymentId) {
        BankPayment payment = bankPaymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + providerPaymentId));

        if (payment.getStatus() != BankPaymentStatus.CREATED) {
            throw new BusinessException("Payment already completed with status: " + payment.getStatus());
        }

        payment.setStatus(BankPaymentStatus.PAID);
        payment.setCompletedAt(LocalDateTime.now());
        bankPaymentRepository.save(payment);

        CallbackRequest callback = new CallbackRequest(
                providerPaymentId,
                BankPaymentStatus.PAID,
                null
        );
        callbackService.sendPaymentResult(payment.getCallbackUrl(), callback);
        log.info("Payment {} marked as PAID, callback sent", providerPaymentId);
    }


    @Transactional
    public void markAsFailed(String providerPaymentId, String failureReason) {
        BankPayment payment = bankPaymentRepository.findByProviderPaymentId(providerPaymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + providerPaymentId));

        if (payment.getStatus() != BankPaymentStatus.CREATED) {
            throw new BusinessException("Payment already completed with status: " + payment.getStatus());
        }

        payment.setStatus(BankPaymentStatus.FAILED);
        payment.setFailureReason(failureReason);
        payment.setCompletedAt(LocalDateTime.now());
        bankPaymentRepository.save(payment);

        CallbackRequest callback = new CallbackRequest(
                providerPaymentId,
                BankPaymentStatus.FAILED,
                failureReason
        );
        callbackService.sendPaymentResult(payment.getCallbackUrl(), callback);
        log.info("Payment {} marked as FAILED, reason: {}, callback sent",
                providerPaymentId, failureReason);
    }


    private BankPaymentResponse mapToResponse(BankPayment payment) {
        BankPaymentResponse response = new BankPaymentResponse();
        response.setProviderPaymentId(payment.getProviderPaymentId());
        response.setEnrollmentId(payment.getEnrollmentId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setStatus(payment.getStatus());
        response.setCallbackUrl(payment.getCallbackUrl());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setCompletedAt(payment.getCompletedAt());
        return response;
    }

}