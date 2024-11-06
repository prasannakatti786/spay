package com.spay.wallet.transaction.entities;

import com.spay.wallet.account.entities.CurrencyCode;
import com.spay.wallet.transaction.payment.PaymentChannel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction_history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionHistory {
    @Id
    @Column(name = "id", nullable = false)
    private String transactionId;
    @Column(length = 200)
    private String xTransactionId;
    @Column(length = 200, nullable = false)
    private String senderAccount;
    @Column(length = 200,nullable = false)
    private String receiverAccount;
    @Column(length = 200)
    private String description;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal creditAmount;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal debitAmount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode creditCurrencyCode;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode debitCurrencyCode;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal debitCurrentBalance;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal creditCurrentBalance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentChannel paymentChannel;
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    @Column(precision = 100, scale = 10, nullable = false)
    private BigDecimal charge;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal exchangeFee;
    @Embedded
    private TransactionLocation transactionLocation;

}