package me.vladislav.payment_provider.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.vladislav.payment_provider.dto.BankPaymentResponse;
import me.vladislav.payment_provider.dto.CompletePaymentRequest;
import me.vladislav.payment_provider.dto.CreateBankPaymentRequest;
import me.vladislav.payment_provider.dto.CreateBankPaymentResponse;
import me.vladislav.payment_provider.service.BankPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank/payments")
@RequiredArgsConstructor
public class BankPaymentController {

    private final BankPaymentService bankPaymentService;

    @PostMapping
    public ResponseEntity<CreateBankPaymentResponse> createPayment(@Valid @RequestBody CreateBankPaymentRequest request) {
        CreateBankPaymentResponse response = bankPaymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{providerPaymentId}")
    public ResponseEntity<BankPaymentResponse> getPayment(@PathVariable String providerPaymentId) {
        BankPaymentResponse response = bankPaymentService.getPaymentByProviderPaymentId(providerPaymentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{providerPaymentId}/success")
    public ResponseEntity<Void> markAsSuccess(@PathVariable String providerPaymentId) {
        bankPaymentService.markAsPaid(providerPaymentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{providerPaymentId}/fail")
    public ResponseEntity<Void> markAsFailed(@PathVariable String providerPaymentId,
                                             @RequestBody(required = false) CompletePaymentRequest request) {
        String reason = (request != null && request.getFailureReason() != null)
                ? request.getFailureReason()
                : "Payment failed";
        bankPaymentService.markAsFailed(providerPaymentId, reason);
        return ResponseEntity.ok().build();
    }
}