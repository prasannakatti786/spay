package com.spay.wallet.transaction.repo;

import com.spay.wallet.transaction.entities.ChargeRate;
import com.spay.wallet.transaction.entities.TransactionType;
import com.spay.wallet.transaction.payment.PaymentChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ChargeRateRepository extends JpaRepository<ChargeRate, UUID> {
    @Query("select (count(c) > 0) from ChargeRate c where c.paymentChannel = ?1 and c.transactionType = ?2")
    boolean existsByPaymentChannelAndTransactionType(PaymentChannel paymentChannel, TransactionType transactionType);

    @Query("select c from ChargeRate c where c.paymentChannel = ?1 and c.transactionType = ?2")
    Optional<ChargeRate> findByPaymentChannelAndTransactionType(PaymentChannel paymentChannel, TransactionType transactionType);
}