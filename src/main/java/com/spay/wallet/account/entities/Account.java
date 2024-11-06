package com.spay.wallet.account.entities;

import com.spay.wallet.transaction.payment.PaymentChannel;
import com.spay.wallet.transaction.entities.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    private String accountId;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Column(length = 100)
    private String accountName;
    @Column(length = 100)
    private String iban;
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;
    @Column(length = 50, nullable = false)
    private String currencyName;
    @Column(nullable = false)
    private Integer currencyNumericCode;
    @Column(length = 10, nullable = false)
    private String currencySymbol;
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal balance;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal shadowBalance;
    @Column(precision = 100, scale = 10,nullable = false)
    private BigDecimal pendingBalance;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime modifiedAt;
    @Column(nullable = false)
    private LocalDateTime expireAt;
    private LocalDateTime lestDebitAt;
    @Enumerated(EnumType.STRING)
    private PaymentChannel lastDebitPaymentChannel;
    @Enumerated(EnumType.STRING)
    private TransactionType lastDebitTransactionType;
    @Column(precision = 100, scale = 10)
    private BigDecimal lastDebitAmount;
    private String aPin;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "account_holder_fk")
    private List<AccountHolder> accountHolders;
}