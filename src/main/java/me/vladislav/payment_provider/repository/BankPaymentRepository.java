package me.vladislav.payment_provider.repository;

import me.vladislav.payment_provider.model.BankPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankPaymentRepository extends JpaRepository<BankPayment, Long> {
    Optional<BankPayment> findByProviderPaymentId(String providerPaymentId);
}