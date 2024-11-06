package com.spay.wallet.transaction.entities;

import com.spay.wallet.account.entities.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "top_up_waiting_transaction")
public class TopUpWaitingTransaction {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String bankName;
    private String accountNumber;
    private Double amount;
    private CurrencyCode currencyCode;
    private String secretNo;
    private String receiverAccount;
}